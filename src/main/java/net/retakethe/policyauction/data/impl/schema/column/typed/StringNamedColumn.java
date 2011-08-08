package net.retakethe.policyauction.data.impl.schema.column.typed;

import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;

/**
 * @author Nick Clarke
 */
public class StringNamedColumn<K, V> extends NamedColumn<K, String, V> {

    public StringNamedColumn(String name, ColumnFamily<K, String> columnFamily, Type<V> valueType) {
        super(name, columnFamily, valueType);
    }
}
