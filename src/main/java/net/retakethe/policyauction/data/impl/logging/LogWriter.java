package net.retakethe.policyauction.data.impl.logging;

import net.retakethe.policyauction.data.api.types.LogMessageID;

/**
 * @author Nick Clarke
 */
public interface LogWriter {

    LogMessageID createCurrentTimeLogMessageID();

    void writeLogMessage(LogMessageID id, long originalTimestamp, String severityLevel, String loggerName,
            String message, Throwable throwable);
}
