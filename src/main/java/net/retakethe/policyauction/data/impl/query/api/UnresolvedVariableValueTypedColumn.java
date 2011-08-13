package net.retakethe.policyauction.data.impl.query.api;


/**
 * NamedColumn where the value type is not specified hence value is not accessible.
 *
 * @param <N> column name type
 * @author Nick Clarke
 */
public interface UnresolvedVariableValueTypedColumn<N> {

    N getName();
}
