package net.retakethe.policyauction.data.impl.query.api;


/**
 * NamedColumn where the value type is specified hence value can be deserialized.
 *
 * @param <N> column name type
 * @param <V> column value type
 * @author Nick Clarke
 */
public interface VariableValueTypedColumn<N, V> extends UnresolvedVariableValueTypedColumn<N> {

    V getValue();
}
