package net.retakethe.policyauction.data.impl.schema.column.typed;

import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;


/**
 * @author Nick Clarke
 */
public class StringStringColumn<K, T extends Timestamp> extends StringNamedColumn<K, T, String> {

    public StringStringColumn(String name, ColumnFamily<K, T, String> columnFamily) {
        super(name, columnFamily, Type.UTF8);
    }
}
