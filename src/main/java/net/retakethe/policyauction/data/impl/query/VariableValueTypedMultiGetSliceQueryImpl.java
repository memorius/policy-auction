package net.retakethe.policyauction.data.impl.query;

import java.util.List;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.impl.schema.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.NamedColumn;

/**
 * @author Nick Clarke
 */
public class VariableValueTypedMultiGetSliceQueryImpl<K, N> implements VariableValueTypedMultiGetSliceQuery<K, N> {

    private final MultigetSliceQuery<K, N, Object> wrappedQuery;

    public VariableValueTypedMultiGetSliceQueryImpl(Keyspace ks, ColumnFamily<K, N> cf,
            List<NamedColumn<K, N, ?>> columns) {
        N[] columnNames = QueryFactory.getColumnNamesUnresolved(cf, columns);

        wrappedQuery = HFactory.createMultigetSliceQuery(ks, cf.getKeySerializer(),
                        cf.getNameSerializer(), DummySerializer.get())
                .setColumnFamily(cf.getName())
                .setColumnNames(columnNames);
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
