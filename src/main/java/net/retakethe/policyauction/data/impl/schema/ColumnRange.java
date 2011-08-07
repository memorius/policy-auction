package net.retakethe.policyauction.data.impl.schema;

import java.util.UUID;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

/**
 * Cassandra column ranges where the name is not specified.
 *
 * @param <K> the key type of the column family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the column name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <V> the column value type of the column, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public class ColumnRange<K, N, V> extends Column<K, N, V> {

    public ColumnRange(ColumnFamily<K> columnFamily, Class<N> nameType,
            Serializer<N> nameSerializer, Class<V> valueType,
            Serializer<V> valueSerializer) {
        super(columnFamily, nameType, nameSerializer, valueType, valueSerializer);
    }

    public void addInsertion(Mutator<K> mutator, K key, N name, V value) {
        mutator.addInsertion(key, getColumnFamily().getName(),
                HFactory.createColumn(name, value, getNameSerializer(), getValueSerializer()));
    }
}
