package net.retakethe.policyauction.data.impl.query.impl;

import java.util.List;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.impl.query.api.MultigetSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.Rows;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.serializers.DummySerializer;

/**
 * @author Nick Clarke
 */
public class MultigetSliceQueryImpl<K, T extends Timestamp, N> implements MultigetSliceQuery<K, T, N> {

    private final me.prettyprint.hector.api.query.MultigetSliceQuery<K, N, Object> wrappedQuery;

    public MultigetSliceQueryImpl(Keyspace ks, ColumnFamily<K, T, N> cf,
            List<NamedColumn<K, T, N, ?>> columns) {
        N[] columnNames = QueryUtils.getColumnNamesUnresolved(cf, columns);

        wrappedQuery = HFactory.createMultigetSliceQuery(ks, cf.getKeySerializer(),
                        cf.getColumnNameSerializer(), DummySerializer.get())
                .setColumnFamily(cf.getName())
                .setColumnNames(columnNames);
    }

    public MultigetSliceQueryImpl(Keyspace ks, ColumnFamily<K, T, N> cf,
            ColumnRange<K, T, N, ?> columnRange, N start, N finish, boolean reversed, int count) {
        QueryUtils.checkColumnBelongsToColumnFamily(cf, columnRange);
        
        wrappedQuery = HFactory.createMultigetSliceQuery(ks, cf.getKeySerializer(),
                cf.getColumnNameSerializer(), DummySerializer.get())
                .setColumnFamily(cf.getName())
                .setRange(start, finish, reversed, count);
    }
    
    @Override
    public MultigetSliceQuery<K, T, N> setKeys(Iterable<K> keys) {
        wrappedQuery.setKeys(keys);
        return this;
    }

    @Override
    public MultigetSliceQuery<K, T, N> setKeys(K... keys) {
        wrappedQuery.setKeys(keys);
        return this;
    }

    @Override
    public QueryResult<Rows<K, T, N>> execute() {
        QueryResult<me.prettyprint.hector.api.beans.Rows<K, N, Object>> wrappedResult = wrappedQuery.execute();

        return new QueryResultImpl<Rows<K, T, N>>(
                new ExecutionResult<Rows<K, T, N>>(
                        new RowsImpl<K, T, N>(wrappedResult.get()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }
}
