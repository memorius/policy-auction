package net.retakethe.policyauction.data.impl.query.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HSuperColumn;
import net.retakethe.policyauction.data.impl.query.api.UnresolvedVariableValueTypedColumn;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedColumn;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSubcolumn;

public class VariableValueTypedSupercolumnImpl<SN, N> implements VariableValueTypedSupercolumn<SN, N> {

    private final HSuperColumn<SN, N, Object> wrappedSupercolumn;
    private final List<UnresolvedVariableValueTypedColumn<N>> columns;
    private final Map<N, HColumn<N, Object>> columnsByName;

    public VariableValueTypedSupercolumnImpl(HSuperColumn<SN, N, Object> wrappedSupercolumn) {
        this.wrappedSupercolumn = wrappedSupercolumn;

        List<HColumn<N, Object>> wrappedColumns = wrappedSupercolumn.getColumns();
        int size = wrappedColumns.size();
        columns = new ArrayList<UnresolvedVariableValueTypedColumn<N>>(size);
        columnsByName = new HashMap<N, HColumn<N, Object>>(size);

        for (HColumn<N, Object> wrappedColumn : wrappedColumns) {
            columns.add(new UnresolvedVariableValueTypedColumnImpl<N>(wrappedColumn));
            columnsByName.put(wrappedColumn.getName(), wrappedColumn);
        }
    }

    @Override
    public SN getSupercolumnName() {
        return wrappedSupercolumn.getName();
    }

    @Override
    public List<UnresolvedVariableValueTypedColumn<N>> getSubcolumns() {
        return Collections.unmodifiableList(columns);
    }

    @Override
    public <V> VariableValueTypedColumn<N, V> getSubcolumn(NamedSubcolumn<N, V> subcolumn) {
        return getSubcolumnByName(subcolumn.getName(), subcolumn.getValueSerializer());
    }

    @Override
    public <V> VariableValueTypedColumn<N, V> getSubcolumnByName(N subcolumnName, Serializer<V> valueSerializer) {
        HColumn<N, Object> wrappedColumn = columnsByName.get(subcolumnName);
        if (wrappedColumn == null) {
            return null;
        }
        return new VariableValueTypedColumnImpl<N, V>(wrappedColumn, valueSerializer);
    }
}