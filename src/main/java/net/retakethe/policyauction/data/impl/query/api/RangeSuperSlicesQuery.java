package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.query.Query;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @see me.prettyprint.hector.api.query.RangeSuperSlicesQuery
 * @author Nick Clarke
 */
public interface RangeSuperSlicesQuery<K, T extends Timestamp, SN, N>
        extends Query<OrderedSuperRows<K, T, SN, N>> {

    RangeSuperSlicesQuery<K, T, SN, N> setKeys(K start, K end);

    RangeSuperSlicesQuery<K, T, SN, N> setRowCount(int rowCount);
}
