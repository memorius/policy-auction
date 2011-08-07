package net.retakethe.policyauction.data.impl.schema;

import java.util.UUID;

import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

/**
 * Cassandra columns with fixed names.
 *
 * @param <K> the key type of the column family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the column name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <V> the column value type of the column, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public class NamedColumn<K, N, V> extends Column<K, N, V> {
 
    private final N name;

    public NamedColumn(N name, ColumnFamily<K, N> columnFamily,
            Class<V> valueType, Serializer<V> valueSerializer) {
        super(columnFamily, valueType, valueSerializer);
        this.name = name;
    }

    public N getName() {
        return name;
    }

    public void addInsertion(Mutator<K> mutator, K key, V value) {
        ColumnFamily<K, N> cf = getColumnFamily();
        mutator.addInsertion(key, cf.getName(),
                HFactory.createColumn(name, value, cf.getNameSerializer(), getValueSerializer()));
    }
}
