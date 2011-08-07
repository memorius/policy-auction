package net.retakethe.policyauction.data.impl.schema;

import me.prettyprint.cassandra.serializers.StringSerializer;


/**
 * @author Nick Clarke
 */
public class StringStringColumn<K> extends StringNamedColumn<K, String> {

    public StringStringColumn(String name, ColumnFamily<K> columnFamily) {
        super(name, columnFamily, String.class, StringSerializer.get());
    }
}
