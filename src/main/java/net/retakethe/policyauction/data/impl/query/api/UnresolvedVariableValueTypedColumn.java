package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.Serializer;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * NamedColumn where the value type is not specified hence value is not accessible.
 *
 * @param <N> column name type
 * @author Nick Clarke
 */
public interface UnresolvedVariableValueTypedColumn<T extends Timestamp, N> {

    N getName();

    <V> V getValue(Serializer<V> valueSerializer);

    long getClock();
}
