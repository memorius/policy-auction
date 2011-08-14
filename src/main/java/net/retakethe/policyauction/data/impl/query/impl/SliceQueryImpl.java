package net.retakethe.policyauction.data.impl.query.impl;

import java.util.List;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.impl.query.api.ColumnSlice;
import net.retakethe.policyauction.data.impl.query.api.SliceQuery;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.serializers.DummySerializer;

/**
 * @author Nick Clarke
 */
public class SliceQueryImpl<K, T extends Timestamp, N>
        implements SliceQuery<K, T, N> {

    private final me.prettyprint.hector.api.query.SliceQuery<K, N, Object> wrappedQuery;

    public SliceQueryImpl(Keyspace ks, ColumnFamily<K, T, N> cf,
            List<NamedColumn<K, T, N, ?>> columns, K key) {
        N[] columnNames = QueryUtils.getColumnNamesUnresolved(cf, columns);

        wrappedQuery = HFactory.createSliceQuery(ks, cf.getKeySerializer(),
                    cf.getColumnNameSerializer(), DummySerializer.get())
                .setColumnFamily(cf.getName())
                .setColumnNames(columnNames)
                .setKey(key);
    }

    public SliceQueryImpl(Keyspace ks, ColumnFamily<K, T, N> cf,
            ColumnRange<K, T, N, ?> columnRange, N start, N finish, boolean reversed, int count, K key) {
        QueryUtils.checkColumnBelongsToColumnFamily(cf, columnRange);

        wrappedQuery = HFactory.createSliceQuery(ks, cf.getKeySerializer(),
                    cf.getColumnNameSerializer(), DummySerializer.get())
                .setColumnFamily(cf.getName())
                .setRange(start, finish, reversed, count)
                .setKey(key);
    }

    @Override
    public QueryResult<ColumnSlice<T, N>> execute() {
        QueryResult<me.prettyprint.hector.api.beans.ColumnSlice<N, Object>> wrappedResult = wrappedQuery.execute();

        return new QueryResultImpl<ColumnSlice<T, N>>(
                new ExecutionResult<ColumnSlice<T, N>>(
                        new ColumnSliceImpl<T, N>(wrappedResult.get()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }
}
