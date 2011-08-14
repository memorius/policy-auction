package net.retakethe.policyauction.data.impl.query.api;

import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @see me.prettyprint.hector.api.beans.SuperRows
 * @author Nick Clarke
 */
public interface SuperRows<K, T extends Timestamp, SN, N> extends Iterable<SuperRow<K, T, SN, N>> {

    SuperRow<K, T, SN, N> getByKey(K key);

    int getCount();
}
