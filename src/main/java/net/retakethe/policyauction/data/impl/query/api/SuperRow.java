package net.retakethe.policyauction.data.impl.query.api;

import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @see me.prettyprint.hector.api.beans.SuperRow
 * @author Nick Clarke
 */
public interface SuperRow<K, T extends Timestamp, SN, N> {

    K getKey();

    SuperSlice<T, SN, N> getSuperSlice();
}
