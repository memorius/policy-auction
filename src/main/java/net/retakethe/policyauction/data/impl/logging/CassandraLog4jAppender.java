package net.retakethe.policyauction.data.impl.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.retakethe.policyauction.data.api.types.LogMessageID;
import net.retakethe.policyauction.data.impl.logging.LogWriter.OutboundLogMessage;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * Log4j Appender that logs events to the 'log' column family in our Cassandra database.
 *
 * @author Nick Clarke
 */
public class CassandraLog4jAppender extends AppenderSkeleton {

    private final class LogWriteTask implements Runnable {

        private final List<OutboundLogMessage> messages;

        public LogWriteTask(List<OutboundLogMessage> messages) {
            this.messages = messages;
        }

        @Override
        public void run() {
            try {
                logWriter.writeLogMessageBatch(messages);
            } catch (RuntimeException e) {
                // TODO: ignore logging errors in production
                e.printStackTrace();
            }
        }
    }

    private static final int MAX_WRITE_THREAD_COUNT = 4;
    private static final int MAX_QUEUED_BATCHES = 10;
    private static final int MAX_LOG_MESSAGES_PER_WRITE = 20;
    private static final int MAX_WRITE_WAIT_SECONDS = 60;

    private LogWriter logWriter;
    private final ThreadPoolExecutor writeThreadPool;
    private final ScheduledExecutorService batchWatcher;

    private final Lock batchLock = new ReentrantLock();
    private List<OutboundLogMessage> batch;

    /**
     * Note overall filter level (usually INFO and above) is set in the log4j config file.
     */
    public CassandraLog4jAppender() {
        // Log all WARN and ERROR events from any sources
        addFilter(new Filter() {
            @Override
            public int decide(LoggingEvent event) {
                if (!event.getLevel().isGreaterOrEqual(Level.INFO)) {
                    // WARN, ERROR or worse. Always log.
                    return Filter.ACCEPT;
                }
                // Let later filters decide
                return Filter.NEUTRAL;
            }
        });
        // Log only events from net.retakethe.policyauction loggers, discard the rest
        addFilter(new Filter() {
            @Override
            public int decide(LoggingEvent event) {
                String loggerName = event.getLoggerName();
                if (loggerName != null && loggerName.startsWith("net.retakethe.policyauction")) {
                    return Filter.ACCEPT;
                }
                return Filter.DENY;
            }
        });

        batch = newEmptyBatch();

        // Writes to cassandra via LogWriter are done in a background task
        writeThreadPool = new ThreadPoolExecutor(1, MAX_WRITE_THREAD_COUNT, 2, TimeUnit.MINUTES,
                new ArrayBlockingQueue<Runnable>(MAX_QUEUED_BATCHES), new ThreadPoolExecutor.CallerRunsPolicy());

        batchWatcher = Executors.newScheduledThreadPool(1);
        batchWatcher.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        flushBatch();
                        // Must swallow all exceptions or the scheduler will stop executing us
                    } catch (Throwable ignored) {}
                }
            }, MAX_WRITE_WAIT_SECONDS, MAX_WRITE_WAIT_SECONDS, TimeUnit.SECONDS); 
    }

    /**
     * Called to write a single log message.
     * <p>
     * Note that {@link AppenderSkeleton#doAppend(LoggingEvent)} calls this in a single-threaded manner
     * - it synchronizes; calls are never interleaved.
     */
    @Override
    protected void append(LoggingEvent event) {
        if (logWriter == null) {
            // Not initialized yet.
            return;
        }

        try {
            OutboundLogMessage olm = makeLogMessage(event);

            batchLock.lock();
            try {
                batch.add(olm);
                if (batch.size() >= MAX_LOG_MESSAGES_PER_WRITE) {
                    // If the write threads and queue are saturated, this thread will run the write itself
                    flushBatch();
                }
            } finally {
                batchLock.unlock();
            }
        } catch (RuntimeException e) {
            // TODO: ignore logging errors in production
            e.printStackTrace();
        }
    }

    /**
     * Synchronized for interaction with {@link AppenderSkeleton#doAppend(LoggingEvent)}
     */
    @Override
    public synchronized void close() {
        // We may get closed twice because DAOManagerImpl shuts us down before disconnecting from Cassandra,
        // which may happen before log4j LogManager shutdown. This is harmless.
        if (this.closed) {
            return;
        }
        this.closed = true;

        // Flush any remaining queued messages into the write threads
        batchWatcher.shutdown();
        flushBatch();

        // Wait until all messages are processed through the LogWriter
        writeThreadPool.shutdown();
    }

    private OutboundLogMessage makeLogMessage(LoggingEvent event) {
        // Create the log message ID now so its timestamp is in the same order as the original message timestamp,
        // even though eventual writes will be reordered by being run in background threads
        LogMessageID id = logWriter.createCurrentTimeLogMessageID();

        Level level = event.getLevel();
        Object message = event.getMessage();
        ThrowableInformation ti = event.getThrowableInformation();

        return new OutboundLogMessage(id,
                event.timeStamp,
                (level == null) ? null : level.toString(),
                event.getLoggerName(),
                (message == null) ? null : message.toString(),
                (ti == null) ? null : ti.getThrowable());
    }

    private void flushBatch() {
        List<OutboundLogMessage> toFlush;

        batchLock.lock();
        try {
            toFlush = batch;
            if (toFlush.isEmpty()) {
                return;
            }
            batch = newEmptyBatch();
        } finally {
            batchLock.unlock();
        }

        try {
            writeThreadPool.execute(new LogWriteTask(toFlush));
        } catch (RuntimeException e) {
            // TODO: ignore logging errors in production
            e.printStackTrace();
        }
    }

    private ArrayList<OutboundLogMessage> newEmptyBatch() {
        return new ArrayList<OutboundLogMessage>(MAX_LOG_MESSAGES_PER_WRITE);
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    /**
     * Called once we have set up Cassandra access part-way through startup.
     */
    public synchronized void setLogWriter(LogWriter logWriter) {
        if (this.logWriter != null) {
            throw new IllegalStateException("logWriter has already been set");
        }
        this.logWriter = logWriter;
    }
}
