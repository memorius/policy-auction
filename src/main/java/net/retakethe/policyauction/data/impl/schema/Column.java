package net.retakethe.policyauction.data.impl.schema;

import java.util.UUID;

import me.prettyprint.hector.api.Serializer;

/**
 * Base class for Cassandra named columns and column ranges.
 *
 * @param <K> the key type of the column family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the column name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <V> the column value type of the column, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public abstract class Column<K, N, V> {

    private final Class<N> nameType;
    private final Class<V> valueType;
    private final Serializer<N> nameSerializer;
    private final Serializer<V> valueSerializer;
    private final ColumnFamily<K> columnFamily;

    protected Column(ColumnFamily<K> columnFamily, Class<N> nameType,
            Serializer<N> nameSerializer, Class<V> valueType,
            Serializer<V> valueSerializer) {
        this.columnFamily = columnFamily;
        this.nameType = nameType;
        this.valueType = valueType;
        this.nameSerializer = nameSerializer;
        this.valueSerializer = valueSerializer;
    }

    public ColumnFamily<K> getColumnFamily() {
        return columnFamily;
    }

    public Class<N> getNameType() {
        return nameType;
    }

    public Class<V> getValueType() {
        return valueType;
    }

    public Serializer<N> getNameSerializer() {
        return nameSerializer;
    }

    public Serializer<V> getValueSerializer() {
        return valueSerializer;
    }
}
