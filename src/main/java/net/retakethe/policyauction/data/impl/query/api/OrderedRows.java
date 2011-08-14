package net.retakethe.policyauction.data.impl.query.api;

import java.util.List;

import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @see me.prettyprint.hector.api.beans.OrderedRows
 * @author Nick Clarke
 */
public interface OrderedRows<K, T extends Timestamp, N> extends Rows<K, T, N> {

    /**
     * Preserves rows order
     *
     * @return unmodifiable list
     */
    List<Row<K, T, N>> getList();

    Row<K, T, N> peekLast();
}
