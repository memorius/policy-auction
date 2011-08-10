package net.retakethe.policyauction.logging;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

        private final LoggingEvent event;

        public LogWriteTask(LoggingEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            try {
                Level level = event.getLevel();
                Object message = event.getMessage();
                ThrowableInformation ti = event.getThrowableInformation();
                logWriter.writeLogMessage(event.timeStamp,
                        (level == null) ? null : level.toString(),
                        event.getLoggerName(),
                        (message == null) ? null : message.toString(),
                        (ti == null) ? null : ti.getThrowable());
            } catch (RuntimeException e) {
                // TODO: ignore logging errors in production
                e.printStackTrace();
            }
        }
    }

    private static final int MAX_WRITE_THREAD_COUNT = 4;
    private static final int MAX_QUEUED_WRITES = 100;

    private LogWriter logWriter;
    private ThreadPoolExecutor writeThreadPool;

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

        // Writes to cassandra via LogWriter are done in a background task
        writeThreadPool = new ThreadPoolExecutor(1, MAX_WRITE_THREAD_COUNT, 2, TimeUnit.MINUTES,
                new ArrayBlockingQueue<Runnable>(MAX_QUEUED_WRITES), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    protected void append(LoggingEvent event) {
        if (logWriter == null) {
            // Not initialized yet.
            return;
        }

        try {
            writeThreadPool.execute(new LogWriteTask(event));
        } catch (RuntimeException e) {
            // TODO: ignore logging errors in production
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        writeThreadPool.shutdown();
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
