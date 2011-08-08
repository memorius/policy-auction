package net.retakethe.policyauction.data.impl.schema.column;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;

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

    private final Class<V> valueType;
    private final Serializer<V> valueSerializer;
    private final ColumnFamily<K, N> columnFamily;

    protected Column(ColumnFamily<K, N> columnFamily,
            Class<V> valueType, Serializer<V> valueSerializer) {
        this.columnFamily = columnFamily;
        this.valueType = valueType;
        this.valueSerializer = valueSerializer;
    }

    public ColumnFamily<K, N> getColumnFamily() {
        return columnFamily;
    }

    public Class<V> getValueType() {
        return valueType;
    }

    public Serializer<V> getValueSerializer() {
        return valueSerializer;
    }
}
