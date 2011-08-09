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
import net.retakethe.policyauction.data.impl.query.impl.serializers.DummySerializer;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;

public class VariableValueTypedRangeSuperSlicesQueryImpl<K, SN, N> implements
        VariableValueTypedRangeSuperSlicesQuery<K, SN, N> {

    private final RangeSuperSlicesQuery<K, SN, N, Object> wrappedQuery;

    public VariableValueTypedRangeSuperSlicesQueryImpl(Keyspace ks, SupercolumnFamily<K, SN, N> scf,
            List<NamedSupercolumn<K, SN, N>> supercolumns) {
        SN[] supercolumnNames = QueryUtils.getSupercolumnNamesUnresolved(scf, supercolumns);

        wrappedQuery = HFactory.createRangeSuperSlicesQuery(ks, scf.getKeySerializer(),
                    scf.getSupercolumnNameSerializer(), scf.getSubcolumnNameSerializer(),
                    DummySerializer.get())
                .setColumnFamily(scf.getName())
                .setColumnNames(supercolumnNames);
    }

    public VariableValueTypedRangeSuperSlicesQueryImpl(Keyspace ks, SupercolumnFamily<K, SN, N> scf,
            SupercolumnRange<K, SN, N> supercolumnRange, SN start, SN finish, boolean reversed, int count) {
        QueryUtils.checkSupercolumnBelongsToFamily(scf, supercolumnRange);

        wrappedQuery = HFactory.createRangeSuperSlicesQuery(ks, scf.getKeySerializer(),
                    scf.getSupercolumnNameSerializer(), scf.getSubcolumnNameSerializer(),
                    DummySerializer.get())
                .setColumnFamily(scf.getName())
                .setRange(start, finish, reversed, count);
    }

    @Override
    public VariableValueTypedRangeSuperSlicesQuery<K, SN, N> setKeys(K start, K end) {
        wrappedQuery.setKeys(start, end);
        return this;
    }

    @Override
    public VariableValueTypedRangeSuperSlicesQuery<K, SN, N> setRowCount(int rowCount) {
        wrappedQuery.setRowCount(rowCount);
        return this;
    }

    @Override
    public QueryResult<VariableValueTypedOrderedSuperRows<K, SN, N>> execute() {
        QueryResult<OrderedSuperRows<K, SN, N, Object>> wrappedResult = wrappedQuery.execute();

        return new QueryResultImpl<VariableValueTypedOrderedSuperRows<K, SN, N>>(
                new ExecutionResult<VariableValueTypedOrderedSuperRows<K, SN, N>>(
                        new VariableValueTypedOrderedSuperRowsImpl<K, SN, N>(wrappedResult.get()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }
}
