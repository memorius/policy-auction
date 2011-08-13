package net.retakethe.policyauction.data.impl.schema.value;

import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

public interface Value<T extends Timestamp, V> {

    /**
     * Get the timestamp for the column value.
     *
     * @return timestamp, never null.
     */
    T getTimestamp();

    /**
     * Get the column value.
     *
     * @return column value, never null.
     */
    V getValue();
}
