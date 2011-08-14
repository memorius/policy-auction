package net.retakethe.policyauction.data.impl.query.api;

import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;


/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.beans.Row
 */
public interface Row<K, T extends Timestamp, N> {

    K getKey();

    ColumnSlice<T, N> getColumnSlice();
}
