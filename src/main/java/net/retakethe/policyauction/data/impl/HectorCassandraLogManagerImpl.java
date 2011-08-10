package net.retakethe.policyauction.data.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import net.retakethe.policyauction.data.api.CassandraLogManager;
import net.retakethe.policyauction.data.api.types.DateAndHour;
import net.retakethe.policyauction.data.api.types.LogMessageID;
import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.api.SupercolumnInserter;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.data.impl.schema.Schema.LogSCF.LogMessageRange;
import net.retakethe.policyauction.data.impl.types.HectorLogMessageIDImpl;
import net.retakethe.policyauction.logging.LogWriter;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * @author Nick Clarke
 */
public class HectorCassandraLogManagerImpl extends AbstractHectorDAOManager
        implements CassandraLogManager, LogWriter {

    private static final String LOCAL_TIME_DATE_PATTERN = "yyyyMMdd HHmmss.SSS Z";

    /**
     * Currently assuming this will never change while running.
     */
    private static final String HOSTNAME = getLocalHostName();

    private final KeyspaceManager keyspaceManager;

    private Set<String> hourBucketsWrittenThisSession;

    public HectorCassandraLogManagerImpl(KeyspaceManager keyspaceManager) {
        this.keyspaceManager = keyspaceManager;
        this.hourBucketsWrittenThisSession = Collections.synchronizedSet(new HashSet<String>());
    }

    @Override
    public void writeLogMessage(long timestamp, String severityLevel, String loggerName, String message,
            Throwable throwable) {
        // Note we intentionally do not use the message timestamp in the log message UUID, since this is likely to give
        // duplicates which overwrite each other - it's from log4j and has only millisecond precision.
        // Hour buckets must be based on message ID since we have to find it from the ID when retrieving.

        UUID uuid = TimeUUIDUtils.getUniqueTimeUUIDinMillis();
        LogMessageID id = new HectorLogMessageIDImpl(uuid);
        DateAndHour key = new DateAndHour(TimeUUIDUtils.getTimeFromUUID(uuid));

        ensureHourBucket(key);

        String fullMessage;
        if (throwable == null) {
            fullMessage = (message == null) ? "" : message;
        } else {
            fullMessage = printMessageAndStackTrace(message, throwable);
        }

        MutatorWrapper<DateAndHour> m = Schema.LOG.createMutator(keyspaceManager);
        SupercolumnInserter<DateAndHour, LogMessageID, String> i = Schema.LOG.createSupercolumnInserter(m, key, id);
        LogMessageRange cols = Schema.LOG.getSupercolumnRange();

        i.addSubcolumnInsertion(cols.LOCAL_TIME,
                DateFormatUtils.format(timestamp, LOCAL_TIME_DATE_PATTERN));
        i.addSubcolumnInsertion(cols.SERVER, HOSTNAME);
        i.addSubcolumnInsertion(cols.LEVEL, (severityLevel == null) ? "" : severityLevel);
        i.addSubcolumnInsertion(cols.LOGGER, (loggerName == null) ? "" : loggerName);
        i.addSubcolumnInsertion(cols.MESSAGE, fullMessage);

        m.execute();
    }

    private void ensureHourBucket(DateAndHour key) {
        String bucket = key.getGMTDateAndHourString();

        // Only need to write each bucket once; unnecessary duplicate writes are harmless though.
        // Note we must only add to the set if we successfully complete the write.
        if (!hourBucketsWrittenThisSession.contains(bucket)) {
            createHourBucket(key);
            hourBucketsWrittenThisSession.add(bucket);
        }
    }

    private void createHourBucket(DateAndHour bucket) {
        MutatorWrapper<String> m = Schema.LOG_HOURS.createMutator(keyspaceManager);
        Schema.LOG_HOURS.addColumnInsertion(m, bucket, DUMMY_VALUE);
        m.execute();
    }

    @Override
    public LogMessageID makeLogMessageID(String asString) {
        return new HectorLogMessageIDImpl(asString);
    }

    private static String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }

    private static String printMessageAndStackTrace(String message, Throwable throwable) {
        StringWriter sw = new StringWriter(2000);
        PrintWriter pw = new PrintWriter(sw, true);
        if (message != null) {
            pw.println(message);
        }
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
