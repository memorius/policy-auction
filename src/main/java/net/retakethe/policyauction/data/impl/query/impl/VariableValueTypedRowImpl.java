package net.retakethe.policyauction.data.impl.query.impl;

import me.prettyprint.hector.api.beans.Row;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedColumnSlice;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRow;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 */
public class VariableValueTypedRowImpl<K, T extends Timestamp, N> implements VariableValueTypedRow<K, T, N> {

    private final Row<K, N, Object> wrappedRow;

    public VariableValueTypedRowImpl(Row<K, N, Object> wrappedRow) {
        this.wrappedRow = wrappedRow;
    }

    @Override
    public K getKey() {
        return wrappedRow.getKey();
    }

    @Override
    public VariableValueTypedColumnSlice<T, N> getColumnSlice() {
        return new VariableValueTypedColumnSliceImpl<T, N>(wrappedRow.getColumnSlice());
    }
}
