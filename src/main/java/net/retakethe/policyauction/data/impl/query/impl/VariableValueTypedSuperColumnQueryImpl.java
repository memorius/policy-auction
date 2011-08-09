package net.retakethe.policyauction.data.impl.query.impl;

import me.prettyprint.cassandra.model.ExecutionResult;
import me.prettyprint.cassandra.model.QueryResultImpl;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HSuperColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SuperColumnQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSupercolumn;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSupercolumnQuery;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.supercolumn.Supercolumn;
import net.retakethe.policyauction.data.impl.serializers.DummySerializer;

public class VariableValueTypedSuperColumnQueryImpl<K, SN, N> implements VariableValueTypedSupercolumnQuery<SN, N> {

    private final SuperColumnQuery<K, SN, N, Object> wrappedQuery;

    public VariableValueTypedSuperColumnQueryImpl(Keyspace ks, SupercolumnFamily<K, SN, N> scf,
            Supercolumn<K, SN, N> supercolumn, SN supercolumnName, K key) {
        QueryUtils.checkSupercolumnBelongsToFamily(scf, supercolumn);

        wrappedQuery = HFactory.createSuperColumnQuery(ks, scf.getKeySerializer(), scf.getSupercolumnNameSerializer(),
                scf.getSubcolumnNameSerializer(), DummySerializer.get())
                .setColumnFamily(scf.getName())
                .setSuperName(supercolumnName)
                .setKey(key);
    }

    @Override
    public QueryResult<VariableValueTypedSupercolumn<SN, N>> execute() {
        QueryResult<HSuperColumn<SN, N, Object>> wrappedResult = wrappedQuery.execute();

        return new QueryResultImpl<VariableValueTypedSupercolumn<SN, N>>(
                new ExecutionResult<VariableValueTypedSupercolumn<SN, N>>(
                        new VariableValueTypedSupercolumnImpl<SN, N>(wrappedResult.get()),
                        wrappedResult.getExecutionTimeMicro(),
                        wrappedResult.getHostUsed()),
                this);
    }
}
