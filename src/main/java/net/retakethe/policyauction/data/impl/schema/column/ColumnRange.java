package net.retakethe.policyauction.data.impl.schema.column;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.impl.MutatorWrapperInternal;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;

/**
 * Cassandra column ranges where there isn't a single column name.
 *
 * @param <K> the key type of the column family, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <N> the column name type of the column, e.g. {@link UUID} or {@link String}  or {@link Integer} etc.
 * @param <V> the column value type of the column, e.g. {@link UUID} or {@link String} or {@link Integer} etc.
 *
 * @author Nick Clarke
 */
public class ColumnRange<K, N, V> extends Column<K, N, V> {

    public ColumnRange(ColumnFamily<K, N> columnFamily, Type<V> valueType) {
        super(columnFamily, valueType);
    }

    public void addColumnInsertion(MutatorWrapper<K> m, K key, N name, V value) {
        ((MutatorWrapperInternal<K>) m).addColumnInsertion(key, this, name, value);
    }

    public void addColumnDeletion(MutatorWrapper<K> m, K key, N name) {
        ((MutatorWrapperInternal<K>) m).addColumnDeletion(key, this, name);
    }
}
