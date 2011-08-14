package net.retakethe.policyauction.data.impl.query.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HSuperColumn;
import net.retakethe.policyauction.data.impl.query.api.ColumnResult;
import net.retakethe.policyauction.data.impl.query.api.SupercolumnResult;
import net.retakethe.policyauction.data.impl.query.api.UnresolvedColumnResult;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SubcolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.util.Functional;

public class SupercolumnResultImpl<T extends Timestamp, SN, N>
        implements SupercolumnResult<T, SN, N> {

    private final HSuperColumn<SN, N, Object> wrappedSupercolumn;
    private final Map<N, HColumn<N, Object>> columnsByName;

    public SupercolumnResultImpl(HSuperColumn<SN, N, Object> wrappedSupercolumn) {
        this.wrappedSupercolumn = wrappedSupercolumn;

        List<HColumn<N, Object>> wrappedColumns = wrappedSupercolumn.getColumns();
        columnsByName = new HashMap<N, HColumn<N, Object>>(wrappedColumns.size());

        for (HColumn<N, Object> wrappedColumn : wrappedColumns) {
            columnsByName.put(wrappedColumn.getName(), wrappedColumn);
        }
    }

    @Override
    public SN getSupercolumnName() {
        return wrappedSupercolumn.getName();
    }

    @Override
    public List<UnresolvedColumnResult<N>> getSubcolumns() {
        return Collections.unmodifiableList(
                Functional.map(wrappedSupercolumn.getColumns(),
                        new Functional.Converter<HColumn<N, Object>, UnresolvedColumnResult<N>>() {
                            @Override
                            public UnresolvedColumnResult<N> convert(HColumn<N, Object> wrappedColumn) {
                                return new UnresolvedColumnResultImpl<N>(wrappedColumn);
                            }
                        }));
    }

    @Override
    public <V> List<ColumnResult<T, N, V>> getSubcolumns(SubcolumnRange<?, T, SN, N, V> subcolumnRange) {
        final SupercolumnFamily<?, T, SN, N> scf = subcolumnRange.getSupercolumn().getSupercolumnFamily();
        final Serializer<V> valueSerializer = subcolumnRange.getValueSerializer();
        return Collections.unmodifiableList(
                Functional.map(wrappedSupercolumn.getColumns(),
                        new Functional.Converter<HColumn<N, Object>, ColumnResult<T, N, V>>() {
                            @Override
                            public ColumnResult<T, N, V> convert(HColumn<N, Object> wrappedColumn) {
                                return new ColumnResultImpl<T, N, V>(wrappedColumn, scf, valueSerializer);
                            }
                        }));
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
