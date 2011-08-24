package net.retakethe.policyauction.data.impl.schema.family;

import net.retakethe.policyauction.data.impl.query.api.ColumnQuery;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.Mutator;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.timestamp.TimestampFactory;
import net.retakethe.policyauction.data.impl.schema.value.Value;

/**
 * Used for single-row lookup indexes such as those in "misc" table.
 *
 * @author Nick Clarke
 */
public class SingleRowNamedColumnFamily<K, T extends Timestamp, N> extends ColumnFamily<K, T, N>
        implements SingleRowColumnFamily<K> {

    private final K key;

    public SingleRowNamedColumnFamily(SchemaKeyspace keyspace, String name, K key, Type<K> keyType,
            TimestampFactory<T> timestampFactory, Type<N> columnNameType) {
        super(keyspace, name, keyType, timestampFactory, columnNameType);
        this.key = key;
    }

    @Override
    public K getKey() {
        return key;
    }

    public <V> void addColumnInsertion(Mutator<K, T> m, NamedColumn<K, T, N, V> column, Value<T, V> value) {
        column.addColumnInsertion(m, key, value);
    }

    /**
     * Delete column, using current timestamp
     */
    public void addColumnDeletion(Mutator<K, T> m, NamedColumn<K, T, N, ?> column) {
        column.addColumnDeletion(m, key);
    }

    /**
     * Delete column, using specified timestamp
     */
    public void addColumnDeletion(Mutator<K, T> m, NamedColumn<K, T, N, ?> column, T timestamp) {
        column.addColumnDeletion(m, key, timestamp);
    }
    
    public <V> ColumnQuery<K, T, N, V> createColumnQuery(KeyspaceManager keyspaceManager, NamedColumn<K, T, N, V> column) {
        return createColumnQuery(keyspaceManager, key, column, column.getName());
    }
}
