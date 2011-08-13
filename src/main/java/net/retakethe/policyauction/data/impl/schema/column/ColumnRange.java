package net.retakethe.policyauction.data.impl.schema.column;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.impl.MutatorWrapperInternal;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * Cassandra column ranges where there isn't a single column name.
 *
 * @param <K> the key type of the column family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the column name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <V> the column value type of the column, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public class ColumnRange<K, T extends Timestamp, N, V> extends Column<K, T, N, V> {

    public ColumnRange(ColumnFamily<K, T, N> columnFamily, Type<V> valueType) {
        super(columnFamily, valueType);
    }

    public void addColumnInsertion(MutatorWrapper<K, T> m, K key, N name, V value) {
        ((MutatorWrapperInternal<K, T>) m).addColumnInsertion(key, this, name, value);
    }

    public void addColumnDeletion(MutatorWrapper<K, T> m, K key, N name) {
        ((MutatorWrapperInternal<K, T>) m).addColumnDeletion(key, this, name);
    }
}
