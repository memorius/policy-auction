package net.retakethe.policyauction.data.impl;

import static net.retakethe.policyauction.util.CollectionUtils.list;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import net.retakethe.policyauction.data.api.PolicyDAO;
import net.retakethe.policyauction.data.api.PolicyID;
import net.retakethe.policyauction.data.api.PolicyManager;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.util.Functional;
import net.retakethe.policyauction.util.Functional.Filter;
import net.retakethe.policyauction.util.Functional.SkippedElementException;

/**
 * @author Nick Clarke
 */
public class HectorPolicyManagerImpl extends AbstractHectorDAOManager implements PolicyManager {

    private final Keyspace _keyspace;

    public HectorPolicyManagerImpl(Keyspace keyspace) {
        super();
        if (keyspace == null) {
            throw new IllegalArgumentException("keyspace must not be null");
        }
        _keyspace = keyspace;
    }

    @Override
    public PolicyID makePolicyID(String idString) {
        return new HectorPolicyIDImpl(idString);
    }

    @Override
    public PolicyDAO getPolicy(PolicyID policyID) throws NoSuchPolicyException {
        HectorPolicyIDImpl idImpl = getPolicyIDImpl(policyID);
        SliceQuery<UUID, String, String> q =
            HFactory.createSliceQuery(_keyspace,
                    Schema.POLICIES.getKeySerializer(), StringSerializer.get(), StringSerializer.get())
                .setColumnFamily(Schema.POLICIES.getName())
                .setKey(idImpl.getUUID())
                .setColumnNames(Schema.POLICIES.SHORT_NAME.getName(),
                                Schema.POLICIES.DESCRIPTION.getName());
        QueryResult<ColumnSlice<String, String>> result = q.execute();
        ColumnSlice<String, String> cs = result.get();
        if (cs == null) {
            // TODO: is this what happens if it's not found? Or do we get a non-null result but null columns?
            throw new NoSuchPolicyException(policyID);
        }

        String shortName = getStringColumnOrNull(cs, Schema.POLICIES.SHORT_NAME.getName());
        String description = getStringColumnOrNull(cs, Schema.POLICIES.DESCRIPTION.getName());

        return new PolicyDAOImpl(idImpl, shortName, description);
    }

    @Override
    public PolicyDAO createPolicy() {
        UUID uuid = TimeUUIDUtils.getUniqueTimeUUIDinMillis();
        HectorPolicyIDImpl policyID = new HectorPolicyIDImpl(uuid);
        return new PolicyDAOImpl(policyID);
    }

    @Override
    public List<PolicyDAO> getAllPolicies() {
        RangeSlicesQuery<UUID, String, String> query =
                Schema.POLICIES.createRangeSlicesQuery(_keyspace,
                        list(Schema.POLICIES.SHORT_NAME,
                             Schema.POLICIES.DESCRIPTION));

        // TODO: may need paging of data once we have more than a few hundred.
        //       This may need some sort of indexing since we're using RandomPartitioner,
        //       in order to return them in a useful order.
        query.setRowCount(1000);
        // TODO: needed?
        // query.setKeys("fake_key_0", "fake_key_4");

        QueryResult<OrderedRows<UUID, String, String>> result = query.execute();

        OrderedRows<UUID, String, String> orderedRows = result.get();
        if (orderedRows == null) {
            return Collections.emptyList();
        }

        return Functional.filter(orderedRows.getList(),
                new Filter<Row<UUID, String, String>, PolicyDAO>() {
                    @Override
                    public PolicyDAO filter(Row<UUID, String, String> row) throws SkippedElementException {
                        ColumnSlice<String, String> cs = row.getColumnSlice();
                        if (cs == null) {
                            throw new SkippedElementException();
                        }

                        String shortName;
                        try {
                            shortName = getNonNullStringColumn(cs, Schema.POLICIES.SHORT_NAME.getName());
                        } catch (NoSuchColumnException e) {
                            throw new SkippedElementException();
                        }

                        String description = getStringColumnOrNull(cs, Schema.POLICIES.DESCRIPTION.getName());

                        return new PolicyDAOImpl(new HectorPolicyIDImpl(row.getKey()), shortName, description);
                    }
                });
    }

    @Override
    public void persist(PolicyDAO policy) {
        PolicyDAOImpl impl = getImpl(policy, PolicyDAOImpl.class);

        UUID policyID = impl.getPolicyID().getUUID();

        Mutator<UUID> m = Schema.POLICIES.createMutator(_keyspace);

        Schema.POLICIES.addExistsMarker(m, policyID);
        Schema.POLICIES.SHORT_NAME.addInsertion(m, policyID, policy.getShortName());
        Schema.POLICIES.DESCRIPTION.addInsertion(m, policyID, policy.getDescription());

        // TODO: error handling? Throws HectorException.
        m.execute();
    }
}
