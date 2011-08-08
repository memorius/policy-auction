package net.retakethe.policyauction.data.impl.query.api;

import java.util.Collection;

import me.prettyprint.hector.api.query.Query;

/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.query.MultigetSuperSliceQuery
 */
public interface VariableValueTypedMultiGetSuperSliceQuery<K, SN, N>
        extends Query<VariableValueTypedSuperRows<K, SN, N>> {

    VariableValueTypedMultiGetSuperSliceQuery<K, SN, N> setKeys(K... keys);

    VariableValueTypedMultiGetSuperSliceQuery<K, SN, N> setKeys(Collection<K> keys);
}
