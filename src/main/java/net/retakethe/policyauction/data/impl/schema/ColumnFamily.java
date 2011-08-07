package net.retakethe.policyauction.data.impl.schema;

import java.util.List;
import java.util.UUID;

import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import net.retakethe.policyauction.data.impl.query.QueryFactory;
import net.retakethe.policyauction.data.impl.query.VariableValueTypedMultiGetSliceQuery;
import net.retakethe.policyauction.data.impl.query.VariableValueTypedRangeSlicesQuery;
import net.retakethe.policyauction.data.impl.query.VariableValueTypedSliceQuery;

/**
 * Schema definition and query creation for Cassandra Column Families.
 *
 * @param <K> the row key type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 * @param <N> the column name type of the column family, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public abstract class ColumnFamily<K, N> extends BaseColumnFamily<K> {

    private final Class<N> nameType;
    private final Serializer<N> nameSerializer;

    protected ColumnFamily(String name, Class<K> keyType, Serializer<K> keySerializer,
            Class<N> nameType, Serializer<N> nameSerializer) {
        super(name, keyType, keySerializer);
        this.nameType = nameType;
        this.nameSerializer = nameSerializer;
    }

    public Class<N> getNameType() {
        return nameType;
    }

    public Serializer<N> getNameSerializer() {
        return nameSerializer;
    }

    /**
     * @param columns columns for {@link SliceQuery#setColumnNames(Object...)},
     *      must be columns belonging to this ColumnFamily.
     */
    public VariableValueTypedSliceQuery<K, N> createVariableValueTypedSliceQuery(Keyspace ks,
            List<NamedColumn<K, N, ?>> columns, K key) {
        return QueryFactory.createVariableValueTypedSliceQuery(ks, this, columns, key);
    }

    /**
     * @param columns columns for {@link MultigetSliceQuery#setColumnNames(Object...)},
     *      must be columns belonging to this ColumnFamily.
     */
    public VariableValueTypedMultiGetSliceQuery<K, N> createVariableValueTypedMultiGetSliceQuery(Keyspace ks,
            List<NamedColumn<K, N, ?>> columns) {
        return QueryFactory.createVariableValueTypedMultiGetSliceQuery(ks, this, columns);
    }

    /**
     * @param columns columns for {@link RangeSlicesQuery#setColumnNames(Object...)},
     *      must be columns belonging to this ColumnFamily.
     */
    public VariableValueTypedRangeSlicesQuery<K, N> createVariableValueTypedRangeSlicesQuery(Keyspace ks,
            List<NamedColumn<K, N, ?>> columns) {
        return QueryFactory.createVariableValueTypedRangeSlicesQuery(ks, this, columns);
    }

    /**
     * @param columns columns for {@link RangeSlicesQuery#setColumnNames(Object...)},
     *      must be columns belonging to this ColumnFamily.
     */
    public <V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(Keyspace ks, List<NamedColumn<K, N, V>> columns) {
        return QueryFactory.createRangeSlicesQuery(ks, this, columns);
    }

    public <V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(Keyspace ks, ColumnRange<K, N, V> columnRange,
            N start, N finish, boolean reversed, int count) {
        return QueryFactory.createRangeSlicesQuery(ks, this, columnRange, start, finish, reversed, count);
    }

    public <V> RangeSlicesQuery<K, N, V> createRangeSlicesQuery(Keyspace ks, Serializer<V> valueSerializer,
            N start, N finish, boolean reversed, int count) {
        return QueryFactory.createRangeSlicesQuery(ks, this, valueSerializer, start, finish, reversed, count);
    }
}
