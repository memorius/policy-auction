package net.retakethe.policyauction.data.impl.query.impl;

import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedColumnSlice;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRow;
import me.prettyprint.hector.api.beans.Row;

/**
 * @author Nick Clarke
 */
public class VariableValueTypedRowImpl<K, N> implements VariableValueTypedRow<K, N> {

    private final Row<K, N, Object> wrappedRow;

    public VariableValueTypedRowImpl(Row<K, N, Object> wrappedRow) {
        this.wrappedRow = wrappedRow;
    }

    @Override
    public K getKey() {
        return wrappedRow.getKey();
    }

    @Override
    public VariableValueTypedColumnSlice<N> getColumnSlice() {
        return new VariableValueTypedColumnSliceImpl<N>(wrappedRow.getColumnSlice());
    }
}
