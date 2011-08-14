package net.retakethe.policyauction.data.impl.query.impl;

import java.util.ArrayList;
import java.util.List;

import me.prettyprint.hector.api.beans.HSuperColumn;
import net.retakethe.policyauction.data.impl.query.api.SuperSlice;
import net.retakethe.policyauction.data.impl.query.api.SupercolumnResult;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

public class SuperSliceImpl<T extends Timestamp, SN, N>
        implements SuperSlice<T, SN, N> {

    private final me.prettyprint.hector.api.beans.SuperSlice<SN, N, Object> wrappedSlice;

    public SuperSliceImpl(me.prettyprint.hector.api.beans.SuperSlice<SN, N, Object> wrappedSlice) {
        this.wrappedSlice = wrappedSlice;
    }

    @Override
    public List<SupercolumnResult<T, SN, N>> getSuperColumns() {
        List<HSuperColumn<SN, N, Object>> wrappedSupercolumns = wrappedSlice.getSuperColumns();
        List<SupercolumnResult<T, SN, N>> supercolumns =
                new ArrayList<SupercolumnResult<T, SN, N>>(wrappedSupercolumns.size());
        for (HSuperColumn<SN, N, Object> wrappedSupercolumn : wrappedSupercolumns) {
            supercolumns.add(new SupercolumnResultImpl<T, SN, N>(wrappedSupercolumn));
        }
        return supercolumns;
    }

    @Override
    public SupercolumnResult<T, SN, N> getSupercolumn(NamedSupercolumn<?, T, SN, N> supercolumn) {
        HSuperColumn<SN, N, Object> wrappedSupercolumn = wrappedSlice.getColumnByName(supercolumn.getName());
        if (wrappedSupercolumn == null) {
            return null;
        }
        return new SupercolumnResultImpl<T, SN, N>(wrappedSupercolumn);
    }

    @Override
    public SupercolumnResult<T, SN, N> getSupercolumn(SupercolumnRange<?, T, SN, N> supercolumn,
            SN supercolumnName) {
        HSuperColumn<SN, N, Object> wrappedSupercolumn = wrappedSlice.getColumnByName(supercolumnName);
        if (wrappedSupercolumn == null) {
            return null;
        }
        return new SupercolumnResultImpl<T, SN, N>(wrappedSupercolumn);
    }
}
