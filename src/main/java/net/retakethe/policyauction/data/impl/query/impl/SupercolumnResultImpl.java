package net.retakethe.policyauction.data.impl.query.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HSuperColumn;
import net.retakethe.policyauction.data.impl.query.api.UnresolvedColumnResult;
import net.retakethe.policyauction.data.impl.query.api.ColumnResult;
import net.retakethe.policyauction.data.impl.query.api.SupercolumnResult;
import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SubcolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

public class SupercolumnResultImpl<T extends Timestamp, SN, N>
        implements SupercolumnResult<T, SN, N> {

    private final HSuperColumn<SN, N, Object> wrappedSupercolumn;
    private final List<UnresolvedColumnResult<N>> columns;
    private final Map<N, HColumn<N, Object>> columnsByName;

    public SupercolumnResultImpl(HSuperColumn<SN, N, Object> wrappedSupercolumn) {
        this.wrappedSupercolumn = wrappedSupercolumn;

        List<HColumn<N, Object>> wrappedColumns = wrappedSupercolumn.getColumns();
        int size = wrappedColumns.size();
        columns = new ArrayList<UnresolvedColumnResult<N>>(size);
        columnsByName = new HashMap<N, HColumn<N, Object>>(size);

        for (HColumn<N, Object> wrappedColumn : wrappedColumns) {
            columns.add(new UnresolvedColumnResultImpl<N>(wrappedColumn));
            columnsByName.put(wrappedColumn.getName(), wrappedColumn);
        }
    }

    @Override
    public SN getSupercolumnName() {
        return wrappedSupercolumn.getName();
    }

    @Override
    public List<UnresolvedColumnResult<N>> getSubcolumns() {
        return Collections.unmodifiableList(columns);
    }

    @Override
    public <V> ColumnResult<T, N, V> getSubcolumn(NamedSubcolumn<?, T, SN, N, V> subcolumn) {
        HColumn<N, Object> wrappedColumn = columnsByName.get(subcolumn.getName());
        if (wrappedColumn == null) {
            return null;
        }
        return new ColumnResultImpl<T, N, V>(wrappedColumn,
                subcolumn.getSupercolumn().getSupercolumnFamily(),
                subcolumn.getValueSerializer());
    }

    @Override
    public <V> ColumnResult<T, N, V> getSubcolumn(SubcolumnRange<?, T, SN, N, V> subcolumn, N subcolumnName) {
        HColumn<N, Object> wrappedColumn = columnsByName.get(subcolumnName);
        if (wrappedColumn == null) {
            return null;
        }
        return new ColumnResultImpl<T, N, V>(wrappedColumn,
                subcolumn.getSupercolumn().getSupercolumnFamily(),
                subcolumn.getValueSerializer());
    }
}
