package net.retakethe.policyauction.data.impl.query.impl;

import java.util.Collections;
import java.util.List;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.HColumn;
import net.retakethe.policyauction.data.impl.query.api.ColumnResult;
import net.retakethe.policyauction.data.impl.query.api.ColumnSlice;
import net.retakethe.policyauction.data.impl.query.api.UnresolvedColumnResult;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.util.Functional;

/**
 * @author Nick Clarke
 */
public class ColumnSliceImpl<T extends Timestamp, N> implements ColumnSlice<T, N> {

    private final me.prettyprint.hector.api.beans.ColumnSlice<N, Object> wrappedColumnSlice;

    public ColumnSliceImpl(me.prettyprint.hector.api.beans.ColumnSlice<N, Object> wrappedColumnSlice) {
        this.wrappedColumnSlice = wrappedColumnSlice;
    }

    @Override
    public List<UnresolvedColumnResult<N>> getColumns() {
        return Collections.unmodifiableList(
                Functional.map(wrappedColumnSlice.getColumns(),
                        new Functional.Converter<HColumn<N, Object>, UnresolvedColumnResult<N>>() {
                            @Override
                            public UnresolvedColumnResult<N> convert(HColumn<N, Object> wrappedColumn) {
                                return new UnresolvedColumnResultImpl<N>(wrappedColumn);
                            }
                        }));
    }

    @Override
    public <V> List<ColumnResult<T, N, V>> getColumns(final ColumnRange<?, T, N, V> columnRange) {
        final ColumnFamily<?, T, N> cf = columnRange.getColumnFamily();
        final Serializer<V> valueSerializer = columnRange.getValueSerializer();
        return Collections.unmodifiableList(
                Functional.map(wrappedColumnSlice.getColumns(),
                        new Functional.Converter<HColumn<N, Object>, ColumnResult<T, N, V>>() {
                            @Override
                            public ColumnResult<T, N, V> convert(HColumn<N, Object> wrappedColumn) {
                                return new ColumnResultImpl<T, N, V>(wrappedColumn, cf, valueSerializer);
                            }
                        }));
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
