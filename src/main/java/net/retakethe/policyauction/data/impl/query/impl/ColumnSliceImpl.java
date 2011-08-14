package net.retakethe.policyauction.data.impl.query.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.prettyprint.hector.api.beans.HColumn;
import net.retakethe.policyauction.data.impl.query.api.ColumnResult;
import net.retakethe.policyauction.data.impl.query.api.ColumnSlice;
import net.retakethe.policyauction.data.impl.query.api.UnresolvedColumnResult;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 */
public class ColumnSliceImpl<T extends Timestamp, N> implements ColumnSlice<T, N> {

    private final me.prettyprint.hector.api.beans.ColumnSlice<N, Object> wrappedColumnSlice;
    private final List<UnresolvedColumnResult<N>> columns;

    public ColumnSliceImpl(me.prettyprint.hector.api.beans.ColumnSlice<N, Object> wrappedColumnSlice) {
        this.wrappedColumnSlice = wrappedColumnSlice;

        List<HColumn<N, Object>> wrappedColumns = wrappedColumnSlice.getColumns();
        int size = wrappedColumns.size();
        columns = new ArrayList<UnresolvedColumnResult<N>>(size);

        for (HColumn<N, Object> wrappedColumn : wrappedColumns) {
            columns.add(new UnresolvedColumnResultImpl<N>(wrappedColumn));
        }
    }

    @Override
    public List<UnresolvedColumnResult<N>> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    @Override
    public <V> ColumnResult<T, N, V> getColumn(NamedColumn<?, T, N, V> column) {
        HColumn<N, Object> wrappedColumn = wrappedColumnSlice.getColumnByName(column.getName());
        if (wrappedColumn == null) {
            return null;
        }
        return new ColumnResultImpl<T, N, V>(wrappedColumn, column.getColumnFamily(),
                column.getValueSerializer());
    }

    @Override
    public <V> ColumnResult<T, N, V> getColumn(ColumnRange<?, T, N, V> column, N columnName) {
        HColumn<N, Object> wrappedColumn = wrappedColumnSlice.getColumnByName(columnName);
        if (wrappedColumn == null) {
            return null;
        }
        return new ColumnResultImpl<T, N, V>(wrappedColumn, column.getColumnFamily(),
                column.getValueSerializer());
    }
}
