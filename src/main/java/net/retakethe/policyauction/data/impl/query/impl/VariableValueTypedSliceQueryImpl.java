package net.retakethe.policyauction.data.impl.query.impl;

import java.util.List;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedColumnSlice;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSliceQuery;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.serializers.DummySerializer;

/**
 * @author Nick Clarke
 */
public class VariableValueTypedSliceQueryImpl<K, T extends Timestamp, N>
        implements VariableValueTypedSliceQuery<K, T, N> {

    private final SliceQuery<K, N, Object> wrappedQuery;

    public VariableValueTypedSliceQueryImpl(Keyspace ks, ColumnFamily<K, T, N> cf,
            List<NamedColumn<K, T, N, ?>> columns, K key) {
        N[] columnNames = QueryUtils.getColumnNamesUnresolved(cf, columns);

        wrappedQuery = HFactory.createSliceQuery(ks, cf.getKeySerializer(),
                    cf.getColumnNameSerializer(), DummySerializer.get())
                .setColumnFamily(cf.getName())
                .setColumnNames(columnNames)
                .setKey(key);
    }

    public VariableValueTypedSliceQueryImpl(Keyspace ks, ColumnFamily<K, T, N> cf,
            ColumnRange<K, T, N, ?> columnRange, N start, N finish, boolean reversed, int count, K key) {
        QueryUtils.checkColumnBelongsToColumnFamily(cf, columnRange);

        wrappedQuery = HFactory.createSliceQuery(ks, cf.getKeySerializer(),
                    cf.getColumnNameSerializer(), DummySerializer.get())
                .setColumnFamily(cf.getName())
                .setRange(start, finish, reversed, count)
                .setKey(key);
    }

    @Override
    public QueryResult<VariableValueTypedColumnSlice<T, N>> execute() {
        QueryResult<ColumnSlice<N, Object>> wrappedResult = wrappedQuery.execute();

        return new QueryResultImpl<VariableValueTypedColumnSlice<T, N>>(
                new ExecutionResult<VariableValueTypedColumnSlice<T, N>>(
                        new VariableValueTypedColumnSliceImpl<T, N>(wrappedResult.get()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }
}
