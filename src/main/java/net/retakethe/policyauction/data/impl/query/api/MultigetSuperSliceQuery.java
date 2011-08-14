package net.retakethe.policyauction.data.impl.query.api;

import java.util.Collection;

import me.prettyprint.hector.api.query.Query;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.query.MultigetSuperSliceQuery
 */
public interface MultigetSuperSliceQuery<K, T extends Timestamp, SN, N> extends Query<SuperRows<K, T, SN, N>> {

    MultigetSuperSliceQuery<K, T, SN, N> setKeys(K... keys);

    MultigetSuperSliceQuery<K, T, SN, N> setKeys(Collection<K> keys);
}
