package net.retakethe.policyauction.data.impl.query.impl;

import java.util.List;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedMultigetSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRows;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.serializers.DummySerializer;

/**
 * @author Nick Clarke
 */
public class VariableValueTypedMultigetSliceQueryImpl<K, T extends Timestamp, N>
        implements VariableValueTypedMultigetSliceQuery<K, T, N> {

    private final MultigetSliceQuery<K, N, Object> wrappedQuery;

    public VariableValueTypedMultigetSliceQueryImpl(Keyspace ks, ColumnFamily<K, T, N> cf,
            List<NamedColumn<K, T, N, ?>> columns) {
        N[] columnNames = QueryUtils.getColumnNamesUnresolved(cf, columns);

        wrappedQuery = HFactory.createMultigetSliceQuery(ks, cf.getKeySerializer(),
                        cf.getColumnNameSerializer(), DummySerializer.get())
                .setColumnFamily(cf.getName())
                .setColumnNames(columnNames);
    }

    public VariableValueTypedMultigetSliceQueryImpl(Keyspace ks, ColumnFamily<K, T, N> cf,
            ColumnRange<K, T, N, ?> columnRange, N start, N finish, boolean reversed, int count) {
        QueryUtils.checkColumnBelongsToColumnFamily(cf, columnRange);
        
        wrappedQuery = HFactory.createMultigetSliceQuery(ks, cf.getKeySerializer(),
                cf.getColumnNameSerializer(), DummySerializer.get())
                .setColumnFamily(cf.getName())
                .setRange(start, finish, reversed, count);
    }
    
    @Override
    public VariableValueTypedMultigetSliceQuery<K, T, N> setKeys(Iterable<K> keys) {
        wrappedQuery.setKeys(keys);
        return this;
    }

    @Override
    public VariableValueTypedMultigetSliceQuery<K, T, N> setKeys(K... keys) {
        wrappedQuery.setKeys(keys);
        return this;
    }

    @Override
    public QueryResult<VariableValueTypedRows<K, T, N>> execute() {
        QueryResult<Rows<K, N, Object>> wrappedResult = wrappedQuery.execute();

        return new QueryResultImpl<VariableValueTypedRows<K, T, N>>(
                new ExecutionResult<VariableValueTypedRows<K, T, N>>(
                        new VariableValueTypedRowsImpl<K, T, N>(wrappedResult.get()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }
}
