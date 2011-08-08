package net.retakethe.policyauction.data.impl.schema.column.typed;

import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;


/**
 * @author Nick Clarke
 */
public class StringStringColumn<K> extends StringNamedColumn<K, String> {

    public StringStringColumn(String name, ColumnFamily<K, String> columnFamily) {
        super(name, columnFamily, Type.UTF8);
    }
}
