package net.retakethe.policyauction.data.impl.schema;

import java.util.UUID;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

/**
 * Enum-like class for Cassandra columns with fixed names.
 * <p>
 * (Can't use an actual java enum due to use of generic type parameters - enums don't support this.)
 *
 * @param <N> the column name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <V> the column value type of the column, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public class Column<K, N, V> {

    private final N name;
    private final Class<N> nameType;
    private final Class<V> valueType;
    private final Serializer<N> nameSerializer;
    private final Serializer<V> valueSerializer;
    private final ColumnFamily<K> columnFamily;

    /**
     * @param keyType the ColumnFamily key type.
     *         Not really needed here but Java forces us to supply it for generic type checking at compile time.
     */
    public Column(N name, Class<K> keyType, ColumnFamily<K> columnFamily,
            Class<N> nameType, Serializer<N> nameSerializer,
            Class<V> valueType, Serializer<V> valueSerializer) {
        this.columnFamily = columnFamily;
        this.name = name;
        this.nameType = nameType;
        this.valueType = valueType;
        this.nameSerializer = nameSerializer;
        this.valueSerializer = valueSerializer;
    }

    public ColumnFamily<K> getColumnFamily() {
        return columnFamily;
    }

    public N getName() {
        return name;
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

    public void addInsertion(Mutator<K> mutator, K key, V value) {
        mutator.addInsertion(key, columnFamily.getName(),
                HFactory.createColumn(name, value, nameSerializer, valueSerializer));
    }
}
