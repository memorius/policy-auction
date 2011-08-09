package net.retakethe.policyauction.data.impl.query.impl;

import java.util.Collection;
import java.util.List;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.SuperRows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.MultigetSuperSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedMultiGetSuperSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSuperRows;
import net.retakethe.policyauction.data.impl.query.impl.serializers.DummySerializer;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;

public class VariableValueTypedMultiGetSuperSliceQueryImpl<K, SN, N> implements
        VariableValueTypedMultiGetSuperSliceQuery<K, SN, N> {

    private final MultigetSuperSliceQuery<K, SN, N, Object> wrappedQuery;

    public VariableValueTypedMultiGetSuperSliceQueryImpl(Keyspace ks, SupercolumnFamily<K, SN, N> scf,
            List<NamedSupercolumn<K, SN, N>> supercolumns) {
        SN[] supercolumnNames = QueryUtils.getSupercolumnNamesUnresolved(scf, supercolumns);

        wrappedQuery = HFactory.createMultigetSuperSliceQuery(ks, scf.getKeySerializer(),
                    scf.getSupercolumnNameSerializer(), scf.getSubcolumnNameSerializer(),
                    DummySerializer.get())
                .setColumnFamily(scf.getName())
                .setColumnNames(supercolumnNames);
    }

    public VariableValueTypedMultiGetSuperSliceQueryImpl(Keyspace ks, SupercolumnFamily<K, SN, N> scf,
            SupercolumnRange<K, SN, N> supercolumnRange, SN start, SN finish, boolean reversed, int count) {
        QueryUtils.checkSupercolumnBelongsToFamily(scf, supercolumnRange);

        wrappedQuery = HFactory.createMultigetSuperSliceQuery(ks, scf.getKeySerializer(),
                    scf.getSupercolumnNameSerializer(), scf.getSubcolumnNameSerializer(),
                    DummySerializer.get())
                .setColumnFamily(scf.getName())
                .setRange(start, finish, reversed, count);
    }

    @Override
    public VariableValueTypedMultiGetSuperSliceQuery<K, SN, N> setKeys(K... keys) {
        wrappedQuery.setKeys(keys);
        return this;
    }

    @Override
    public VariableValueTypedMultiGetSuperSliceQuery<K, SN, N> setKeys(Collection<K> keys) {
        wrappedQuery.setKeys(keys);
        return this;
    }

    @Override
    public QueryResult<VariableValueTypedSuperRows<K, SN, N>> execute() {
        QueryResult<SuperRows<K, SN, N, Object>> wrappedResult = wrappedQuery.execute();

        return new QueryResultImpl<VariableValueTypedSuperRows<K, SN, N>>(
                new ExecutionResult<VariableValueTypedSuperRows<K, SN, N>>(
                        new VariableValueTypedSuperRowsImpl<K, SN, N>(wrappedResult.get()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }
}
