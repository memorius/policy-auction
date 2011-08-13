package net.retakethe.policyauction.data.impl.schema.timestamp;

import net.retakethe.policyauction.data.impl.schema.family.BaseColumnFamily;

/**
 * Timestamp supplier provided for a {@link BaseColumnFamily}.
 *
 * @param <T> the Timestamp variety created by this factory
 * @author Nick Clarke
 */
public interface TimestampFactory<T extends Timestamp> {

    /**
     * Create a new Timestamp value for "now". This may be based on the current system time, for example.
     *
     * @return new Timestamp
     */
    T createCurrentTimestamp();

    /**
     * Create a Timestamp from the long timestamp value returned by Cassandra.
     *
     * @param cassandraValue the long timestamp value from the cassandra column or subcolumn
     * @return new Timestamp for this value
     * @see Timestamp#getCassandraValue()
     */
    T fromCassandraTimestamp(long cassandraValue);
}
