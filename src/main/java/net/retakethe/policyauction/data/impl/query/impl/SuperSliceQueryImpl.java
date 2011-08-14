package net.retakethe.policyauction.data.impl.query.impl;

import java.util.List;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.impl.query.api.SuperSlice;
import net.retakethe.policyauction.data.impl.query.api.SuperSliceQuery;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.serializers.DummySerializer;

public class SuperSliceQueryImpl<K, T extends Timestamp, SN, N>
        implements SuperSliceQuery<K, T, SN, N> {

    private final me.prettyprint.hector.api.query.SuperSliceQuery<K, SN, N, Object> wrappedQuery;

    public SuperSliceQueryImpl(Keyspace ks, SupercolumnFamily<K, T, SN, N> scf,
            K key, List<NamedSupercolumn<K, T, SN, N>> supercolumns) {
        SN[] supercolumnNames = QueryUtils.getSupercolumnNamesUnresolved(scf, supercolumns);

        wrappedQuery = HFactory.createSuperSliceQuery(ks, scf.getKeySerializer(),
                    scf.getSupercolumnNameSerializer(), scf.getSubcolumnNameSerializer(),
                    DummySerializer.get())
                .setColumnFamily(scf.getName())
                .setColumnNames(supercolumnNames)
                .setKey(key);
    }

    public SuperSliceQueryImpl(Keyspace ks, SupercolumnFamily<K, T, SN, N> scf, K key,
            SupercolumnRange<K, T, SN, N> supercolumnRange, SN start, SN finish, boolean reversed, int count) {
        QueryUtils.checkSupercolumnBelongsToFamily(scf, supercolumnRange);

        wrappedQuery = HFactory.createSuperSliceQuery(ks, scf.getKeySerializer(),
                scf.getSupercolumnNameSerializer(), scf.getSubcolumnNameSerializer(),
                DummySerializer.get())
            .setColumnFamily(scf.getName())
            .setRange(start, finish, reversed, count)
            .setKey(key);
    }

    @Override
    public QueryResult<SuperSlice<T, SN, N>> execute() {
        QueryResult<me.prettyprint.hector.api.beans.SuperSlice<SN, N, Object>> wrappedResult = wrappedQuery.execute();

        return new QueryResultImpl<SuperSlice<T, SN, N>>(
                new ExecutionResult<SuperSlice<T, SN, N>>(
                        new SuperSliceImpl<T, SN, N>(wrappedResult.get()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }

}
