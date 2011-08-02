package net.retakethe.policyauction.data.impl.schema;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Serializer;

/**
 * @author Nick Clarke
 */
public class StringNamedColumn<K, V> extends Column<K, String, V> {

    public StringNamedColumn(String name,
            Class<K> keyType, ColumnFamily<K> columnFamily,
            Class<V> valueType, Serializer<V> valueSerializer) {
        super(name, keyType, columnFamily, String.class, StringSerializer.get(), valueType, valueSerializer);
    }
}
