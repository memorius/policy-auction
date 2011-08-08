package net.retakethe.policyauction.data.impl.query.api;

import java.util.List;

import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;

/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.beans.ColumnSlice
 */
public interface VariableValueTypedColumnSlice<N> {

   /**
    * Note the column values are not accessible through this method because they are allowed to be different types
    * in each column.
    */
   List<UnresolvedVariableValueTypedColumn<N>> getColumns();

   <V> VariableValueTypedColumn<N, V> getColumn(NamedColumn<?, N, V> column);

   <V> VariableValueTypedColumn<N, V> getColumn(ColumnRange<?, N, V> column, N columnName);
}
