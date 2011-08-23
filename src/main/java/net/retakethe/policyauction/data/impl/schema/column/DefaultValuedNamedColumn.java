package net.retakethe.policyauction.data.impl.schema.column;

import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.impl.query.api.ColumnQuery;
import net.retakethe.policyauction.data.impl.query.api.ColumnResult;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.Mutator;
import net.retakethe.policyauction.data.impl.schema.Type;
import net.retakethe.policyauction.data.impl.schema.family.SingleRowNamedColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

public class DefaultValuedNamedColumn<K, T extends Timestamp, N, V> extends NamedColumn<K, T, N, V> {

    private final V defaultValue;
    private final SingleRowNamedColumnFamily<K, T, N> columnFamily;

    /**
     * Construct with fixed default value
     *
     * @param defaultValue must not be null
     */
    public DefaultValuedNamedColumn(N name, SingleRowNamedColumnFamily<K, T, N> columnFamily, Type<V> valueType,
            V defaultValue) {
        super(name, columnFamily, valueType);
        if (defaultValue == null) {
            throw new IllegalArgumentException("defaultValue must not be null");
        }
        this.columnFamily = columnFamily;
        this.defaultValue = defaultValue;
    }

    /**
     * Constructor for use where {@link #getDefaultValue()} is overridden instead of using a value fixed at consstruction
     */
    protected DefaultValuedNamedColumn(N name, SingleRowNamedColumnFamily<K, T, N> columnFamily, Type<V> valueType) {
        super(name, columnFamily, valueType);
        this.columnFamily = columnFamily;
        // We expect getDefaultValue() to be overridden
        this.defaultValue = null;
    }

    public V getColumnValueOrSetDefault(KeyspaceManager keyspaceManager) {
        ColumnQuery<K, T, N, V> query = columnFamily.createColumnQuery(keyspaceManager, this);
        QueryResult<ColumnResult<T, N, V>> result = query.execute();
        ColumnResult<T, N, V> columnResult = result.get();
        if (columnResult != null) {
            return columnResult.getValue().getValue();
        }

        // Not set - insert default value
        setColumnValue(keyspaceManager, getDefaultValue());

        return getColumnValueOrSetDefault(keyspaceManager);
    }

    /**
     * Internal use only - can be overridden where a dynamically-generated initial value is required
     */
    protected V getDefaultValue() {
        if (defaultValue == null) {
            throw new UnsupportedOperationException("getDefaultValue must be overridden if no value is set at construction");
        }
        return defaultValue;
    }

    public void setColumnValue(KeyspaceManager keyspaceManager, V value) {
        Mutator<K, T> m = columnFamily.createMutator(keyspaceManager);
        columnFamily.addColumnInsertion(m, this, columnFamily.createValue(value));
        m.execute();
    }
}
