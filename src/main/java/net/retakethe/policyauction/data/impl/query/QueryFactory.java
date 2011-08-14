package net.retakethe.policyauction.data.impl.query;

import java.util.List;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.ColumnValueQuery;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedMultigetSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedMultigetSuperSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRangeSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRangeSuperSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSupercolumnQuery;
import net.retakethe.policyauction.data.impl.query.impl.ColumnValueQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.VariableValueTypedMultigetSliceQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.VariableValueTypedMultigetSuperSliceQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.VariableValueTypedRangeSlicesQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.VariableValueTypedRangeSuperSlicesQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.VariableValueTypedSliceQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.VariableValueTypedSuperColumnQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.VariableValueTypedSuperSliceQueryImpl;
import net.retakethe.policyauction.data.impl.schema.column.Column;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.Supercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * Creation of various Hector query objects
 *
 * @author Nick Clarke
 */
public final class QueryFactory {

    private QueryFactory() {}

    public static <K, T extends Timestamp, N, V> ColumnValueQuery<K, T, N, V> createColumnQuery(
            KeyspaceManager keyspaceManager,
            ColumnFamily<K, T, N> cf, K key,
            Column<K, T, N, V> column, N columnName) {
        return new ColumnValueQueryImpl<K, T, N, V>(keyspaceManager.getKeyspace(cf.getKeyspace()),
                cf, key, column, columnName);
    }

    public static <K, T extends Timestamp, SN, N> VariableValueTypedSupercolumnQuery<T, SN, N> createSupercolumnQuery(
            KeyspaceManager keyspaceManager,
            SupercolumnFamily<K, T, SN, N> scf, K key,
            Supercolumn<K, T, SN, N> supercolumn, SN supercolumnName) {
        return new VariableValueTypedSuperColumnQueryImpl<K, T, SN, N>(keyspaceManager.getKeyspace(scf.getKeyspace()),
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
    public static <K, T extends Timestamp, N> VariableValueTypedSliceQuery<K, T, N> createVariableValueTypedSliceQuery(
            KeyspaceManager keyspaceManager, ColumnFamily<K, T, N> cf, K key, List<NamedColumn<K, T, N, ?>> columns) {
        return new VariableValueTypedSliceQueryImpl<K, T, N>(keyspaceManager.getKeyspace(cf.getKeyspace()),
                cf, columns, key);
    }

    /**
     * Create a query to return a list of specific columns for one row specified by key.
     * The columns may contain different value types.
     * @param cf the ColumnFamily owning the columns
     * @param columnRange must be columns belonging to the specified ColumnFamily.
     *
     * @param <K> key type
     * @param <N> column name type
     */
    public static <K, T extends Timestamp, N> VariableValueTypedSliceQuery<K, T, N> createVariableValueTypedSliceQuery(
            KeyspaceManager keyspaceManager, ColumnFamily<K, T, N> cf, K key, ColumnRange<K, T, N, ?> columnRange,
            N start, N finish, boolean reversed, int count) {
        return new VariableValueTypedSliceQueryImpl<K, T, N>(keyspaceManager.getKeyspace(cf.getKeyspace()),
                cf, columnRange, start, finish, reversed, count, key);
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
    public static <K, T extends Timestamp, SN, N> VariableValueTypedSuperSliceQuery<K, T, SN, N> createVariableValueTypedSuperSliceQuery(
            KeyspaceManager keyspaceManager, SupercolumnFamily<K, T, SN, N> scf,
            K key, List<NamedSupercolumn<K, T, SN, N>> supercolumns) {
        return new VariableValueTypedSuperSliceQueryImpl<K, T, SN, N>(keyspaceManager.getKeyspace(scf.getKeyspace()),
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
    public static <K, T extends Timestamp, SN, N> VariableValueTypedSuperSliceQuery<K, T, SN, N> createVariableValueTypedSuperSliceQuery(
            KeyspaceManager keyspaceManager, SupercolumnFamily<K, T, SN, N> scf,
            K key, SupercolumnRange<K, T, SN, N> supercolumnRange,
            SN start, SN finish, boolean reversed, int count) {
        return new VariableValueTypedSuperSliceQueryImpl<K, T, SN, N>(keyspaceManager.getKeyspace(scf.getKeyspace()),
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
    public static <K, T extends Timestamp, N> VariableValueTypedMultigetSliceQuery<K, T, N> createVariableValueTypedMultigetSliceQuery(
            KeyspaceManager keyspaceManager, ColumnFamily<K, T, N> cf, List<NamedColumn<K, T, N, ?>> columns) {
        return new VariableValueTypedMultigetSliceQueryImpl<K, T, N>(keyspaceManager.getKeyspace(cf.getKeyspace()),
                cf, columns);
    }

    /**
     * Create a query to return a list of specific columns for one or more rows specified by key.
     * The columns may contain different value types.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param cf the ColumnFamily owning the columns
     * @param columnRange must be columns belonging to the specified ColumnFamily.
     */
    public static <K, T extends Timestamp, N> VariableValueTypedMultigetSliceQuery<K, T, N> createVariableValueTypedMultigetSliceQuery(
            KeyspaceManager keyspaceManager, ColumnFamily<K, T, N> cf, ColumnRange<K, T, N, ?> columnRange,
            N start, N finish, boolean reversed, int count) {
        return new VariableValueTypedMultigetSliceQueryImpl<K, T, N>(keyspaceManager.getKeyspace(cf.getKeyspace()),
                cf, columnRange, start, finish, reversed, count);
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
    public static <K, T extends Timestamp, SN, N> VariableValueTypedMultigetSuperSliceQuery<K, T, SN, N>
            createVariableValueTypedMultigetSuperSliceQuery(KeyspaceManager keyspaceManager,
                    SupercolumnFamily<K, T, SN, N> scf, List<NamedSupercolumn<K, T, SN, N>> supercolumns) {
        return new VariableValueTypedMultigetSuperSliceQueryImpl<K, T, SN, N>(
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
    public static <K, T extends Timestamp, SN, N> VariableValueTypedMultigetSuperSliceQuery<K, T, SN, N>
            createVariableValueTypedMultigetSuperSliceQuery(KeyspaceManager keyspaceManager,
                    SupercolumnFamily<K, T, SN, N> scf,
                    SupercolumnRange<K, T, SN, N> supercolumnRange,
                    SN start, SN finish, boolean reversed, int count) {
        return new VariableValueTypedMultigetSuperSliceQueryImpl<K, T, SN, N>(
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
    public static <K, T extends Timestamp, N> VariableValueTypedRangeSlicesQuery<K, T, N> createVariableValueTypedRangeSlicesQuery(
            KeyspaceManager keyspaceManager, ColumnFamily<K, T, N> cf, List<NamedColumn<K, T, N, ?>> columns) {
        return new VariableValueTypedRangeSlicesQueryImpl<K, T, N>(keyspaceManager.getKeyspace(cf.getKeyspace()),
                cf, columns);
    }

    /**
     * Create a query to return a list of specific columns for a range of rows specified by key, or all rows.
     * The columns may contain different value types.
     *
     * @param <K> key type
     * @param <N> column name type
     * @param cf the ColumnFamily owning the columns
     * @param columnRange must be columns belonging to the specified ColumnFamily.
     */
    public static <K, T extends Timestamp, N> VariableValueTypedRangeSlicesQuery<K, T, N> createVariableValueTypedRangeSlicesQuery(
            KeyspaceManager keyspaceManager, ColumnFamily<K, T, N> cf, ColumnRange<K, T, N, ?> columnRange,
            N start, N finish, boolean reversed, int count) {
        return new VariableValueTypedRangeSlicesQueryImpl<K, T, N>(keyspaceManager.getKeyspace(cf.getKeyspace()),
                cf, columnRange, start, finish, reversed, count);
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
    public static <K, T extends Timestamp, N, V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(
            KeyspaceManager keyspaceManager,
            ColumnFamily<K, T, N> cf,
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
    public static <K, T extends Timestamp, SN, N> VariableValueTypedRangeSuperSlicesQuery<K, T, SN, N>
            createVariableValueTypedRangeSuperSlicesQuery(KeyspaceManager keyspaceManager,
                    SupercolumnFamily<K, T, SN, N> scf, List<NamedSupercolumn<K, T, SN, N>> supercolumns) {
        return new VariableValueTypedRangeSuperSlicesQueryImpl<K, T, SN, N>(
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
    public static <K, T extends Timestamp, SN, N> VariableValueTypedRangeSuperSlicesQuery<K, T, SN, N>
            createVariableValueTypedRangeSuperSlicesQuery(KeyspaceManager keyspaceManager,
                    SupercolumnFamily<K, T, SN, N> scf, SupercolumnRange<K, T, SN, N> supercolumnRange,
                    SN start, SN finish, boolean reversed, int count) {
        return new VariableValueTypedRangeSuperSlicesQueryImpl<K, T, SN, N>(
                keyspaceManager.getKeyspace(scf.getKeyspace()),
                scf, supercolumnRange, start, finish, reversed, count);
    }
}
