package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.query.Query;

/**
 * @see me.prettyprint.hector.api.query.RangeSuperSlicesQuery
 * @author Nick Clarke
 */
public interface VariableValueTypedRangeSuperSlicesQuery<K, SN, N>
        extends Query<VariableValueTypedOrderedSuperRows<K, SN, N>> {

    VariableValueTypedRangeSuperSlicesQuery<K, SN, N> setKeys(K start, K end);

    VariableValueTypedRangeSuperSlicesQuery<K, SN, N> setRowCount(int rowCount);
}
