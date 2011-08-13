package net.retakethe.policyauction.data.impl.schema.timestamp;

/**
 * Base class for Timestamp implementations.
 *
 * @author Nick Clarke
 */
public abstract class AbstractTimestamp implements Timestamp {
    private static final long serialVersionUID = 0L;

    private final long timestamp;

    protected AbstractTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public long getCassandraValue() {
        return timestamp;
    }
}
