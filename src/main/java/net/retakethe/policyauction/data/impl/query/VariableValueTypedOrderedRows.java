package net.retakethe.policyauction.data.impl.query;

import java.util.List;

/**
 * @see me.prettyprint.hector.api.beans.OrderedRows
 * @author Nick Clarke
 */
public interface VariableValueTypedOrderedRows<K, N> extends VariableValueTypedRows<K, N> {

    /**
     * Preserves rows order
     *
     * @return unmodifiable list
     */
    List<VariableValueTypedRow<K, N>> getList();

    VariableValueTypedRow<K, N> peekLast();
}
