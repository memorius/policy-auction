package net.retakethe.policyauction.data.impl.schema.value;

import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 */
public class ValueImpl<T extends Timestamp, V> implements Value<T, V> {

    private final V value;
    private final T timestamp;
    private final Integer timeToLiveSeconds;

    /**
     * Construct without setting TTL.
     */
    public ValueImpl(V value, T timestamp) {
        this(value, timestamp, null);
    }

    /**
     * Construct with TTL.
     *
     * @param timeToLiveSeconds null means don't set ttl
     */
    public ValueImpl(V value, T timestamp, Integer timeToLiveSeconds) {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp must not be null");
        }
        this.value = value;
        this.timestamp = timestamp;
        this.timeToLiveSeconds = timeToLiveSeconds;
    }
    
    /**
     * Get TTL value to set for the column, or null if no TTL should be set
     * <p>
     * This method is in the impl only, not the public API -
     * it's generally not useful when returned in queries (if it is even actually returned).
     */
    public Integer getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    @Override
    public T getTimestamp() {
        return timestamp;
    }

    @Override
    public V getValue() {
        return value;
    }
}
