package net.retakethe.policyauction.data.impl.query.impl;

import java.util.ArrayList;
import java.util.List;

import me.prettyprint.hector.api.beans.HSuperColumn;
import me.prettyprint.hector.api.beans.SuperSlice;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperSlice;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;

public class VariableValueTypedSuperSliceImpl<SN, N> implements VariableValueTypedSuperSlice<SN, N> {

    private final SuperSlice<SN, N, Object> wrappedSlice;

    public VariableValueTypedSuperSliceImpl(SuperSlice<SN, N, Object> wrappedSlice) {
        this.wrappedSlice = wrappedSlice;
    }

    @Override
    public List<VariableValueTypedSupercolumn<SN, N>> getSuperColumns() {
        List<HSuperColumn<SN, N, Object>> wrappedSupercolumns = wrappedSlice.getSuperColumns();
        List<VariableValueTypedSupercolumn<SN, N>> supercolumns =
                new ArrayList<VariableValueTypedSupercolumn<SN, N>>(wrappedSupercolumns.size());
        for (HSuperColumn<SN, N, Object> wrappedSupercolumn : wrappedSupercolumns) {
            supercolumns.add(new VariableValueTypedSupercolumnImpl<SN, N>(wrappedSupercolumn));
        }
        return supercolumns;
    }

    @Override
    public VariableValueTypedSupercolumn<SN, N> getSupercolumn(NamedSupercolumn<?, SN, N> supercolumn) {
        HSuperColumn<SN, N, Object> wrappedSupercolumn = wrappedSlice.getColumnByName(supercolumn.getName());
        if (wrappedSupercolumn == null) {
            return null;
        }
        return new VariableValueTypedSupercolumnImpl<SN, N>(wrappedSupercolumn);
    }

    @Override
    public VariableValueTypedSupercolumn<SN, N> getSupercolumn(SupercolumnRange<?, SN, N> supercolumn,
            SN supercolumnName) {
        HSuperColumn<SN, N, Object> wrappedSupercolumn = wrappedSlice.getColumnByName(supercolumnName);
        if (wrappedSupercolumn == null) {
            return null;
        }
        return new VariableValueTypedSupercolumnImpl<SN, N>(wrappedSupercolumn);
    }
}
