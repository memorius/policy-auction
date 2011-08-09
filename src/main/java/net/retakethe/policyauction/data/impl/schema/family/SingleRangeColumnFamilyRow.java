package net.retakethe.policyauction.data.impl.schema.family;

import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import net.retakethe.policyauction.data.impl.KeyspaceManager;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.Type;

/**
 * Used for single-row lookup indexes such as those in "misc" table.
 *
 * @author Nick Clarke
 */
public class SingleRangeColumnFamilyRow<K, N, V> extends RangeColumnFamily<K, N, V> {

    private final K key;

    public SingleRangeColumnFamilyRow(SchemaKeyspace keyspace, String name, K key, Type<K> keyType, Type<N> columnNameType) {
        super(keyspace, name, keyType, columnNameType);
        this.key = key;
    }

    public K getKey() {
        return key;
    }

    public SliceQuery<K, N, V> createSliceQuery(KeyspaceManager keyspaceManager,
            N start, N finish, boolean reversed, int count) {
        return createSliceQuery(keyspaceManager, key, start, finish, reversed, count);
    }

    public ColumnQuery<K, N, V> createColumnQuery(KeyspaceManager keyspaceManager, N columnName) {
        return createColumnQuery(keyspaceManager, key, columnName);
    }
}
