package net.retakethe.policyauction.data.impl.schema.family;

import java.util.List;
import java.util.UUID;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import net.retakethe.policyauction.data.impl.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.QueryFactory;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedMultiGetSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRangeSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSliceQuery;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.types.Type;

/**
 * Schema definition and query creation for Cassandra Column Families.
 *
 * @param <K> the row key type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 * @param <N> the column name type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public abstract class ColumnFamily<K, N> extends BaseColumnFamily<K> {

    private final Type<N> columnNameType;

    protected ColumnFamily(SchemaKeyspace keyspace, String name, Type<K> keyType, Type<N> columnNameType) {
        super(keyspace, name, keyType);
        this.columnNameType = columnNameType;
    }

    public Type<N> getColumnNameType() {
        return columnNameType;
    }

    public Serializer<N> getColumnNameSerializer() {
        return columnNameType.getSerializer();
    }

    /**
     * @param columns columns for {@link SliceQuery#setColumnNames(Object...)},
     *      must be columns belonging to this ColumnFamily.
     */
    public VariableValueTypedSliceQuery<K, N> createVariableValueTypedSliceQuery(KeyspaceManager keyspaceManager,
            K key, List<NamedColumn<K, N, ?>> columns) {
        return QueryFactory.createVariableValueTypedSliceQuery(keyspaceManager, this, key, columns);
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
