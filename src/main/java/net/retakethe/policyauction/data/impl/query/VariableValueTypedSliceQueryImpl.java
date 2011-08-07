package net.retakethe.policyauction.data.impl.query;

import java.util.List;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;
import net.retakethe.policyauction.data.impl.schema.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.NamedColumn;

/**
 * @author Nick Clarke
 *
 */
public class VariableValueTypedSliceQueryImpl<K, N> implements VariableValueTypedSliceQuery<K, N> {

    private final SliceQuery<K, N, Object> wrappedQuery;

    public VariableValueTypedSliceQueryImpl(Keyspace ks, ColumnFamily<K> cf, List<NamedColumn<K, N, ?>> columns, K key) {
        if (columns.isEmpty()) {
            throw new IllegalArgumentException("At least one column is required");
        }

        NamedColumn<K, N, ?> firstColumn = columns.get(0);
        Serializer<N> nameSerializer = firstColumn.getNameSerializer();

        N[] columnNames = QueryFactory.getColumnNamesUnresolved(cf, columns);

        wrappedQuery = HFactory.createSliceQuery(ks, cf.getKeySerializer(),
                    nameSerializer, new DummySerializer<Object>())
                .setColumnFamily(cf.getName())
                .setColumnNames(columnNames)
                .setKey(key);
    }

    public VariableValueTypedSliceQueryImpl(Keyspace ks, ColumnFamily<K> cf, ColumnRange<K, N, ?> columnRange,
            N start, N finish, boolean reversed, int count, K key) {
        QueryFactory.checkColumnRangeBelongsToColumnFamily(cf, columnRange);

        wrappedQuery = HFactory.createSliceQuery(ks, cf.getKeySerializer(),
                    columnRange.getNameSerializer(), new DummySerializer<Object>())
                .setColumnFamily(cf.getName())
                .setRange(start, finish, reversed, count)
                .setKey(key);
    }

    @Override
    public QueryResult<VariableValueTypedColumnSlice<N>> execute() {
        QueryResult<ColumnSlice<N, Object>> wrappedResult = wrappedQuery.execute();

        return new QueryResultImpl<VariableValueTypedColumnSlice<N>>(
                new ExecutionResult<VariableValueTypedColumnSlice<N>>(
                        new VariableValueTypedColumnSliceImpl<N>(wrappedResult.get()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }
}
