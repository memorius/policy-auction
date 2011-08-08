package net.retakethe.policyauction.data.impl.schema;

import java.util.UUID;

import me.prettyprint.hector.api.Serializer;
import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;

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

    public ColumnRange(ColumnFamily<K, N> columnFamily,
            Class<V> valueType, Serializer<V> valueSerializer) {
        super(columnFamily, valueType, valueSerializer);
    }

    public void addColumnInsertion(MutatorWrapper<K> m, K key, N name, V value) {
        m.addColumnInsertion(key, this, name, value);
    }
}
