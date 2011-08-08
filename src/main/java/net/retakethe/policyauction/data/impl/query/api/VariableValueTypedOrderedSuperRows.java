package net.retakethe.policyauction.data.impl.query.api;

import java.util.List;

/**
 * @see me.prettyprint.hector.api.beans.OrderedSuperRows
 * @author Nick Clarke
 */
public interface VariableValueTypedOrderedSuperRows<K, SN, N> extends VariableValueTypedSuperRows<K, SN, N> {

    List<VariableValueTypedSuperRow<K, SN, N>> getList();
}
