package net.retakethe.policyauction.data.impl.query.api;

import me.prettyprint.hector.api.query.Query;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @see me.prettyprint.hector.api.query.ColumnQuery
 * @author Nick Clarke
 */
public interface ColumnQuery<K, T extends Timestamp, N, V> extends Query<ColumnResult<T, N, V>> {
}
