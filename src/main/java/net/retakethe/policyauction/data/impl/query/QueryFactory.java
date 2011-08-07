package net.retakethe.policyauction.data.impl.query;

import java.util.List;

import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import net.retakethe.policyauction.data.impl.schema.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.NamedColumn;
import net.retakethe.policyauction.util.Functional;

/**
 * Creation of various Hector query objects
 *
 * @author Nick Clarke
 */
public final class QueryFactory {

    /**
     * Create a query to return a list of specific columns for one row specified by key.
     * The columns may contain different value types.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param cf the ColumnFamily owning the columns
     * @param columns columns to retrieve, must not be empty,
     *      must be columns belonging to the specified ColumnFamily.
     */
    public static <K, N> VariableValueTypedSliceQuery<K, N> createVariableValueTypedSliceQuery(
            Keyspace ks, ColumnFamily<K> cf, List<NamedColumn<K, N, ?>> columns, K key) {
        return new VariableValueTypedSliceQueryImpl<K, N>(ks, cf, columns, key);
    }

    /**
     * Create a query to return a range of columns for one row specified by key.
     * The columns may contain different value types.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param cf the ColumnFamily owning the columns
     */
    public static <K, N> VariableValueTypedSliceQuery<K, N> createVariableValueTypedSliceQuery(
            Keyspace ks, ColumnFamily<K> cf,
            ColumnRange<K, N, ?> columnRange, N start, N finish,
            boolean reversed, int count, K key) {
        return new VariableValueTypedSliceQueryImpl<K, N>(ks, cf, columnRange, start, finish, reversed, count, key);
    }

    /**
     * Create a query to return a list of specific columns for one or more rows specified by key.
     * The columns may contain different value types.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param cf the ColumnFamily owning the columns
     * @param columns columns to retrieve, must not be empty,
     *      must be columns belonging to the specified ColumnFamily.
     */
    public static <K, N> VariableValueTypedMultiGetSliceQuery<K, N> createVariableValueTypedMultiGetSliceQuery(
            Keyspace ks, ColumnFamily<K> cf, List<NamedColumn<K, N, ?>> columns) {
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
            ColumnRange<K, N, ?> columnRange, N start, N finish, boolean reversed, int count) {
        return new VariableValueTypedMultiGetSliceQueryImpl<K, N>(ks, cf, columnRange, start, finish, reversed, count);
    }

    /**
     * Create a query to return a list of specific columns for a range of rows specified by key, or all rows.
     * The columns may contain different value types.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param cf the ColumnFamily owning the columns
     * @param columns columns to retrieve, must not be empty,
     *      must be columns belonging to the specified ColumnFamily.
     */
    public static <K, N> VariableValueTypedRangeSlicesQuery<K, N> createVariableValueTypedRangeSlicesQuery(
            Keyspace ks, ColumnFamily<K> cf, List<NamedColumn<K, N, ?>> columns) {
        return new VariableValueTypedRangeSlicesQueryImpl<K, N>(ks, cf, columns);
    }

    /**
     * Create a query to return a range of columns for a range of rows specified by key, or all rows.
     * The columns may contain different value types.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param cf the ColumnFamily owning the columns
     */
    public static <K, N> VariableValueTypedRangeSlicesQuery<K, N> createVariableValueTypedRangeSlicesQuery(
            Keyspace ks, ColumnFamily<K> cf,
            ColumnRange<K, N, ?> columnRange, N start, N finish, boolean reversed, int count) {
        return new VariableValueTypedRangeSlicesQueryImpl<K, N>(ks, cf, columnRange, start, finish, reversed, count);
    }

    /**
     * Create a query to return a list of specific columns for a range of rows specified by key, or all rows.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param <V> column value type
     * @param cf the ColumnFamily owning the columns
     * @param columns columns for {@link RangeSlicesQuery#setColumnNames(Object...)}, must not be empty,
     *      must be columns belonging to the specified ColumnFamily.
     */
    public static <K, N, V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(Keyspace ks, final ColumnFamily<K> cf,
            List<NamedColumn<K, N, V>> columns) {
        if (columns.isEmpty()) {
            throw new IllegalArgumentException("At least one column is required");
        }

        NamedColumn<K, N, V> firstColumn = columns.get(0);
        Serializer<N> nameSerializer = firstColumn.getNameSerializer();
        Serializer<V> valueSerializer = firstColumn.getValueSerializer();

        N[] columnNames = getColumnNamesResolved(cf, columns);

        return HFactory.createRangeSlicesQuery(ks, cf.getKeySerializer(),
                nameSerializer, valueSerializer)
                .setColumnFamily(cf.getName())
                .setColumnNames(columnNames);
    }

    /**
     * Create a query to return a range of columns for a range of rows specified by key, or all rows.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param <V> column value type
     * @param cf the ColumnFamily owning the columns
     */
    public static <K, N, V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(Keyspace ks, ColumnFamily<K> cf,
            ColumnRange<K, N, V> columnRange,
            N start, N finish, boolean reversed, int count) {
        QueryFactory.checkColumnRangeBelongsToColumnFamily(cf, columnRange);

        return HFactory.createRangeSlicesQuery(ks, cf.getKeySerializer(),
                columnRange.getNameSerializer(), columnRange.getValueSerializer())
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
    protected static <K, N, V> N[] getColumnNamesResolved(final ColumnFamily<K> cf, List<NamedColumn<K, N, V>> columns) {
        List<N> columnNames = Functional.map(columns, new Functional.Converter<NamedColumn<K, N, V>, N>() {
            @Override
            public N convert(NamedColumn<K, N, V> column) {
                if (column.getColumnFamily() != cf) {
                    throw new IllegalArgumentException("NamedColumn '" + column.getName() + "' is from column family '"
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
    protected static <K, N> N[] getColumnNamesUnresolved(final ColumnFamily<K> cf, List<NamedColumn<K, N, ?>> columns) {
        List<N> columnNames = Functional.map(columns, new Functional.Converter<NamedColumn<K, N, ?>, N>() {
                @Override
                public N convert(NamedColumn<K, N, ?> column) {
                    if (column.getColumnFamily() != cf) {
                        throw new IllegalArgumentException("NamedColumn '" + column.getName() + "' is from column family '"
                                + column.getColumnFamily().getName() + "', expected column family '" + cf.getName() + "'");
                    }
                    return column.getName();
                }
            });

        return toArray(columnNames);
    }

    protected static <N> void checkColumnRangeBelongsToColumnFamily(ColumnFamily<?> cf,
            ColumnRange<?, N, ?> columnRange) {
        if (columnRange.getColumnFamily() != cf) {
            throw new IllegalArgumentException("ColumnRange is from column family '"
                    + columnRange.getColumnFamily().getName() + "', expected column family '" + cf.getName() + "'");
        }
    }

    private static <N> N[] toArray(List<N> columnNames) {
        @SuppressWarnings("unchecked") // Generic array creation
        N[] columnNamesArray = columnNames.toArray((N[]) new Object[columnNames.size()]);

        return columnNamesArray;
    }
}
