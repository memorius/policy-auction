package net.retakethe.policyauction.data.impl.schema.column.typed;

import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.types.Type;


/**
 * @author Nick Clarke
 */
public class StringStringColumn<K> extends StringNamedColumn<K, String> {

    public StringStringColumn(String name, ColumnFamily<K, String> columnFamily) {
        super(name, columnFamily, Type.UTF8);
    }
}
