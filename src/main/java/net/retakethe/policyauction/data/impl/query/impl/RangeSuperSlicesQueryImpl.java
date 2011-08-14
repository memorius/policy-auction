package net.retakethe.policyauction.data.impl.query.impl;

import java.util.List;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.impl.query.api.OrderedSuperRows;
import net.retakethe.policyauction.data.impl.query.api.RangeSuperSlicesQuery;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.supercolumn.NamedSupercolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.serializers.DummySerializer;

public class RangeSuperSlicesQueryImpl<K, T extends Timestamp, SN, N> implements
        RangeSuperSlicesQuery<K, T, SN, N> {

    private final me.prettyprint.hector.api.query.RangeSuperSlicesQuery<K, SN, N, Object> wrappedQuery;

    public RangeSuperSlicesQueryImpl(Keyspace ks, SupercolumnFamily<K, T, SN, N> scf,
            List<NamedSupercolumn<K, T, SN, N>> supercolumns) {
        SN[] supercolumnNames = QueryUtils.getSupercolumnNamesUnresolved(scf, supercolumns);

        wrappedQuery = HFactory.createRangeSuperSlicesQuery(ks, scf.getKeySerializer(),
                    scf.getSupercolumnNameSerializer(), scf.getSubcolumnNameSerializer(),
                    DummySerializer.get())
                .setColumnFamily(scf.getName())
                .setColumnNames(supercolumnNames);
    }

    public RangeSuperSlicesQueryImpl(Keyspace ks, SupercolumnFamily<K, T, SN, N> scf,
            SupercolumnRange<K, T, SN, N> supercolumnRange, SN start, SN finish, boolean reversed, int count) {
        QueryUtils.checkSupercolumnBelongsToFamily(scf, supercolumnRange);

        wrappedQuery = HFactory.createRangeSuperSlicesQuery(ks, scf.getKeySerializer(),
                    scf.getSupercolumnNameSerializer(), scf.getSubcolumnNameSerializer(),
                    DummySerializer.get())
                .setColumnFamily(scf.getName())
                .setRange(start, finish, reversed, count);
    }

    @Override
    public RangeSuperSlicesQuery<K, T, SN, N> setKeys(K start, K end) {
        wrappedQuery.setKeys(start, end);
        return this;
    }

    @Override
    public RangeSuperSlicesQuery<K, T, SN, N> setRowCount(int rowCount) {
        wrappedQuery.setRowCount(rowCount);
        return this;
    }

    @Override
    public QueryResult<OrderedSuperRows<K, T, SN, N>> execute() {
        QueryResult<me.prettyprint.hector.api.beans.OrderedSuperRows<K, SN, N, Object>> wrappedResult =
                wrappedQuery.execute();

        return new QueryResultImpl<OrderedSuperRows<K, T, SN, N>>(
                new ExecutionResult<OrderedSuperRows<K, T, SN, N>>(
                        new OrderedSuperRowsImpl<K, T, SN, N>(wrappedResult.get()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }
}
