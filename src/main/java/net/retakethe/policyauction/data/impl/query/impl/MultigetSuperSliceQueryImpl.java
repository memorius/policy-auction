package net.retakethe.policyauction.data.impl.query.impl;

import java.util.Collection;
import java.util.List;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.impl.query.api.MultigetSuperSliceQuery;
import net.retakethe.policyauction.data.impl.query.api.SuperRows;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.serializers.DummySerializer;

public class MultigetSuperSliceQueryImpl<K, T extends Timestamp, SN, N> implements
        MultigetSuperSliceQuery<K, T, SN, N> {

    private final me.prettyprint.hector.api.query.MultigetSuperSliceQuery<K, SN, N, Object> wrappedQuery;

    public MultigetSuperSliceQueryImpl(Keyspace ks, SupercolumnFamily<K, T, SN, N> scf,
            List<NamedSupercolumn<K, T, SN, N>> supercolumns) {
        SN[] supercolumnNames = QueryUtils.getSupercolumnNamesUnresolved(scf, supercolumns);

        wrappedQuery = HFactory.createMultigetSuperSliceQuery(ks, scf.getKeySerializer(),
                    scf.getSupercolumnNameSerializer(), scf.getSubcolumnNameSerializer(),
                    DummySerializer.get())
                .setColumnFamily(scf.getName())
                .setColumnNames(supercolumnNames);
    }

    public MultigetSuperSliceQueryImpl(Keyspace ks, SupercolumnFamily<K, T, SN, N> scf,
            SupercolumnRange<K, T, SN, N> supercolumnRange, SN start, SN finish, boolean reversed, int count) {
        QueryUtils.checkSupercolumnBelongsToFamily(scf, supercolumnRange);

        wrappedQuery = HFactory.createMultigetSuperSliceQuery(ks, scf.getKeySerializer(),
                    scf.getSupercolumnNameSerializer(), scf.getSubcolumnNameSerializer(),
                    DummySerializer.get())
                .setColumnFamily(scf.getName())
                .setRange(start, finish, reversed, count);
    }

    @Override
    public MultigetSuperSliceQuery<K, T, SN, N> setKeys(K... keys) {
        wrappedQuery.setKeys(keys);
        return this;
    }

    @Override
    public MultigetSuperSliceQuery<K, T, SN, N> setKeys(Collection<K> keys) {
        wrappedQuery.setKeys(keys);
        return this;
    }

    @Override
    public QueryResult<SuperRows<K, T, SN, N>> execute() {
        QueryResult<me.prettyprint.hector.api.beans.SuperRows<K, SN, N, Object>> wrappedResult = wrappedQuery.execute();

        return new QueryResultImpl<SuperRows<K, T, SN, N>>(
                new ExecutionResult<SuperRows<K, T, SN, N>>(
                        new SuperRowsImpl<K, T, SN, N>(wrappedResult.get()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }
}
