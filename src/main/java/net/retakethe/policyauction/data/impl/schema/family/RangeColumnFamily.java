package net.retakethe.policyauction.data.impl.schema.family;


import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.impl.MutatorWrapperInternal;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.timestamp.TimestampFactory;

/**
 * Used for column families which contain a single column range in each row.
 *
 * @author Nick Clarke
 */
public class RangeColumnFamily<K, T extends Timestamp, N, V> extends ColumnFamily<K, T, N> {

    private ColumnRange<K, T, N, V> columnRange;

    public RangeColumnFamily(SchemaKeyspace keyspace, String name, Type<K> keyType,
            TimestampFactory<T> timestampFactory, Type<N> columnNameType) {
        super(keyspace, name, keyType, timestampFactory, columnNameType);
    }

    /**
     * Initialization call to set the column range definition.
     * Can't be set in constructor due to cyclic dependency.
     *
     * @throws IllegalStateException if called more than once
     */
    protected void setColumnRange(ColumnRange<K, T, N, V> columnRange) {
        if (this.columnRange != null) {
            throw new IllegalStateException("columnRange already set");
        }
        this.columnRange = columnRange;
    }

    public ColumnRange<K, T, N, V> getColumnRange() {
        return columnRange;
    }

    public void addColumnInsertion(MutatorWrapper<K, T> m, K key, N name, V value) {
        ((MutatorWrapperInternal<K, T>) m).addColumnInsertion(key, columnRange, name, value);
    }

    public void addColumnDeletion(MutatorWrapper<K, T> m, K key, N name) {
        ((MutatorWrapperInternal<K, T>) m).addColumnDeletion(key, columnRange, name);
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
