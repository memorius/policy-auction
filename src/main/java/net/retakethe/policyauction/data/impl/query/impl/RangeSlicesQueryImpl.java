package net.retakethe.policyauction.data.impl.query.impl;

import java.util.List;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.impl.query.api.OrderedRows;
import net.retakethe.policyauction.data.impl.query.api.RangeSlicesQuery;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.serializers.DummySerializer;

/**
 * @author Nick Clarke
 */
public class RangeSlicesQueryImpl<K, T extends Timestamp, N>
        implements RangeSlicesQuery<K, T, N> {

    private final me.prettyprint.hector.api.query.RangeSlicesQuery<K, N, Object> wrappedQuery;

    public RangeSlicesQueryImpl(Keyspace ks, ColumnFamily<K, T, N> cf,
            List<NamedColumn<K, T, N, ?>> columns) {
        N[] columnNames = QueryUtils.getColumnNamesUnresolved(cf, columns);

        wrappedQuery = HFactory.createRangeSlicesQuery(ks, cf.getKeySerializer(),
                    cf.getColumnNameSerializer(), DummySerializer.get())
                .setColumnFamily(cf.getName())
                .setColumnNames(columnNames);
    }

    public RangeSlicesQueryImpl(Keyspace ks, ColumnFamily<K, T, N> cf,
            ColumnRange<K, T, N, ?> columnRange, N start, N finish, boolean reversed, int count) {
        QueryUtils.checkColumnBelongsToColumnFamily(cf, columnRange);

        wrappedQuery = HFactory.createRangeSlicesQuery(ks, cf.getKeySerializer(),
                    cf.getColumnNameSerializer(), DummySerializer.get())
                .setColumnFamily(cf.getName())
                .setRange(start, finish, reversed, count);
    }

    @Override
    public QueryResult<OrderedRows<K, T, N>> execute() {
        QueryResult<me.prettyprint.hector.api.beans.OrderedRows<K, N, Object>> wrappedResult = wrappedQuery.execute();

        return new QueryResultImpl<OrderedRows<K, T, N>>(
                new ExecutionResult<OrderedRows<K, T, N>>(
                        new OrderedRowsImpl<K, T, N>(wrappedResult.get()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }

    @Override
    public RangeSlicesQuery<K, T, N> setKeys(K start, K end) {
        wrappedQuery.setKeys(start, end);
        return this;
    }

    @Override
    public RangeSlicesQuery<K, T, N> setRowCount(int rowCount) {
        wrappedQuery.setRowCount(rowCount);
        return this;
    }

    @Override
    public RangeSlicesQuery<K, T, N> setReturnKeysOnly() {
        wrappedQuery.setReturnKeysOnly();
        return this;
    }
}
