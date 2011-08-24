package net.retakethe.policyauction.data.impl.schema.family;

import net.retakethe.policyauction.data.impl.query.api.ColumnQuery;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.Mutator;
import net.retakethe.policyauction.data.impl.query.api.SliceQuery;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.timestamp.TimestampFactory;
import net.retakethe.policyauction.data.impl.schema.value.Value;

/**
 * Used for single-row lookup indexes such as those in "misc" table.
 *
 * @author Nick Clarke
 */
public class SingleRowRangeColumnFamily<K, T extends Timestamp, N, V> extends RangeColumnFamily<K, T, N, V>
        implements SingleRowColumnFamily<K> {

    private final K key;

    public SingleRowRangeColumnFamily(SchemaKeyspace keyspace, String name, K key, Type<K> keyType,
            TimestampFactory<T> timestampFactory, Type<N> columnNameType) {
        super(keyspace, name, keyType, timestampFactory, columnNameType);
        this.key = key;
    }

    @Override
    public K getKey() {
        return key;
    }

    public void addColumnInsertion(Mutator<K, T> m, N name, Value<T, V> value) {
        addColumnInsertion(m, key, name, value);
    }

    /**
     * Delete column, using current timestamp
     */
    public void addColumnDeletion(Mutator<K, T> m, N name) {
        addColumnDeletion(m, key, name);
    }

    /**
     * Delete column, using specified timestamp
     */
    public void addColumnDeletion(Mutator<K, T> m, N name, T timestamp) {
        addColumnDeletion(m, key, name, timestamp);
    }
    
    public ColumnQuery<K, T, N, V> createColumnQuery(KeyspaceManager keyspaceManager, N columnName) {
        return createColumnQuery(keyspaceManager, key, columnName);
    }

    public SliceQuery<K, T, N> createSliceQuery(KeyspaceManager keyspaceManager,
            N start, N finish, boolean reversed, int count) {
        return createSliceQuery(keyspaceManager, key, start, finish, reversed, count);
    }
}
