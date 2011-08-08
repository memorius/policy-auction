package net.retakethe.policyauction.data.impl.query.impl;

import java.util.ArrayList;
import java.util.List;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import net.retakethe.policyauction.data.impl.query.api.UnresolvedVariableValueTypedColumn;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedColumn;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedColumnSlice;
import net.retakethe.policyauction.data.impl.schema.NamedColumn;

/**
 * @author Nick Clarke
 */
public class VariableValueTypedColumnSliceImpl<N> implements VariableValueTypedColumnSlice<N> {

    private final ColumnSlice<N, Object> wrappedColumnSlice;

    public VariableValueTypedColumnSliceImpl(ColumnSlice<N, Object> wrappedColumnSlice) {
        this.wrappedColumnSlice = wrappedColumnSlice;
    }

    @Override
    public List<UnresolvedVariableValueTypedColumn<N>> getColumns() {
        List<HColumn<N, Object>> wrappedColumns = wrappedColumnSlice.getColumns();
        List<UnresolvedVariableValueTypedColumn<N>> columns =
                new ArrayList<UnresolvedVariableValueTypedColumn<N>>(wrappedColumns.size());
        for (HColumn<N, Object> wrappedColumn : wrappedColumns) {
            columns.add(new UnresolvedVariableValueTypedColumnImpl<N>(wrappedColumn));
        }
        return columns;
    }

    @Override
    public <V> VariableValueTypedColumn<N, V> getColumn(NamedColumn<?, N, V> column) {
        return getColumnByName(column.getName(), column.getValueSerializer());
    }

    @Override
    public <V> VariableValueTypedColumn<N, V> getColumnByName(N columnName, Serializer<V> valueSerializer) {
        HColumn<N, Object> wrappedColumn = wrappedColumnSlice.getColumnByName(columnName);
        if (wrappedColumn == null) {
            return null;
        }
        return new VariableValueTypedColumnImpl<N, V>(wrappedColumn, valueSerializer);
    }
}
