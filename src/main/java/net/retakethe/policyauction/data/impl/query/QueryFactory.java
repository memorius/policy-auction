package net.retakethe.policyauction.data.impl.query;

import java.util.List;

import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import net.retakethe.policyauction.data.impl.schema.Column;
import net.retakethe.policyauction.data.impl.schema.ColumnFamily;
import net.retakethe.policyauction.util.Functional;

/**
 * Creation of various Hector query objects
 *
 * @author Nick Clarke
 */
public final class QueryFactory {

    /**
     * Create a query to return a list of specific columns for one or more rows specified by key.
     * The columns may contain different value types.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param cf the ColumnFamily owning the columns
     * @param columns columns to retrieve, can be empty,
     *      must be columns belonging to the specified ColumnFamily.
     */
    public static <K, N> VariableValueTypedMultiGetSliceQuery<K, N> createVariableValueTypedMultiGetSliceQuery(
            Keyspace ks, ColumnFamily<K> cf, List<Column<K, N, ?>> columns) {
        return new VariableValueTypedMultiGetSliceQueryImpl<K, N>(ks, cf, columns);
    }

    /**
     * Create a query to return a list of specific columns for one or more rows specified by key.
     * The columns may contain different value types.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param cf the ColumnFamily owning the columns
     */
    public static <K, N> VariableValueTypedMultiGetSliceQuery<K, N> createVariableValueTypedMultiGetSliceQuery(
            Keyspace ks, ColumnFamily<K> cf,
            Serializer<N> nameSerializer,
            N start, N finish, boolean reversed, int count) {
        return new VariableValueTypedMultiGetSliceQueryImpl<K, N>(ks, cf, nameSerializer, start, finish, reversed, count);
    }

    /**
     * Create a whole-column-family query to return a list of specific columns for each row.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param <V> column value type
     * @param cf the ColumnFamily owning the columns
     * @param columns columns for {@link RangeSlicesQuery#setColumnNames(Object...)}, can be empty,
     *      must be columns belonging to the specified ColumnFamily.
     */
    public static <K, N, V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(Keyspace ks, final ColumnFamily<K> cf,
            List<Column<K, N, V>> columns) {
        N[] columnNames = getColumnNamesResolved(cf, columns);

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
                .setColumnNames(columnNames);
    }

    /**
     * Create a whole-column-family query to return a range of columns for each row.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param <V> column value type
     * @param cf the ColumnFamily owning the columns
     */
    public static <K, N, V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(Keyspace ks, ColumnFamily<K> cf,
            Serializer<N> nameSerializer, Serializer<V> valueSerializer,
            N start, N finish, boolean reversed, int count) {
        return HFactory.createRangeSlicesQuery(ks, cf.getKeySerializer(),
                nameSerializer, valueSerializer)
                .setColumnFamily(cf.getName())
                .setRange(start, finish, reversed, count);
    }

    /**
     * Get names for a list of columns and validate that they belong to the same column family.
     *
     * @param cf the ColumnFamily owning the columns
     * @param columns columns to retrieve, can be empty,
     *      must be columns belonging to the specified ColumnFamily.
     * @throws IllegalArgumentException if any columns don't belong to this {@link ColumnFamily}.
     */
    protected static <K, N, V> N[] getColumnNamesResolved(final ColumnFamily<K> cf, List<Column<K, N, V>> columns) {
        List<N> columnNames = Functional.map(columns, new Functional.Converter<Column<K, N, V>, N>() {
            @Override
            public N convert(Column<K, N, V> column) {
                if (column.getColumnFamily() != cf) {
                    throw new IllegalArgumentException("Column '" + column.getName() + "' is from column family '"
                            + column.getColumnFamily().getName() + "', expected column family '" + cf.getName() + "'");
                }
                return column.getName();
            }
        });

        return toArray(columnNames);
    }

    /**
     * Get names for a list of columns and validate that they belong to the same column family.
     *
     * @param cf the ColumnFamily owning the columns
     * @param columns columns to retrieve, can be empty,
     *      must be columns belonging to the specified ColumnFamily.
     * @throws IllegalArgumentException if any columns don't belong to this {@link ColumnFamily}.
     */
    protected static <K, N> N[] getColumnNamesUnresolved(final ColumnFamily<K> cf, List<Column<K, N, ?>> columns) {
        List<N> columnNames = Functional.map(columns, new Functional.Converter<Column<K, N, ?>, N>() {
                @Override
                public N convert(Column<K, N, ?> column) {
                    if (column.getColumnFamily() != cf) {
                        throw new IllegalArgumentException("Column '" + column.getName() + "' is from column family '"
                                + column.getColumnFamily().getName() + "', expected column family '" + cf.getName() + "'");
                    }
                    return column.getName();
                }
            });

        return toArray(columnNames);
    }

    private static <N> N[] toArray(List<N> columnNames) {
        @SuppressWarnings("unchecked") // Generic array creation
        N[] columnNamesArray = columnNames.toArray((N[]) new Object[columnNames.size()]);

        return columnNamesArray;
    }
}
