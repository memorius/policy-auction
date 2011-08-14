package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.query.Query;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.query.MultigetSliceQuery
 */
public interface MultigetSliceQuery<K, T extends Timestamp, N> extends Query<Rows<K, T, N>> {

    MultigetSliceQuery<K, T, N> setKeys(K... keys);

    MultigetSliceQuery<K, T, N> setKeys(Iterable<K> keys);
}
