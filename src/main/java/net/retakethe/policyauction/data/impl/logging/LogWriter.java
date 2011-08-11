package net.retakethe.policyauction.data.impl.logging;

import java.util.List;

import net.retakethe.policyauction.data.api.types.LogMessageID;

/**
 * @author Nick Clarke
 */
public interface LogWriter {

    LogMessageID createCurrentTimeLogMessageID();

    void writeLogMessageBatch(List<OutboundLogMessage> messages);

    public static class OutboundLogMessage {
    
        private final LogMessageID id;
        private final long originalTimestamp;
        private final String severityLevel;
        private final String loggerName;
        private final String message;
        private final Throwable throwable;
    
        public OutboundLogMessage(LogMessageID id, long originalTimestamp, String severityLevel, String loggerName,
                String message, Throwable throwable) {
            this.id = id;
            this.originalTimestamp = originalTimestamp;
            this.severityLevel = severityLevel;
            this.loggerName = loggerName;
            this.message = message;
            this.throwable = throwable;
        }
    
        public LogMessageID getId() {
            return this.id;
        }
    
        public long getOriginalTimestamp() {
            return this.originalTimestamp;
        }
    
        public String getSeverityLevel() {
            return this.severityLevel;
        }
    
        public String getLoggerName() {
            return this.loggerName;
        }
    
        public String getMessage() {
            return this.message;
        }
    
        public Throwable getThrowable() {
            return this.throwable;
        }
    }
}
