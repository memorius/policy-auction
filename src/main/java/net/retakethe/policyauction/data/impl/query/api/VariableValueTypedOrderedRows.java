package net.retakethe.policyauction.data.impl.query.api;

import java.util.List;

import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @see me.prettyprint.hector.api.beans.OrderedRows
 * @author Nick Clarke
 */
public interface VariableValueTypedOrderedRows<K, T extends Timestamp, N> extends VariableValueTypedRows<K, T, N> {

    /**
     * Preserves rows order
     *
     * @return unmodifiable list
     */
    List<VariableValueTypedRow<K, T, N>> getList();

    VariableValueTypedRow<K, T, N> peekLast();
}
