package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.query.Query;

/**
 * @see me.prettyprint.hector.api.query.RangeSlicesQuery
 * @author Nick Clarke
 */
public interface VariableValueTypedRangeSlicesQuery<K, N> extends Query<VariableValueTypedOrderedRows<K, N>> {

    VariableValueTypedRangeSlicesQuery<K, N> setKeys(K start, K end);

    VariableValueTypedRangeSlicesQuery<K, N> setRowCount(int rowCount);

    VariableValueTypedRangeSlicesQuery<K, N> setReturnKeysOnly();
}
