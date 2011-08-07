package net.retakethe.policyauction.data.impl.schema;

import java.util.List;
import java.util.UUID;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import net.retakethe.policyauction.data.impl.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.QueryFactory;
import net.retakethe.policyauction.data.impl.query.VariableValueTypedMultiGetSliceQuery;
import net.retakethe.policyauction.data.impl.query.VariableValueTypedRangeSlicesQuery;
import net.retakethe.policyauction.data.impl.query.VariableValueTypedSliceQuery;
import net.retakethe.policyauction.data.impl.schema.Schema.SchemaKeyspace;

/**
 * Schema definition and query creation for Cassandra Column Families.
 *
 * @param <K> the row key type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 * @param <N> the column name type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public abstract class ColumnFamily<K, N> extends BaseColumnFamily<K> {

    private final Class<N> columnNameType;
    private final Serializer<N> columnNameSerializer;

    protected ColumnFamily(SchemaKeyspace keyspace, String name, Class<K> keyType, Serializer<K> keySerializer,
            Class<N> columnNameType, Serializer<N> columnNameSerializer) {
        super(keyspace, name, keyType, keySerializer);
        this.columnNameType = columnNameType;
        this.columnNameSerializer = columnNameSerializer;
    }

    public Class<N> getColumnNameType() {
        return columnNameType;
    }

    public Serializer<N> getColumnNameSerializer() {
        return columnNameSerializer;
    }

    /**
     * @param columns columns for {@link SliceQuery#setColumnNames(Object...)},
     *      must be columns belonging to this ColumnFamily.
     */
    public VariableValueTypedSliceQuery<K, N> createVariableValueTypedSliceQuery(KeyspaceManager keyspaceManager,
            List<NamedColumn<K, N, ?>> columns, K key) {
        return QueryFactory.createVariableValueTypedSliceQuery(keyspaceManager, this, columns, key);
    }

    /**
     * @param columns columns for {@link MultigetSliceQuery#setColumnNames(Object...)},
     *      must be columns belonging to this ColumnFamily.
     */
    public VariableValueTypedMultiGetSliceQuery<K, N> createVariableValueTypedMultiGetSliceQuery(
            KeyspaceManager keyspaceManager,
            List<NamedColumn<K, N, ?>> columns) {
        return QueryFactory.createVariableValueTypedMultiGetSliceQuery(keyspaceManager, this, columns);
    }

    /**
     * @param columns columns for {@link RangeSlicesQuery#setColumnNames(Object...)},
     *      must be columns belonging to this ColumnFamily.
     */
    public VariableValueTypedRangeSlicesQuery<K, N> createVariableValueTypedRangeSlicesQuery(
            KeyspaceManager keyspaceManager,
            List<NamedColumn<K, N, ?>> columns) {
        return QueryFactory.createVariableValueTypedRangeSlicesQuery(keyspaceManager, this, columns);
    }

    /**
     * @param columns columns for {@link RangeSlicesQuery#setColumnNames(Object...)},
     *      must be columns belonging to this ColumnFamily.
     */
    public <V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(KeyspaceManager keyspaceManager,
            List<NamedColumn<K, N, V>> columns) {
        return QueryFactory.createRangeSlicesQuery(keyspaceManager, this, columns);
    }

    public <V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(KeyspaceManager keyspaceManager,
            ColumnRange<K, N, V> columnRange,
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
