package net.retakethe.policyauction.data.impl.query.impl;

import net.retakethe.policyauction.data.impl.query.api.SuperRow;
import net.retakethe.policyauction.data.impl.query.api.SuperSlice;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

public class SuperRowImpl<K, T extends Timestamp, SN, N>
        implements SuperRow<K, T, SN, N> {

    private final me.prettyprint.hector.api.beans.SuperRow<K, SN, N, Object> wrappedRow;

    public SuperRowImpl(me.prettyprint.hector.api.beans.SuperRow<K, SN, N, Object> wrappedRow) {
        this.wrappedRow = wrappedRow;
    }

    @Override
    public K getKey() {
        return wrappedRow.getKey();
    }

    @Override
    public SuperSlice<T, SN, N> getSuperSlice() {
        return new SuperSliceImpl<T, SN, N>(wrappedRow.getSuperSlice());
    }
}
