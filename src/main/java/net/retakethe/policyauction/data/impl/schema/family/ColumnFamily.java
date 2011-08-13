package net.retakethe.policyauction.data.impl.schema.family;

import java.util.List;
import java.util.UUID;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import net.retakethe.policyauction.data.impl.query.QueryFactory;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedMultigetSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRangeSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSliceQuery;
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

    public <V> ColumnQuery<K, N, V> createColumnQuery(KeyspaceManager keyspaceManager, K key, Column<K, T, N, V> column,
            N columnName) {
        return QueryFactory.createColumnQuery(keyspaceManager, this, key, column, columnName);
    }

    public <V> SliceQuery<K, N, V> createSliceQuery(KeyspaceManager keyspaceManager, K key,
            ColumnRange<K, T, N, V> columnRange, N start, N finish, boolean reversed, int count) {
        return QueryFactory.createSliceQuery(keyspaceManager, this, key, columnRange, start, finish, reversed, count);
    }

    /**
     * @param columns columns for {@link SliceQuery#setColumnNames(Object...)},
     *      must be columns belonging to this ColumnFamily.
     */
    public VariableValueTypedSliceQuery<K, T, N> createVariableValueTypedSliceQuery(KeyspaceManager keyspaceManager,
            K key, List<NamedColumn<K, T, N, ?>> columns) {
        return QueryFactory.createVariableValueTypedSliceQuery(keyspaceManager, this, key, columns);
    }

    public <V> MultigetSliceQuery<K, N, V> createMultigetSliceQuery(KeyspaceManager keyspaceManager,
            ColumnRange<K, T, N, V> columnRange, N start, N finish, boolean reversed, int count) {
        return QueryFactory.createMultigetSliceQuery(keyspaceManager, this, columnRange, start, finish, reversed, count);
    }

    /**
     * @param columns columns for {@link MultigetSliceQuery#setColumnNames(Object...)},
     *      must be columns belonging to this ColumnFamily.
     */
    public VariableValueTypedMultigetSliceQuery<K, T, N> createVariableValueTypedMultigetSliceQuery(
            KeyspaceManager keyspaceManager, List<NamedColumn<K, T, N, ?>> columns) {
        return QueryFactory.createVariableValueTypedMultigetSliceQuery(keyspaceManager, this, columns);
    }

    /**
     * @param columns columns for {@link RangeSlicesQuery#setColumnNames(Object...)},
     *      must be columns belonging to this ColumnFamily.
     */
    public VariableValueTypedRangeSlicesQuery<K, T, N> createVariableValueTypedRangeSlicesQuery(
            KeyspaceManager keyspaceManager,
            List<NamedColumn<K, T, N, ?>> columns) {
        return QueryFactory.createVariableValueTypedRangeSlicesQuery(keyspaceManager, this, columns);
    }

    /**
     * @param columns columns for {@link RangeSlicesQuery#setColumnNames(Object...)},
     *      must be columns belonging to this ColumnFamily.
     */
    public <V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(KeyspaceManager keyspaceManager,
            List<NamedColumn<K, T, N, V>> columns) {
        return QueryFactory.createRangeSlicesQuery(keyspaceManager, this, columns);
    }

    public <V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(KeyspaceManager keyspaceManager,
            ColumnRange<K, T, N, V> columnRange,
            N start, N finish, boolean reversed, int count) {
        return QueryFactory.createRangeSlicesQuery(keyspaceManager, this, columnRange, start, finish, reversed, count);
    }

    public <V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(KeyspaceManager keyspaceManager,
            Serializer<V> valueSerializer,
            N start, N finish, boolean reversed, int count) {
        return QueryFactory.createRangeSlicesQuery(keyspaceManager, this, valueSerializer,
                start, finish, reversed, count);
    }
}
