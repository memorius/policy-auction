package net.retakethe.policyauction.data.impl.query.impl;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.impl.query.api.ColumnValueQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedColumn;
import net.retakethe.policyauction.data.impl.schema.column.Column;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

/**
 * @author Nick Clarke
 */
public class ColumnValueQueryImpl<K, T extends Timestamp, N, V> implements ColumnValueQuery<K, T, N, V> {

    private final ColumnQuery<K, N, V> wrappedQuery;
    private final ColumnFamily<K, T, N> columnFamily;
    private final Column<K, T, N, V> column;

    public ColumnValueQueryImpl(Keyspace ks, ColumnFamily<K, T, N> cf, K key, Column<K, T, N, V> column,
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
    public QueryResult<VariableValueTypedColumn<T, N, V>> execute() {
        QueryResult<HColumn<N, V>> wrappedResult = wrappedQuery.execute();

        @SuppressWarnings("unchecked")
        HColumn<N, Object> hColumn = (HColumn<N, Object>) wrappedResult.get();

        return new QueryResultImpl<VariableValueTypedColumn<T, N, V>>(
                new ExecutionResult<VariableValueTypedColumn<T, N, V>>(
                        new VariableValueTypedColumnImpl<T, N, V>(hColumn, columnFamily, column.getValueSerializer()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }

}
