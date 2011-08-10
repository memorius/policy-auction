package net.retakethe.policyauction.data.impl.logging;

/**
 * @author Nick Clarke
 */
public interface LogWriter {

    void writeLogMessage(long timestamp, String severityLevel, String loggerName, String message, Throwable throwable);
}
