package net.retakethe.policyauction.data.impl.query.impl;

import me.prettyprint.hector.api.beans.SuperRow;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperRow;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperSlice;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

public class VariableValueTypedSuperRowImpl<K, T extends Timestamp, SN, N>
        implements VariableValueTypedSuperRow<K, T, SN, N> {

    private final SuperRow<K, SN, N, Object> wrappedRow;

    public VariableValueTypedSuperRowImpl(SuperRow<K, SN, N, Object> wrappedRow) {
        this.wrappedRow = wrappedRow;
    }

    @Override
    public K getKey() {
        return wrappedRow.getKey();
    }

    @Override
    public VariableValueTypedSuperSlice<T, SN, N> getSuperSlice() {
        return new VariableValueTypedSuperSliceImpl<T, SN, N>(wrappedRow.getSuperSlice());
    }
}
