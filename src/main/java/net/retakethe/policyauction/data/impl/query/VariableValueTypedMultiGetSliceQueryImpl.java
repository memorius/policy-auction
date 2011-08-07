package net.retakethe.policyauction.data.impl.query;

import java.util.List;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.impl.schema.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.NamedColumn;

/**
 * @author Nick Clarke
 */
public class VariableValueTypedMultiGetSliceQueryImpl<K, N> implements VariableValueTypedMultiGetSliceQuery<K, N> {

    private final MultigetSliceQuery<K, N, Object> wrappedQuery;

    public VariableValueTypedMultiGetSliceQueryImpl(Keyspace ks, ColumnFamily<K> cf, List<NamedColumn<K, N, ?>> columns) {
        if (columns.isEmpty()) {
            throw new IllegalArgumentException("At least one column is required");
        }

        NamedColumn<K, N, ?> firstColumn = columns.get(0);
        Serializer<N> nameSerializer = firstColumn.getNameSerializer();

        N[] columnNames = QueryFactory.getColumnNamesUnresolved(cf, columns);

        wrappedQuery = HFactory.createMultigetSliceQuery(ks, cf.getKeySerializer(),
                nameSerializer, new DummySerializer<Object>())
                .setColumnFamily(cf.getName())
                .setColumnNames(columnNames);
    }

    public VariableValueTypedMultiGetSliceQueryImpl(Keyspace ks, ColumnFamily<K> cf, ColumnRange<K, N, ?> columnRange,
            N start, N finish, boolean reversed, int count) {
        QueryFactory.checkColumnRangeBelongsToColumnFamily(cf, columnRange);

        wrappedQuery = HFactory.createMultigetSliceQuery(ks, cf.getKeySerializer(),
                columnRange.getNameSerializer(), new DummySerializer<Object>())
                .setColumnFamily(cf.getName())
                .setRange(start, finish, reversed, count);
    }

    @Override
    public VariableValueTypedMultiGetSliceQuery<K, N> setKeys(Iterable<K> keys) {
        wrappedQuery.setKeys(keys);
        return this;
    }

    @Override
    public VariableValueTypedMultiGetSliceQuery<K,N> setKeys(K... keys) {
        wrappedQuery.setKeys(keys);
        return this;
    }

    @Override
    public QueryResult<VariableValueTypedRows<K, N>> execute() {
        QueryResult<Rows<K, N, Object>> wrappedResult = wrappedQuery.execute();

        return new QueryResultImpl<VariableValueTypedRows<K, N>>(
                new ExecutionResult<VariableValueTypedRows<K, N>>(
                        new VariableValueTypedRowsImpl<K, N>(wrappedResult.get()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }
}
