package net.retakethe.policyauction.data.impl.schema.family;

import java.util.List;
import java.util.UUID;

import me.prettyprint.hector.api.Serializer;
import net.retakethe.policyauction.data.impl.query.QueryFactory;
import net.retakethe.policyauction.data.impl.query.api.ColumnQuery;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.MultigetSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.RangeSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.SliceQuery;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.column.Column;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.timestamp.TimestampFactory;

/**
 * Schema definition and query creation for Cassandra Column Families.
 *
 * @param <K> the row key type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 * @param <N> the column name type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public abstract class ColumnFamily<K, T extends Timestamp, N> extends BaseColumnFamily<K, T> {

    private final Type<N> columnNameType;

    protected ColumnFamily(SchemaKeyspace keyspace, String name, Type<K> keyType,
            TimestampFactory<T> timestampFactory, Type<N> columnNameType) {
        super(keyspace, name, keyType, timestampFactory);
        this.columnNameType = columnNameType;
    }

    public Type<N> getColumnNameType() {
        return columnNameType;
    }

    public Serializer<N> getColumnNameSerializer() {
        return columnNameType.getSerializer();
    }

    public <V> ColumnQuery<K, T, N, V> createColumnQuery(KeyspaceManager keyspaceManager, K key, Column<K, T, N, V> column,
            N columnName) {
        return QueryFactory.createColumnQuery(keyspaceManager, this, key, column, columnName);
    }

    public SliceQuery<K, T, N> createSliceQuery(KeyspaceManager keyspaceManager, K key,
            ColumnRange<K, T, N, ?> columnRange, N start, N finish, boolean reversed, int count) {
        return QueryFactory.createSliceQuery(keyspaceManager, this, key, columnRange,
                start, finish, reversed, count);
    }

    /**
     * @param columns must be columns belonging to this ColumnFamily.
     */
    public SliceQuery<K, T, N> createSliceQuery(KeyspaceManager keyspaceManager,
            K key, List<NamedColumn<K, T, N, ?>> columns) {
        return QueryFactory.createSliceQuery(keyspaceManager, this, key, columns);
    }

    public MultigetSliceQuery<K, T, N> createMultigetSliceQuery(KeyspaceManager keyspaceManager,
            ColumnRange<K, T, N, ?> columnRange, N start, N finish, boolean reversed, int count) {
        return QueryFactory.createMultigetSliceQuery(keyspaceManager, this, columnRange,
                start, finish, reversed, count);
    }

    /**
     * @param columns must be columns belonging to this ColumnFamily.
     */
    public MultigetSliceQuery<K, T, N> createMultigetSliceQuery(
            KeyspaceManager keyspaceManager, List<NamedColumn<K, T, N, ?>> columns) {
        return QueryFactory.createMultigetSliceQuery(keyspaceManager, this, columns);
    }

    /**
     * @param columns must be columns belonging to this ColumnFamily.
     */
    public RangeSlicesQuery<K, T, N> createRangeSlicesQuery(
            KeyspaceManager keyspaceManager,
            List<NamedColumn<K, T, N, ?>> columns) {
        return QueryFactory.createRangeSlicesQuery(keyspaceManager, this, columns);
    }

    public RangeSlicesQuery<K, T, N> createRangeSlicesQuery(
            KeyspaceManager keyspaceManager,
            ColumnRange<K, T, N, ?> columnRange,
            N start, N finish, boolean reversed, int count) {
        return QueryFactory.createRangeSlicesQuery(keyspaceManager, this, columnRange,
                start, finish, reversed, count);
    }
}
