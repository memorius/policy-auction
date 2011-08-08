package net.retakethe.policyauction.data.impl.query.api;

import java.util.List;

import me.prettyprint.hector.api.Serializer;
import net.retakethe.policyauction.data.impl.schema.subcolumn.NamedSubcolumn;

public interface VariableValueTypedSupercolumn<SN, N> {

    SN getSupercolumnName();

    /**
     * Note the column values are not accessible through this method because they are allowed to be different types
     * in each column.
     */
    List<UnresolvedVariableValueTypedColumn<N>> getSubcolumns();

    <V> VariableValueTypedColumn<N, V> getSubcolumn(NamedSubcolumn<N, V> subcolumn);

    <V> VariableValueTypedColumn<N, V> getSubcolumnByName(N subcolumnName, Serializer<V> valueSerializer);
}
