package net.retakethe.policyauction.data.impl.query.api;

import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.value.Value;


/**
 * NamedColumn where the value type is specified hence value can be deserialized.
 *
 * @param <N> column name type
 * @param <V> column value type
 * @author Nick Clarke
 */
public interface ColumnResult<T extends Timestamp, N, V> extends UnresolvedColumnResult<N> {

    Value<T, V> getValue();
}
