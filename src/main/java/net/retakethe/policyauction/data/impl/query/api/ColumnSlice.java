package net.retakethe.policyauction.data.impl.query.api;

import java.util.List;

import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 * @see me.prettyprint.hector.api.beans.ColumnSlice
 */
public interface ColumnSlice<T extends Timestamp, N> {

   /**
    * Note the column values are not accessible through this method because they are allowed to be different types
    * in each column.
    */
   List<UnresolvedColumnResult<N>> getColumns();

   <V> ColumnResult<T, N, V> getColumn(NamedColumn<?, T, N, V> column);

   <V> ColumnResult<T, N, V> getColumn(ColumnRange<?, T, N, V> column, N columnName);
}
