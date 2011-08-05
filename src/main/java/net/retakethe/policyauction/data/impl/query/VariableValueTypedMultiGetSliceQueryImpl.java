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
import net.retakethe.policyauction.data.impl.schema.Column;
import net.retakethe.policyauction.data.impl.schema.ColumnFamily;

/**
 * @author Nick Clarke
 */
public class VariableValueTypedMultiGetSliceQueryImpl<K, N> implements VariableValueTypedMultiGetSliceQuery<K, N> {

    private final MultigetSliceQuery<K, N, Object> wrappedQuery;

    public VariableValueTypedMultiGetSliceQueryImpl(Keyspace ks, ColumnFamily<K> cf, List<Column<K, N, ?>> columns) {
        N[] columnNames = QueryFactory.getColumnNamesUnresolved(cf, columns);

        Serializer<N> nameSerializer;
        if (columns.isEmpty()) {
            // Required but won't be used
            nameSerializer = new DummySerializer<N>();
        } else {
            Column<K, N, ?> firstColumn = columns.get(0);
            nameSerializer = firstColumn.getNameSerializer();
        }

        wrappedQuery = HFactory.createMultigetSliceQuery(ks, cf.getKeySerializer(),
                nameSerializer, new DummySerializer<Object>())
                .setColumnFamily(cf.getName())
                .setColumnNames(columnNames);
    }

    public VariableValueTypedMultiGetSliceQueryImpl(Keyspace ks, ColumnFamily<K> cf, Serializer<N> nameSerializer,
            N start, N finish, boolean reversed, int count) {
        wrappedQuery = HFactory.createMultigetSliceQuery(ks, cf.getKeySerializer(),
                nameSerializer, new DummySerializer<Object>())
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
