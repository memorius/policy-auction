package net.retakethe.policyauction.data.impl.query.api;

import java.util.Collection;

import me.prettyprint.hector.api.query.Query;

/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.query.MultigetSuperSliceQuery
 */
public interface VariableValueTypedMultigetSuperSliceQuery<K, SN, N>
        extends Query<VariableValueTypedSuperRows<K, SN, N>> {

    VariableValueTypedMultigetSuperSliceQuery<K, SN, N> setKeys(K... keys);

    VariableValueTypedMultigetSuperSliceQuery<K, SN, N> setKeys(Collection<K> keys);
}
