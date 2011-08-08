package net.retakethe.policyauction.data.impl.schema.column.typed;

import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import me.prettyprint.hector.api.Serializer;

/**
 * @author Nick Clarke
 */
public class StringNamedColumn<K, V> extends NamedColumn<K, String, V> {

    public StringNamedColumn(String name,
            ColumnFamily<K, String> columnFamily, Class<V> valueType,
            Serializer<V> valueSerializer) {
        super(name, columnFamily, valueType, valueSerializer);
    }
}
