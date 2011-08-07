package net.retakethe.policyauction.data.impl.query;

import java.util.List;

import me.prettyprint.hector.api.Serializer;
import net.retakethe.policyauction.data.impl.schema.NamedColumn;

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

   <V> VariableValueTypedColumn<N, V> getColumnByName(N columnName, Serializer<V> valueSerializer);
}
