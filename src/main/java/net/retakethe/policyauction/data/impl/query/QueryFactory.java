package net.retakethe.policyauction.data.impl.query;

import java.util.ArrayList;
import java.util.List;

import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import net.retakethe.policyauction.data.impl.schema.Column;
import net.retakethe.policyauction.data.impl.schema.ColumnFamily;

/**
 * Creation of various Hector query objects
 *
 * @author Nick Clarke
 */
public final class QueryFactory {

    /**
     * @param <K> key type
     * @param <N> column name type
     * @param <V> column value type
     * @param cf the ColumnFamily owning the columns
     * @param columns columns for {@link RangeSlicesQuery#setColumnNames(Object...)}, can be empty,
     *      must be columns belonging to the specified ColumnFamily.
     */
    public static <K, N, V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(Keyspace ks, ColumnFamily<K> cf,
            List<Column<K, N, V>> columns) {
        List<N> columnNames = new ArrayList<N>(columns.size());
        for (Column<K, N, V> column : columns) {
            if (column.getColumnFamily() != cf) {
                throw new IllegalArgumentException("Column '" + column.getName() + "' is from column family '"
                        + column.getColumnFamily().getName() + "', expected column family '" + cf.getName() + "'");
            }
            columnNames.add(column.getName());
        }

        @SuppressWarnings("unchecked") // Generic array creation
        N[] columnNamesArray = columnNames.toArray((N[]) new Object[columns.size()]);

        Serializer<N> nameSerializer;
        Serializer<V> valueSerializer;
        if (columns.isEmpty()) {
            // These are required but won't be used
            nameSerializer = new DummySerializer<N>();
            valueSerializer = new DummySerializer<V>();
        } else {
            Column<K, N, V> firstColumn = columns.get(0);
            nameSerializer = firstColumn.getNameSerializer();
            valueSerializer = firstColumn.getValueSerializer();
        }

        return HFactory.createRangeSlicesQuery(ks, cf.getKeySerializer(),
                nameSerializer, valueSerializer)
                .setColumnFamily(cf.getName())
                .setColumnNames(columnNamesArray);
    }
}
