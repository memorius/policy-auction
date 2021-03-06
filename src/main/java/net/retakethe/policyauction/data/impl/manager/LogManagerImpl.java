package net.retakethe.policyauction.data.impl.manager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.retakethe.policyauction.data.api.LogManager;
import net.retakethe.policyauction.data.api.types.DateAndHour;
import net.retakethe.policyauction.data.api.types.LogMessageID;
import net.retakethe.policyauction.data.impl.logging.LogWriter;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.Mutator;
import net.retakethe.policyauction.data.impl.query.api.SubcolumnMutator;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.data.impl.schema.Schema.LogHoursRow;
import net.retakethe.policyauction.data.impl.schema.Schema.LogSCF;
import net.retakethe.policyauction.data.impl.schema.Schema.LogSCF.LogMessageRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.MillisTimestamp;
import net.retakethe.policyauction.data.impl.types.LogMessageIDImpl;
import net.retakethe.policyauction.data.impl.util.UUIDUtils;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * @author Nick Clarke
 */
public class LogManagerImpl extends AbstractDAOManagerImpl
        implements LogManager, LogWriter {

    private static final String LOCAL_TIME_DATE_PATTERN = "yyyyMMdd HHmmss.SSS Z";

    /**
     * Currently assuming this will never change while running.
     */
    private static final String HOSTNAME = getLocalHostName();

    private Set<String> hourBucketsWrittenThisSession;

    public LogManagerImpl(KeyspaceManager keyspaceManager) {
        super(keyspaceManager);
        this.hourBucketsWrittenThisSession = Collections.synchronizedSet(new HashSet<String>());
    }

    @Override
    public LogMessageID createCurrentTimeLogMessageID() {
        return new LogMessageIDImpl();
    }

    @Override
    public void writeLogMessageBatch(List<OutboundLogMessage> messages) {
        if (messages.isEmpty()) {
            return;
        }

        Mutator<DateAndHour, MillisTimestamp> m = Schema.LOG.createMutator(getKeyspaceManager());

        for (OutboundLogMessage message : messages) {
            addMessage(m, message);
        }

        m.execute();
    }

    private void addMessage(Mutator<DateAndHour, MillisTimestamp> m, OutboundLogMessage olm) {
        // Note we intentionally do not use the message timestamp in the log message UUID, since this is likely to give
        // duplicates which overwrite each other - it's from log4j and has only millisecond precision.
        // Hour buckets must be based on message ID since we have to find it from the ID when retrieving.

        LogMessageID id = olm.getId();
        DateAndHour key = new DateAndHour(UUIDUtils.getTimeMillisFromTimeUUID(((LogMessageIDImpl) id).getUUID()));

        ensureHourBucket(key);

        Throwable throwable = olm.getThrowable();
        String message = olm.getMessage();
        String fullMessage;
        if (throwable == null) {
            fullMessage = (message == null) ? "" : message;
        } else {
            fullMessage = printMessageAndStackTrace(message, throwable);
        }

        LogSCF cf = Schema.LOG;
        SubcolumnMutator<DateAndHour, MillisTimestamp, LogMessageID, String> i =
                cf.createSubcolumnMutator(m, key, id);
        LogMessageRange cols = cf.getSupercolumnRange();
        MillisTimestamp ts = cf.createCurrentTimestamp();

        cols.LOCAL_TIME.addSubcolumnInsertion(i,
                cf.createValue(DateFormatUtils.format(olm.getOriginalTimestamp(), LOCAL_TIME_DATE_PATTERN), ts));
        cols.SERVER.addSubcolumnInsertion(i, cf.createValue(HOSTNAME, ts));
        cols.LEVEL.addSubcolumnInsertion(i, cf.createValue(emptyIfNull(olm.getSeverityLevel()), ts));
        cols.LOGGER.addSubcolumnInsertion(i, cf.createValue(emptyIfNull(olm.getLoggerName()), ts));
        cols.MESSAGE.addSubcolumnInsertion(i, cf.createValue(fullMessage, ts));
    }

    private String emptyIfNull(String value) {
        return (value == null) ? "" : value;
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
        LogHoursRow cf = Schema.LOG_HOURS;
        Mutator<String, MillisTimestamp> m = cf.createMutator(getKeyspaceManager());
        cf.addColumnInsertion(m, bucket, cf.createValue(DUMMY_VALUE));
        m.execute();
    }

    @Override
    public LogMessageID makeLogMessageID(String asString) {
        return new LogMessageIDImpl(asString);
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
