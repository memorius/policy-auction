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
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.serializers.DummySerializer;

/**
 * @author Nick Clarke
 */
public class VariableValueTypedRangeSlicesQueryImpl<K, T extends Timestamp, N>
        implements VariableValueTypedRangeSlicesQuery<K, T, N> {

    private final RangeSlicesQuery<K, N, Object> wrappedQuery;

    public VariableValueTypedRangeSlicesQueryImpl(Keyspace ks, ColumnFamily<K, T, N> cf,
            List<NamedColumn<K, T, N, ?>> columns) {
        N[] columnNames = QueryUtils.getColumnNamesUnresolved(cf, columns);

        wrappedQuery = HFactory.createRangeSlicesQuery(ks, cf.getKeySerializer(),
                    cf.getColumnNameSerializer(), DummySerializer.get())
                .setColumnFamily(cf.getName())
                .setColumnNames(columnNames);
    }

    @Override
    public QueryResult<VariableValueTypedOrderedRows<K, T, N>> execute() {
        QueryResult<OrderedRows<K, N, Object>> wrappedResult = wrappedQuery.execute();

        return new QueryResultImpl<VariableValueTypedOrderedRows<K, T, N>>(
                new ExecutionResult<VariableValueTypedOrderedRows<K, T, N>>(
                        new VariableValueTypedOrderedRowsImpl<K, T, N>(wrappedResult.get()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }

    @Override
    public VariableValueTypedRangeSlicesQuery<K, T, N> setKeys(K start, K end) {
        wrappedQuery.setKeys(start, end);
        return this;
    }

    @Override
    public VariableValueTypedRangeSlicesQuery<K, T, N> setRowCount(int rowCount) {
        wrappedQuery.setRowCount(rowCount);
        return this;
    }

    @Override
    public VariableValueTypedRangeSlicesQuery<K, T, N> setReturnKeysOnly() {
        wrappedQuery.setReturnKeysOnly();
        return this;
    }
}
