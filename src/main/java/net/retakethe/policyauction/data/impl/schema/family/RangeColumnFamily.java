package net.retakethe.policyauction.data.impl.schema.family;


import net.retakethe.policyauction.data.impl.query.api.ColumnQuery;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.Mutator;
import net.retakethe.policyauction.data.impl.query.api.MultigetSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.RangeSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.SliceQuery;
import net.retakethe.policyauction.data.impl.query.impl.MutatorInternal;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.schema.timestamp.TimestampFactory;
import net.retakethe.policyauction.data.impl.schema.value.Value;

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

    public void addColumnInsertion(Mutator<K, T> m, K key, N name, Value<T, V> value) {
        ((MutatorInternal<K, T>) m).addColumnInsertion(key, columnRange, name, value);
    }

    /**
     * Delete column, using current timestamp
     */
    public void addColumnDeletion(Mutator<K, T> m, K key, N name) {
        ((MutatorInternal<K, T>) m).addColumnDeletion(key, columnRange, name,
                createCurrentTimestamp());
    }
    
    /**
     * Delete column, using specified timestamp
     */
    public void addColumnDeletion(Mutator<K, T> m, K key, N name, T timestamp) {
        ((MutatorInternal<K, T>) m).addColumnDeletion(key, columnRange, name, timestamp);
    }

    public ColumnQuery<K, T, N, V> createColumnQuery(KeyspaceManager keyspaceManager, K key, N columnName) {
        return createColumnQuery(keyspaceManager, key, columnRange, columnName);
    }

    public SliceQuery<K, T, N> createSliceQuery(KeyspaceManager keyspaceManager, K key,
            N start, N finish, boolean reversed, int count) {
        return createSliceQuery(keyspaceManager, key, columnRange, start, finish, reversed, count);
    }

    public MultigetSliceQuery<K, T, N> createMultigetSliceQuery(KeyspaceManager keyspaceManager,
            N start, N finish, boolean reversed, int count) {
        return createMultigetSliceQuery(keyspaceManager, columnRange, start, finish, reversed, count);
    }

    public RangeSlicesQuery<K, T, N> createRangeSlicesQuery(KeyspaceManager keyspaceManager,
            N start, N finish, boolean reversed, int count) {
        return createRangeSlicesQuery(keyspaceManager, columnRange, start, finish, reversed, count);
    }
}
