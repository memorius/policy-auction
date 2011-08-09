package net.retakethe.policyauction.data.impl.schema.column;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;

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

    public NamedColumn(N name, ColumnFamily<K, N> columnFamily, Type<V> valueType) {
        super(columnFamily, valueType);
        this.name = name;
    }

    public N getName() {
        return name;
    }

    public void addColumnInsertion(MutatorWrapper<K> m, K key, V value) {
        m.addColumnInsertion(key, this, value);
    }
}
