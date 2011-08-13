package net.retakethe.policyauction.data.impl.schema.column.typed;

import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 */
public class StringNamedColumn<K, T extends Timestamp, V> extends NamedColumn<K, T, String, V> {

    public StringNamedColumn(String name, ColumnFamily<K, T, String> columnFamily, Type<V> valueType) {
        super(name, columnFamily, valueType);
    }
}
