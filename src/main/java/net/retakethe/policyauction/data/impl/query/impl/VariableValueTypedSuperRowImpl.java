package net.retakethe.policyauction.data.impl.query.impl;

import me.prettyprint.hector.api.beans.SuperRow;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperRow;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperSlice;

public class VariableValueTypedSuperRowImpl<K, SN, N> implements VariableValueTypedSuperRow<K, SN, N> {

    private final SuperRow<K, SN, N, Object> wrappedRow;

    public VariableValueTypedSuperRowImpl(SuperRow<K, SN, N, Object> wrappedRow) {
        this.wrappedRow = wrappedRow;
    }

    @Override
    public K getKey() {
        return wrappedRow.getKey();
    }

    @Override
    public VariableValueTypedSuperSlice<SN, N> getSuperSlice() {
        return new VariableValueTypedSuperSliceImpl<SN, N>(wrappedRow.getSuperSlice());
    }
}
