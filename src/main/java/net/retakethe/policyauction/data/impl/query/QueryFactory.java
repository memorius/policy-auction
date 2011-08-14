package net.retakethe.policyauction.data.impl.query;

import java.util.List;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.factory.HFactory;
import net.retakethe.policyauction.data.impl.query.api.ColumnQuery;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.MultigetSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.MultigetSuperSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.RangeSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.RangeSuperSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.SliceQuery;
import net.retakethe.policyauction.data.impl.query.api.SuperSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.SupercolumnQuery;
import net.retakethe.policyauction.data.impl.query.impl.ColumnQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.MultigetSliceQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.MultigetSuperSliceQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.RangeSlicesQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.RangeSuperSlicesQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.SliceQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.SuperSliceQueryImpl;
import net.retakethe.policyauction.data.impl.query.impl.SupercolumnQueryImpl;
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

    public static <K, T extends Timestamp, N, V> ColumnQuery<K, T, N, V> createColumnQuery(
            KeyspaceManager keyspaceManager,
            ColumnFamily<K, T, N> cf, K key,
            Column<K, T, N, V> column, N columnName) {
        return new ColumnQueryImpl<K, T, N, V>(keyspaceManager.getKeyspace(cf.getKeyspace()),
                cf, key, column, columnName);
    }

    public static <K, T extends Timestamp, SN, N> SupercolumnQuery<T, SN, N> createSupercolumnQuery(
            KeyspaceManager keyspaceManager,
            SupercolumnFamily<K, T, SN, N> scf, K key,
            Supercolumn<K, T, SN, N> supercolumn, SN supercolumnName) {
        return new SupercolumnQueryImpl<K, T, SN, N>(keyspaceManager.getKeyspace(scf.getKeyspace()),
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
    public static <K, T extends Timestamp, N> SliceQuery<K, T, N> createSliceQuery(
            KeyspaceManager keyspaceManager, ColumnFamily<K, T, N> cf, K key, List<NamedColumn<K, T, N, ?>> columns) {
        return new SliceQueryImpl<K, T, N>(keyspaceManager.getKeyspace(cf.getKeyspace()),
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
    public static <K, T extends Timestamp, N> SliceQuery<K, T, N> createSliceQuery(
            KeyspaceManager keyspaceManager, ColumnFamily<K, T, N> cf, K key, ColumnRange<K, T, N, ?> columnRange,
            N start, N finish, boolean reversed, int count) {
        return new SliceQueryImpl<K, T, N>(keyspaceManager.getKeyspace(cf.getKeyspace()),
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
    public static <K, T extends Timestamp, SN, N> SuperSliceQuery<K, T, SN, N> createSuperSliceQuery(
            KeyspaceManager keyspaceManager, SupercolumnFamily<K, T, SN, N> scf,
            K key, List<NamedSupercolumn<K, T, SN, N>> supercolumns) {
        return new SuperSliceQueryImpl<K, T, SN, N>(keyspaceManager.getKeyspace(scf.getKeyspace()),
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
    public static <K, T extends Timestamp, SN, N> SuperSliceQuery<K, T, SN, N> createSuperSliceQuery(
            KeyspaceManager keyspaceManager, SupercolumnFamily<K, T, SN, N> scf,
            K key, SupercolumnRange<K, T, SN, N> supercolumnRange,
            SN start, SN finish, boolean reversed, int count) {
        return new SuperSliceQueryImpl<K, T, SN, N>(keyspaceManager.getKeyspace(scf.getKeyspace()),
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
    public static <K, T extends Timestamp, N> MultigetSliceQuery<K, T, N> createMultigetSliceQuery(
            KeyspaceManager keyspaceManager, ColumnFamily<K, T, N> cf, List<NamedColumn<K, T, N, ?>> columns) {
        return new MultigetSliceQueryImpl<K, T, N>(keyspaceManager.getKeyspace(cf.getKeyspace()),
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
    public static <K, T extends Timestamp, N> MultigetSliceQuery<K, T, N> createMultigetSliceQuery(
            KeyspaceManager keyspaceManager, ColumnFamily<K, T, N> cf, ColumnRange<K, T, N, ?> columnRange,
            N start, N finish, boolean reversed, int count) {
        return new MultigetSliceQueryImpl<K, T, N>(keyspaceManager.getKeyspace(cf.getKeyspace()),
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
    public static <K, T extends Timestamp, SN, N> MultigetSuperSliceQuery<K, T, SN, N>
            createMultigetSuperSliceQuery(KeyspaceManager keyspaceManager,
                    SupercolumnFamily<K, T, SN, N> scf, List<NamedSupercolumn<K, T, SN, N>> supercolumns) {
        return new MultigetSuperSliceQueryImpl<K, T, SN, N>(
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
    public static <K, T extends Timestamp, SN, N> MultigetSuperSliceQuery<K, T, SN, N>
            createMultigetSuperSliceQuery(KeyspaceManager keyspaceManager,
                    SupercolumnFamily<K, T, SN, N> scf,
                    SupercolumnRange<K, T, SN, N> supercolumnRange,
                    SN start, SN finish, boolean reversed, int count) {
        return new MultigetSuperSliceQueryImpl<K, T, SN, N>(
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
    public static <K, T extends Timestamp, N> RangeSlicesQuery<K, T, N> createRangeSlicesQuery(
            KeyspaceManager keyspaceManager, ColumnFamily<K, T, N> cf, List<NamedColumn<K, T, N, ?>> columns) {
        return new RangeSlicesQueryImpl<K, T, N>(keyspaceManager.getKeyspace(cf.getKeyspace()),
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
    public static <K, T extends Timestamp, N> RangeSlicesQuery<K, T, N> createRangeSlicesQuery(
            KeyspaceManager keyspaceManager, ColumnFamily<K, T, N> cf, ColumnRange<K, T, N, ?> columnRange,
            N start, N finish, boolean reversed, int count) {
        return new RangeSlicesQueryImpl<K, T, N>(keyspaceManager.getKeyspace(cf.getKeyspace()),
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
    public static <K, T extends Timestamp, N, V> me.prettyprint.hector.api.query.RangeSlicesQuery<K, N, V>
            createHectorRangeSlicesQuery(KeyspaceManager keyspaceManager, ColumnFamily<K, T, N> cf,
                    Serializer<V> valueSerializer, N start, N finish, boolean reversed, int count) {
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
    public static <K, T extends Timestamp, SN, N> RangeSuperSlicesQuery<K, T, SN, N>
            createRangeSuperSlicesQuery(KeyspaceManager keyspaceManager,
                    SupercolumnFamily<K, T, SN, N> scf, List<NamedSupercolumn<K, T, SN, N>> supercolumns) {
        return new RangeSuperSlicesQueryImpl<K, T, SN, N>(
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
    public static <K, T extends Timestamp, SN, N> RangeSuperSlicesQuery<K, T, SN, N>
            createRangeSuperSlicesQuery(KeyspaceManager keyspaceManager,
                    SupercolumnFamily<K, T, SN, N> scf, SupercolumnRange<K, T, SN, N> supercolumnRange,
                    SN start, SN finish, boolean reversed, int count) {
        return new RangeSuperSlicesQueryImpl<K, T, SN, N>(
                keyspaceManager.getKeyspace(scf.getKeyspace()),
                scf, supercolumnRange, start, finish, reversed, count);
    }
}
