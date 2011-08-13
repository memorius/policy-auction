package net.retakethe.policyauction.data.impl.query.impl;

import java.util.List;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.OrderedSuperRows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSuperSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedOrderedSuperRows;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRangeSuperSlicesQuery;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.serializers.DummySerializer;

public class VariableValueTypedRangeSuperSlicesQueryImpl<K, T extends Timestamp, SN, N> implements
        VariableValueTypedRangeSuperSlicesQuery<K, T, SN, N> {

    private final RangeSuperSlicesQuery<K, SN, N, Object> wrappedQuery;

    public VariableValueTypedRangeSuperSlicesQueryImpl(Keyspace ks, SupercolumnFamily<K, T, SN, N> scf,
            List<NamedSupercolumn<K, T, SN, N>> supercolumns) {
        SN[] supercolumnNames = QueryUtils.getSupercolumnNamesUnresolved(scf, supercolumns);

        wrappedQuery = HFactory.createRangeSuperSlicesQuery(ks, scf.getKeySerializer(),
                    scf.getSupercolumnNameSerializer(), scf.getSubcolumnNameSerializer(),
                    DummySerializer.get())
                .setColumnFamily(scf.getName())
                .setColumnNames(supercolumnNames);
    }

    public VariableValueTypedRangeSuperSlicesQueryImpl(Keyspace ks, SupercolumnFamily<K, T, SN, N> scf,
            SupercolumnRange<K, T, SN, N> supercolumnRange, SN start, SN finish, boolean reversed, int count) {
        QueryUtils.checkSupercolumnBelongsToFamily(scf, supercolumnRange);

        wrappedQuery = HFactory.createRangeSuperSlicesQuery(ks, scf.getKeySerializer(),
                    scf.getSupercolumnNameSerializer(), scf.getSubcolumnNameSerializer(),
                    DummySerializer.get())
                .setColumnFamily(scf.getName())
                .setRange(start, finish, reversed, count);
    }

    @Override
    public VariableValueTypedRangeSuperSlicesQuery<K, T, SN, N> setKeys(K start, K end) {
        wrappedQuery.setKeys(start, end);
        return this;
    }

    @Override
    public VariableValueTypedRangeSuperSlicesQuery<K, T, SN, N> setRowCount(int rowCount) {
        wrappedQuery.setRowCount(rowCount);
        return this;
    }

    @Override
    public QueryResult<VariableValueTypedOrderedSuperRows<K, T, SN, N>> execute() {
        QueryResult<OrderedSuperRows<K, SN, N, Object>> wrappedResult = wrappedQuery.execute();

        return new QueryResultImpl<VariableValueTypedOrderedSuperRows<K, T, SN, N>>(
                new ExecutionResult<VariableValueTypedOrderedSuperRows<K, T, SN, N>>(
                        new VariableValueTypedOrderedSuperRowsImpl<K, T, SN, N>(wrappedResult.get()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }
}
