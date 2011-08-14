package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.query.Query;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @see me.prettyprint.hector.api.query.RangeSlicesQuery
 * @author Nick Clarke
 */
public interface RangeSlicesQuery<K, T extends Timestamp, N> extends Query<OrderedRows<K, T, N>> {

    RangeSlicesQuery<K, T, N> setKeys(K start, K end);

    RangeSlicesQuery<K, T, N> setRowCount(int rowCount);

    RangeSlicesQuery<K, T, N> setReturnKeysOnly();
}
