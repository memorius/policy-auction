package net.retakethe.policyauction.data.impl.query.impl;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.impl.query.api.ColumnQuery;
import net.retakethe.policyauction.data.impl.query.api.ColumnResult;
import net.retakethe.policyauction.data.impl.schema.column.Column;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 */
public class ColumnQueryImpl<K, T extends Timestamp, N, V> implements ColumnQuery<K, T, N, V> {

    private final me.prettyprint.hector.api.query.ColumnQuery<K, N, V> wrappedQuery;
    private final ColumnFamily<K, T, N> columnFamily;
    private final Column<K, T, N, V> column;

    public ColumnQueryImpl(Keyspace ks, ColumnFamily<K, T, N> cf, K key, Column<K, T, N, V> column,
            N columnName) {
        QueryUtils.checkColumnBelongsToColumnFamily(cf, column);

        this.column = column;
        this.columnFamily = cf;
        this.wrappedQuery = HFactory.createColumnQuery(ks, cf.getKeySerializer(),
                cf.getColumnNameSerializer(), column.getValueSerializer())
                .setColumnFamily(cf.getName())
                .setName(columnName)
                .setKey(key);
    }

    @Override
    public QueryResult<ColumnResult<T, N, V>> execute() {
        QueryResult<HColumn<N, V>> wrappedResult = wrappedQuery.execute();

        @SuppressWarnings("unchecked")
        HColumn<N, Object> hColumn = (HColumn<N, Object>) wrappedResult.get();

        ColumnResultImpl<T, N, V> columnResult;
        if (hColumn == null) {
            columnResult = null;
        } else {
            columnResult = new ColumnResultImpl<T, N, V>(hColumn, columnFamily, column.getValueSerializer());
        }
        return new QueryResultImpl<ColumnResult<T, N, V>>(
                new ExecutionResult<ColumnResult<T, N, V>>(
                        columnResult,
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }

}
