package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.query.Query;

/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.query.MultigetSliceQuery
 */
public interface VariableValueTypedMultigetSliceQuery<K, N> extends Query<VariableValueTypedRows<K, N>> {

    VariableValueTypedMultigetSliceQuery<K, N> setKeys(K... keys);

    VariableValueTypedMultigetSliceQuery<K, N> setKeys(Iterable<K> keys);
}
