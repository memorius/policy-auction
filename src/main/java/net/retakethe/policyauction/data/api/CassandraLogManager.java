package net.retakethe.policyauction.data.api;

import net.retakethe.policyauction.data.api.types.LogMessageID;

/**
 * Access to logs stored in Cassandra.
 *
 * @author Nick Clarke
 */
public interface CassandraLogManager {

    LogMessageID makeLogMessageID(String asString);

    // TODO: methods to retrieve logs
}
