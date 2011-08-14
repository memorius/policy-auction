package net.retakethe.policyauction.data.impl.query.api;

import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @see me.prettyprint.hector.api.beans.Rows
 * @author Nick Clarke
 */
public interface Rows<K, T extends Timestamp, N> extends Iterable<Row<K, T, N>> {

    Row<K, T, N> getByKey(K key);

    int getCount();
}
