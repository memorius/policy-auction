package net.retakethe.policyauction.data.impl.schema.column;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.query.api.Mutator;
import net.retakethe.policyauction.data.impl.query.impl.MutatorInternal;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.value.Value;

/**
 * Cassandra columns with fixed names.
 *
 * @param <K> the key type of the column family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the column name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <V> the column value type of the column, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public class NamedColumn<K, T extends Timestamp, N, V> extends Column<K, T, N, V> {
 
    private final N name;

    public NamedColumn(N name, ColumnFamily<K, T, N> columnFamily, Type<V> valueType) {
        super(columnFamily, valueType);
        this.name = name;
    }

    public N getName() {
        return name;
    }

    public void addColumnInsertion(Mutator<K, T> m, K key, Value<T, V> value) {
        ((MutatorInternal<K, T>) m).addColumnInsertion(key, this, getName(), value);
    }

    /**
     * Delete column, using current timestamp
     */
    public void addColumnDeletion(Mutator<K, T> m, K key) {
        ((MutatorInternal<K, T>) m).addColumnDeletion(key, this, getName(),
                getColumnFamily().createCurrentTimestamp());
    }

    /**
     * Delete column, using specified timestamp
     */
    public void addColumnDeletion(Mutator<K, T> m, K key, T timestamp) {
        ((MutatorInternal<K, T>) m).addColumnDeletion(key, this, getName(), timestamp);
    }
}
