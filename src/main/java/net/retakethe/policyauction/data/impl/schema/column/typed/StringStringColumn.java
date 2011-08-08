package net.retakethe.policyauction.data.impl.schema.column.typed;

import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import me.prettyprint.cassandra.serializers.StringSerializer;


/**
 * @author Nick Clarke
 */
public class StringStringColumn<K> extends StringNamedColumn<K, String> {

    public StringStringColumn(String name, ColumnFamily<K, String> columnFamily) {
        super(name, columnFamily, String.class, StringSerializer.get());
    }
}
