package net.retakethe.policyauction.data.impl.query.api;

import java.util.List;

import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SubcolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

public interface VariableValueTypedSupercolumn<T extends Timestamp, SN, N> {

    SN getSupercolumnName();

    /**
     * Note the column values are not accessible through this method because they are allowed to be different types
     * in each column.
     */
    List<UnresolvedVariableValueTypedColumn<N>> getSubcolumns();

    <V> VariableValueTypedColumn<T, N, V> getSubcolumn(NamedSubcolumn<?, T, SN, N, V> subcolumn);

    <V> VariableValueTypedColumn<T, N, V> getSubcolumn(SubcolumnRange<?, T, SN, N, V> subcolumn, N subcolumnName);
}
