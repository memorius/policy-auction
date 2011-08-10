package net.retakethe.policyauction.data.impl.schema.family;


import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;

/**
 * Used for column families which contain a single column range in each row.
 *
 * @author Nick Clarke
 */
public class RangeColumnFamily<K, N, V> extends ColumnFamily<K, N> {

    private ColumnRange<K, N, V> columnRange;

    public RangeColumnFamily(SchemaKeyspace keyspace, String name, Type<K> keyType, Type<N> columnNameType) {
        super(keyspace, name, keyType, columnNameType);
    }

    /**
     * Initialization call to set the column range definition.
     * Can't be set in constructor due to cyclic dependency.
     *
     * @throws IllegalStateException if called more than once
     */
    protected void setColumnRange(ColumnRange<K, N, V> columnRange) {
        if (this.columnRange != null) {
            throw new IllegalStateException("columnRange already set");
        }
        this.columnRange = columnRange;
    }

    public ColumnRange<K, N, V> getColumnRange() {
        return columnRange;
    }

    public void addColumnInsertion(MutatorWrapper<K> m, K key, N name, V value) {
        m.addColumnInsertion(key, columnRange, name, value);
    }

    public void addColumnDeletion(MutatorWrapper<K> m, K key, N name) {
        m.addColumnDeletion(key, columnRange, name);
    }
    
    public ColumnQuery<K, N, V> createColumnQuery(KeyspaceManager keyspaceManager, K key, N columnName) {
        return createColumnQuery(keyspaceManager, key, columnRange, columnName);
    }

    public SliceQuery<K, N, V> createSliceQuery(KeyspaceManager keyspaceManager, K key,
            N start, N finish, boolean reversed, int count) {
        return createSliceQuery(keyspaceManager, key, columnRange, start, finish, reversed, count);
    }

    public MultigetSliceQuery<K, N, V> createMultigetSliceQuery(KeyspaceManager keyspaceManager,
            N start, N finish, boolean reversed, int count) {
        return createMultigetSliceQuery(keyspaceManager, columnRange, start, finish, reversed, count);
    }

    public RangeSlicesQuery<K, N, V> createRangeSlicesQuery(KeyspaceManager keyspaceManager,
            N start, N finish, boolean reversed, int count) {
        return createRangeSlicesQuery(keyspaceManager, columnRange, start, finish, reversed, count);
    }
}
