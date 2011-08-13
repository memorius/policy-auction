package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.query.Query;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @see me.prettyprint.hector.api.query.RangeSlicesQuery
 * @author Nick Clarke
 */
public interface VariableValueTypedRangeSlicesQuery<K, T extends Timestamp, N>
        extends Query<VariableValueTypedOrderedRows<K, T, N>> {

    VariableValueTypedRangeSlicesQuery<K, T, N> setKeys(K start, K end);

    VariableValueTypedRangeSlicesQuery<K, T, N> setRowCount(int rowCount);

    VariableValueTypedRangeSlicesQuery<K, T, N> setReturnKeysOnly();
}
