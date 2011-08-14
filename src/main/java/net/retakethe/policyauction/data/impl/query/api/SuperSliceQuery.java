package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.query.Query;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.query.SuperSliceQuery
 */
public interface SuperSliceQuery<K, T extends Timestamp, SN, N> extends Query<SuperSlice<T, SN, N>> {
}
