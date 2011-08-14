package net.retakethe.policyauction.data.impl.query.api;

import java.util.List;

import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @see me.prettyprint.hector.api.beans.OrderedSuperRows
 * @author Nick Clarke
 */
public interface OrderedSuperRows<K, T extends Timestamp, SN, N> extends SuperRows<K, T, SN, N> {

    List<SuperRow<K, T, SN, N>> getList();
}
