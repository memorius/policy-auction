package net.retakethe.policyauction.data.impl.query.impl;

import net.retakethe.policyauction.data.impl.query.api.ColumnSlice;
import net.retakethe.policyauction.data.impl.query.api.Row;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 */
public class RowImpl<K, T extends Timestamp, N> implements Row<K, T, N> {

    private final me.prettyprint.hector.api.beans.Row<K, N, Object> wrappedRow;

    public RowImpl(me.prettyprint.hector.api.beans.Row<K, N, Object> wrappedRow) {
        this.wrappedRow = wrappedRow;
    }

    @Override
    public K getKey() {
        return wrappedRow.getKey();
    }

    @Override
    public ColumnSlice<T, N> getColumnSlice() {
        return new ColumnSliceImpl<T, N>(wrappedRow.getColumnSlice());
    }
}
