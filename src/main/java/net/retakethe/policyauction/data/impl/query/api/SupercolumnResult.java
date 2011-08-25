package net.retakethe.policyauction.data.impl.query.api;

import java.util.List;

import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SubcolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

public interface SupercolumnResult<T extends Timestamp, SN, N> {

    SN getSupercolumnName();

    /**
     * Note the column values are not accessible through this method because they are allowed to be different types
     * in each column.
     */
    List<UnresolvedColumnResult<N>> getSubcolumns();

    <V> List<ColumnResult<T, N, V>> getSubcolumns(SubcolumnRange<?, T, SN, N, V> subcolumnRange);

    <V> ColumnResult<T, N, V> getSubcolumn(NamedSubcolumn<?, T, SN, N, V> subcolumn);

    <V> ColumnResult<T, N, V> getSubcolumn(SubcolumnRange<?, T, SN, N, V> subcolumnRange, N subcolumnName);
}
