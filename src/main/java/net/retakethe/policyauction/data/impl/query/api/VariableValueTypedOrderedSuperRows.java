package net.retakethe.policyauction.data.impl.query.api;

import java.util.List;

import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @see me.prettyprint.hector.api.beans.OrderedSuperRows
 * @author Nick Clarke
 */
public interface VariableValueTypedOrderedSuperRows<K, T extends Timestamp, SN, N>
        extends VariableValueTypedSuperRows<K, T, SN, N> {

    List<VariableValueTypedSuperRow<K, T, SN, N>> getList();
}
