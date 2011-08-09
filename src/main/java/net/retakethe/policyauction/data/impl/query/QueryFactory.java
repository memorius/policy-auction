package net.retakethe.policyauction.data.impl.query;

import java.util.List;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import net.retakethe.policyauction.data.impl.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedMultiGetSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedMultiGetSuperSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRangeSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRangeSuperSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSupercolumnQuery;
import net.retakethe.policyauction.data.impl.query.impl.VariableValueTypedMultiGetSliceQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.VariableValueTypedMultiGetSuperSliceQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.VariableValueTypedRangeSlicesQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.VariableValueTypedRangeSuperSlicesQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.VariableValueTypedSliceQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.VariableValueTypedSuperColumnQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.VariableValueTypedSuperSliceQueryImpl;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.Supercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.util.Functional;

/**
 * Creation of various Hector query objects
 *
 * @author Nick Clarke
 */
public final class QueryFactory {

    private QueryFactory() {}

    public static <K, N, V> SliceQuery<K, N, V> createSliceQuery(
            KeyspaceManager keyspaceManager, ColumnFamily<K, N> cf, K key,
            ColumnRange<K, N, V> columnRange, N start, N finish, boolean reversed, int count) {
        checkColumnRangeBelongsToColumnFamily(cf, columnRange);

        return HFactory.createSliceQuery(keyspaceManager.getKeyspace(cf.getKeyspace()), cf.getKeySerializer(),
                cf.getColumnNameSerializer(), columnRange.getValueSerializer())
                .setColumnFamily(cf.getName())
                .setRange(start, finish, reversed, count)
                .setKey(key);
    }

    public static <K, N, V> ColumnQuery<K, N, V> createColumnQuery(KeyspaceManager keyspaceManager,
            ColumnFamily<K, N> cf, K key,
            ColumnRange<K, N, V> columnRange, N columnName) {
        checkColumnRangeBelongsToColumnFamily(cf, columnRange);

        return HFactory.createColumnQuery(keyspaceManager.getKeyspace(cf.getKeyspace()), cf.getKeySerializer(),
                cf.getColumnNameSerializer(), columnRange.getValueSerializer())
                .setColumnFamily(cf.getName())
                .setName(columnName)
                .setKey(key);
    }

    public static <K, SN, N> VariableValueTypedSupercolumnQuery<SN, N> createSupercolumnQuery(
            KeyspaceManager keyspaceManager,
            SupercolumnFamily<K, SN, N> scf, K key,
            Supercolumn<K, SN, N> supercolumn, SN supercolumnName) {
        return new VariableValueTypedSuperColumnQueryImpl<K, SN, N>(keyspaceManager.getKeyspace(scf.getKeyspace()),
                scf, supercolumn, supercolumnName, key);
    }

    /**
     * Create a query to return a list of specific columns for one row specified by key.
     * The columns may contain different value types.
     * @param cf the ColumnFamily owning the columns
     * @param columns columns to retrieve,
     *      must be columns belonging to the specified ColumnFamily.
     *
     * @param <K> key type
     * @param <N> column name type
     */
    public static <K, N> VariableValueTypedSliceQuery<K, N> createVariableValueTypedSliceQuery(
            KeyspaceManager keyspaceManager, ColumnFamily<K, N> cf, K key, List<NamedColumn<K, N, ?>> columns) {
        return new VariableValueTypedSliceQueryImpl<K, N>(keyspaceManager.getKeyspace(cf.getKeyspace()),
                cf, columns, key);
    }

    /**
     * Create a query to return all subcolumns for a list of specific supercolumns for one row specified by key.
     * The subcolumns may contain different value types.
     * @param scf the SupercolumnFamily owning the supercolumns
     * @param supercolumns supercolumns to retrieve,
     *      must be supercolumns belonging to the specified SupercolumnFamily.
     *
     * @param <K> key type
     * @param <SN> supercolumn name type
     * @param <N> subcolumn name type
     */
    public static <K, SN, N> VariableValueTypedSuperSliceQuery<K, SN, N> createVariableValueTypedSuperSliceQuery(
            KeyspaceManager keyspaceManager, SupercolumnFamily<K, SN, N> scf,
            K key, List<NamedSupercolumn<K, SN, N>> supercolumns) {
        return new VariableValueTypedSuperSliceQueryImpl<K, SN, N>(keyspaceManager.getKeyspace(scf.getKeyspace()),
                scf, key, supercolumns);
    }

    /**
     * Create a query to return all subcolumns for a range of supercolumns for one row specified by key.
     * The subcolumns may contain different value types.
     *
     * @param <K> key type
     * @param <SN> supercolumn name type
     * @param <N> subcolumn name type
     * @param scf the SupercolumnFamily owning the supercolumns
     */
    public static <K, SN, N> VariableValueTypedSuperSliceQuery<K, SN, N> createVariableValueTypedSuperSliceQuery(
            KeyspaceManager keyspaceManager, SupercolumnFamily<K, SN, N> scf,
            K key, SupercolumnRange<K, SN, N> supercolumnRange,
            SN start, SN finish, boolean reversed, int count) {
        return new VariableValueTypedSuperSliceQueryImpl<K, SN, N>(keyspaceManager.getKeyspace(scf.getKeyspace()),
                scf, key, supercolumnRange, start, finish, reversed, count);
    }

    /**
     * Create a query to return a list of specific columns for one or more rows specified by key.
     * The columns may contain different value types.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param cf the ColumnFamily owning the columns
     * @param columns columns to retrieve,
     *      must be columns belonging to the specified ColumnFamily.
     */
    public static <K, N> VariableValueTypedMultiGetSliceQuery<K, N> createVariableValueTypedMultiGetSliceQuery(
            KeyspaceManager keyspaceManager, ColumnFamily<K, N> cf, List<NamedColumn<K, N, ?>> columns) {
        return new VariableValueTypedMultiGetSliceQueryImpl<K, N>(keyspaceManager.getKeyspace(cf.getKeyspace()),
                cf, columns);
    }

    /**
     * Create a query to return all subcolumns from a list of specific supercolumns for one or more rows specified by key.
     * The subcolumns may contain different value types.
     *
     * @param <K> key type
     * @param <SN> supercolumn name type
     * @param <N> subcolumn name type
     * @param scf the SupercolumnFamily owning the supercolumns
     * @param supercolumns supercolumns to retrieve,
     *      must be supercolumns belonging to the specified SupercolumnFamily.
     */
    public static <K, SN, N> VariableValueTypedMultiGetSuperSliceQuery<K, SN, N>
            createVariableValueTypedMultiGetSuperSliceQuery(KeyspaceManager keyspaceManager,
                    SupercolumnFamily<K, SN, N> scf, List<NamedSupercolumn<K, SN, N>> supercolumns) {
        return new VariableValueTypedMultiGetSuperSliceQueryImpl<K, SN, N>(
                keyspaceManager.getKeyspace(scf.getKeyspace()), scf, supercolumns);
    }

    /**
     * Create a query to return all subcolumns from a range of supercolumns for one or more rows specified by key.
     * The subcolumns may contain different value types.
     *
     * @param <K> key type
     * @param <SN> supercolumn name type
     * @param <N> subcolumn name type
     * @param scf the SupercolumnFamily owning the supercolumns
     */
    public static <K, SN, N> VariableValueTypedMultiGetSuperSliceQuery<K, SN, N>
            createVariableValueTypedMultiGetSuperSliceQuery(KeyspaceManager keyspaceManager,
                    SupercolumnFamily<K, SN, N> scf,
                    SupercolumnRange<K, SN, N> supercolumnRange,
                    SN start, SN finish, boolean reversed, int count) {
        return new VariableValueTypedMultiGetSuperSliceQueryImpl<K, SN, N>(
                keyspaceManager.getKeyspace(scf.getKeyspace()), scf, supercolumnRange, start, finish, reversed, count);
    }

    /**
     * Create a query to return a list of specific columns for a range of rows specified by key, or all rows.
     * The columns may contain different value types.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param cf the ColumnFamily owning the columns
     * @param columns columns to retrieve,
     *      must be columns belonging to the specified ColumnFamily.
     */
    public static <K, N> VariableValueTypedRangeSlicesQuery<K, N> createVariableValueTypedRangeSlicesQuery(
            KeyspaceManager keyspaceManager, ColumnFamily<K, N> cf, List<NamedColumn<K, N, ?>> columns) {
        return new VariableValueTypedRangeSlicesQueryImpl<K, N>(keyspaceManager.getKeyspace(cf.getKeyspace()),
                cf, columns);
    }

    /**
     * Create a query to return a list of specific columns for a range of rows specified by key, or all rows.
     * All values must have the same type.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param <V> column value type
     * @param cf the ColumnFamily owning the columns
     * @param columns columns for {@link RangeSlicesQuery#setColumnNames(Object...)}, must not be empty,
     *      must be columns belonging to the specified ColumnFamily.
     */
    public static <K, N, V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(KeyspaceManager keyspaceManager,
            ColumnFamily<K, N> cf,
            List<NamedColumn<K, N, V>> columns) {
        if (columns.isEmpty()) {
            throw new IllegalArgumentException("At least one column is required");
        }

        NamedColumn<K, N, V> firstColumn = columns.get(0);
        Serializer<V> valueSerializer = firstColumn.getValueSerializer();

        N[] columnNames = getColumnNamesResolved(cf, columns);

        return HFactory.createRangeSlicesQuery(keyspaceManager.getKeyspace(cf.getKeyspace()), cf.getKeySerializer(),
                cf.getColumnNameSerializer(), valueSerializer)
                .setColumnFamily(cf.getName())
                .setColumnNames(columnNames);
    }

    /**
     * Create a query to return a range of columns for a range of rows specified by key, or all rows.
     * All values must have the same type.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param <V> column value type
     * @param cf the ColumnFamily owning the columns
     */
    public static <K, N, V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(KeyspaceManager keyspaceManager,
            ColumnFamily<K, N> cf,
            ColumnRange<K, N, V> columnRange,
            N start, N finish, boolean reversed, int count) {
        checkColumnRangeBelongsToColumnFamily(cf, columnRange);

        return HFactory.createRangeSlicesQuery(keyspaceManager.getKeyspace(cf.getKeyspace()), cf.getKeySerializer(),
                cf.getColumnNameSerializer(), columnRange.getValueSerializer())
                .setColumnFamily(cf.getName())
                .setRange(start, finish, reversed, count);
    }

    /**
     * Create a query to return a range of columns for a range of rows specified by key, or all rows.
     * All values must have the same type.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param <V> column value type
     * @param cf the ColumnFamily owning the columns
     */
    public static <K, N, V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(KeyspaceManager keyspaceManager,
            ColumnFamily<K, N> cf,
            Serializer<V> valueSerializer,
            N start, N finish, boolean reversed, int count) {
        return HFactory.createRangeSlicesQuery(keyspaceManager.getKeyspace(cf.getKeyspace()), cf.getKeySerializer(),
                cf.getColumnNameSerializer(), valueSerializer)
                .setColumnFamily(cf.getName())
                .setRange(start, finish, reversed, count);
    }

    /**
     * Create a query to return all subcolumns for a list of specific supercolumns for a range of rows specified by key, 
     * or all rows.
     * The subcolumns may contain different value types.
     *
     * @param <K> key type
     * @param <SN> supercolumn name type
     * @param <N> subcolumn name type
     * @param scf the SupercolumnFamily owning the supercolumns
     * @param supercolumns supercolumns to retrieve,
     *      must be supercolumns belonging to the specified SupercolumnFamily.
     */
    public static <K, SN, N> VariableValueTypedRangeSuperSlicesQuery<K, SN, N>
            createVariableValueTypedRangeSuperSlicesQuery(KeyspaceManager keyspaceManager,
                    SupercolumnFamily<K, SN, N> scf, List<NamedSupercolumn<K, SN, N>> supercolumns) {
        return new VariableValueTypedRangeSuperSlicesQueryImpl<K, SN, N>(
                keyspaceManager.getKeyspace(scf.getKeyspace()),
                scf, supercolumns);
    }

    /**
     * Create a query to return all subcolumns for a range of supercolumns for a range of rows specified by key, 
     * or all rows.
     * The subcolumns may contain different value types.
     *
     * @param <K> key type
     * @param <SN> supercolumn name type
     * @param <N> subcolumn name type
     * @param scf the SupercolumnFamily owning the supercolumns
     */
    public static <K, SN, N> VariableValueTypedRangeSuperSlicesQuery<K, SN, N>
            createVariableValueTypedRangeSuperSlicesQuery(KeyspaceManager keyspaceManager,
                    SupercolumnFamily<K, SN, N> scf, SupercolumnRange<K, SN, N> supercolumnRange,
                    SN start, SN finish, boolean reversed, int count) {
        return new VariableValueTypedRangeSuperSlicesQueryImpl<K, SN, N>(
                keyspaceManager.getKeyspace(scf.getKeyspace()),
                scf, supercolumnRange, start, finish, reversed, count);
    }
    
    /**
     * Get names for a list of columns and validate that they belong to the same column family.
     *
     * @param cf the ColumnFamily owning the columns
     * @param columns columns to retrieve, can be empty,
     *      must be columns belonging to the specified ColumnFamily.
     * @throws IllegalArgumentException if any columns don't belong to this {@link ColumnFamily}.
     */
    private static <K, N, V> N[] getColumnNamesResolved(final ColumnFamily<K, N> cf,
            List<NamedColumn<K, N, V>> columns) {
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

    private static <N> void checkColumnRangeBelongsToColumnFamily(ColumnFamily<?, N> cf,
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
