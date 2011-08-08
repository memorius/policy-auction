package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.query.Query;

/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.query.MultigetSliceQuery
 */
public interface VariableValueTypedMultiGetSliceQuery<K, N> extends Query<VariableValueTypedRows<K, N>> {

    VariableValueTypedMultiGetSliceQuery<K, N> setKeys(K... keys);

    VariableValueTypedMultiGetSliceQuery<K, N> setKeys(Iterable<K> keys);
}
