package net.retakethe.policyauction.data.impl.schema.timestamp;

import java.io.Serializable;

/**
 * A Cassandra timestamp for column/subcolumn values and mutation operations.
 * <p>
 * Timestamps in Cassandra are just long values, and the largest value wins when there are update conflicts.
 * The semantics (how the timestamp values are created) are up to us; instances of this interface determine the
 * semantics, and each BaseColumnFamily uses only one flavour, so we have type safety between different kinds of
 * timestamps which have different meanings.
 *
 * @author Nick Clarke
 */
public interface Timestamp extends Serializable {
    /**
     * The value sent/received from cassandra in Hector queries.
     * <p>
     * The meaning of this value is unspecified and depends on the specific Timestamp implementation.
     * In particular, it may not be a milliseconds timestamp and may decrease with time instead of increase.
     *
     * @return Cassandra timestamp value with unspecified semantics
     */
    long getCassandraValue();

}
