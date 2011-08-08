package net.retakethe.policyauction.data.impl.query.impl;

import java.util.List;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedOrderedRows;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRangeSlicesQuery;
import net.retakethe.policyauction.data.impl.schema.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.NamedColumn;

/**
 * @author Nick Clarke
 */
public class VariableValueTypedRangeSlicesQueryImpl<K, N> implements VariableValueTypedRangeSlicesQuery<K, N> {

    private final RangeSlicesQuery<K, N, Object> wrappedQuery;

    public VariableValueTypedRangeSlicesQueryImpl(Keyspace ks, ColumnFamily<K, N> cf,
            List<NamedColumn<K, N, ?>> columns) {
        N[] columnNames = QueryUtils.getColumnNamesUnresolved(cf, columns);

        wrappedQuery = HFactory.createRangeSlicesQuery(ks, cf.getKeySerializer(),
                    cf.getColumnNameSerializer(), DummySerializer.get())
                .setColumnFamily(cf.getName())
                .setColumnNames(columnNames);
    }

    @Override
    public QueryResult<VariableValueTypedOrderedRows<K, N>> execute() {
        QueryResult<OrderedRows<K, N, Object>> wrappedResult = wrappedQuery.execute();

        return new QueryResultImpl<VariableValueTypedOrderedRows<K, N>>(
                new ExecutionResult<VariableValueTypedOrderedRows<K, N>>(
                        new VariableValueTypedOrderedRowsImpl<K, N>(wrappedResult.get()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }

    @Override
    public VariableValueTypedRangeSlicesQuery<K, N> setKeys(K start, K end) {
        wrappedQuery.setKeys(start, end);
        return this;
    }

    @Override
    public VariableValueTypedRangeSlicesQuery<K, N> setRowCount(int rowCount) {
        wrappedQuery.setRowCount(rowCount);
        return this;
    }

    @Override
    public VariableValueTypedRangeSlicesQuery<K, N> setReturnKeysOnly() {
        wrappedQuery.setReturnKeysOnly();
        return this;
    }
}
