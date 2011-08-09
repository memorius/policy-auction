package net.retakethe.policyauction.data.impl.schema.family;


import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import net.retakethe.policyauction.data.impl.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.QueryFactory;
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

    protected void setColumnRange(ColumnRange<K, N, V> columnRange) {
        if (this.columnRange != null) {
            throw new IllegalStateException("columnRange already set");
        }
        this.columnRange = columnRange;
    }

    public SliceQuery<K, N, V> createSliceQuery(KeyspaceManager keyspaceManager, K key,
            N start, N finish, boolean reversed, int count) {
        return QueryFactory.createSliceQuery(keyspaceManager, this, key, columnRange, start, finish, reversed, count);
    }

    public ColumnQuery<K, N, V> createColumnQuery(KeyspaceManager keyspaceManager, K key, N columnName) {
        return QueryFactory.createColumnQuery(keyspaceManager, this, key, columnRange, columnName);
    }
}
