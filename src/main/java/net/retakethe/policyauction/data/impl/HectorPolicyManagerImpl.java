package net.retakethe.policyauction.data.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import net.retakethe.policyauction.data.api.PolicyID;
import net.retakethe.policyauction.data.api.PolicyManager;
import net.retakethe.policyauction.entities.Policy;

/**
 * @author Nick Clarke
 */
public class HectorPolicyManagerImpl extends AbstractHectorDAO implements PolicyManager {

    private static final class HectorPolicyIDImpl implements PolicyID {

        private final UUID _uuid;

        public HectorPolicyIDImpl(String idString) {
            if (idString == null) {
                throw new IllegalArgumentException("idString must not be null");
            }
            _uuid = UUID.fromString(idString);
        }

        public HectorPolicyIDImpl(UUID uuid) {
            _uuid = uuid;
        }

        @Override
        public String asString() {
            return _uuid.toString();
        }

        @Override
        public String toString() {
            return asString();
        }

        public UUID getUUID() {
            return _uuid;
        }
    }

    private static final String POLICIES_COLUMN_FAMILY = "policies";

    private static final String SHORT_NAME = "short_name";
    private static final String DESCRIPTION = "description";

    private final HectorCassandraDAOManagerImpl _daoManager;

    public HectorPolicyManagerImpl(HectorCassandraDAOManagerImpl daoManager) {
        super();
        _daoManager = daoManager;
    }

    @Override
    public PolicyID makePolicyID(String idString) {
        return new HectorPolicyIDImpl(idString);
    }

    @Override
    public Policy getPolicy(PolicyID policyID) throws NoSuchPolicyException {
        HectorPolicyIDImpl idImpl = getPolicyIDImpl(policyID);
        SliceQuery<UUID, String, String> q =
            HFactory.createSliceQuery(_daoManager.getKeyspace(),
                    UUIDSerializer.get(), StringSerializer.get(), StringSerializer.get())
                .setColumnFamily(POLICIES_COLUMN_FAMILY)
                .setKey(idImpl.getUUID())
                .setColumnNames(SHORT_NAME, DESCRIPTION);
        QueryResult<ColumnSlice<String, String>> result = q.execute();
        ColumnSlice<String, String> cs = result.get();
        if (cs == null) {
            // TODO: is this what happens if it's not found? Or do we get a non-null result but null columns?
            throw new NoSuchPolicyException(policyID);
        }

        String shortName = getStringColumnOrNull(cs, SHORT_NAME);
        String description = getStringColumnOrNull(cs, DESCRIPTION);

        return new Policy(idImpl, shortName, description);
    }

    private HectorPolicyIDImpl getPolicyIDImpl(PolicyID policyID) {
        return (HectorPolicyIDImpl) policyID;
    }

    @Override
    public Policy createPolicy() {
        UUID uuid = TimeUUIDUtils.getUniqueTimeUUIDinMillis();
        HectorPolicyIDImpl policyID = new HectorPolicyIDImpl(uuid);
        return new Policy(policyID);
    }

    @Override
    public List<Policy> getAllPolicies() {
        RangeSlicesQuery<UUID, String, String> rangeSlicesQuery =
            HFactory.createRangeSlicesQuery(_daoManager.getKeyspace(),
                    UUIDSerializer.get(), StringSerializer.get(), StringSerializer.get())
                .setColumnFamily(POLICIES_COLUMN_FAMILY)
                // TODO: may need paging of data once we have more than a few hundred.
                //       This may need some sort of indexing since we're using RandomPartitioner,
                //       in order to return them in a useful order.
                .setRowCount(1000)
                // TODO: needed?
                // .setKeys("fake_key_0", "fake_key_4");
                // TODO: needed?
                // .setRange("", "", false, 3)
                .setColumnNames(SHORT_NAME, DESCRIPTION);

        QueryResult<OrderedRows<UUID, String, String>> result = rangeSlicesQuery.execute();

        OrderedRows<UUID, String, String> orderedRows = result.get();
        if (orderedRows == null) {
            return Collections.emptyList();
        }

        List<Row<UUID, String, String>> rowList = orderedRows.getList();
        List<Policy> policies = new ArrayList<Policy>(rowList.size());
        for (Row<UUID, String, String> row : rowList) {
            ColumnSlice<String, String> cs = row.getColumnSlice();
            if (cs == null) {
                continue;
            }

            String shortName;
            try {
                shortName = getNonNullStringColumn(cs, SHORT_NAME);
            } catch (NoSuchColumnException e) {
                continue; // Tombstone (deleted item) reappeared in range scan. Ignore, will eventually be GC'd.
            }

            String description = getStringColumnOrNull(cs, DESCRIPTION);

            policies.add(new Policy(new HectorPolicyIDImpl(row.getKey()), shortName, description));
        }

        return policies;
    }

    @Override
    public void storePolicy(Policy policy) {
        UUID policyID = getInternalPolicyID(policy);
        Mutator<UUID> mutator = HFactory.createMutator(_daoManager.getKeyspace(), UUIDSerializer.get())
                .addInsertion(policyID, POLICIES_COLUMN_FAMILY,
                              HFactory.createStringColumn(SHORT_NAME, policy.getShortName()))
                .addInsertion(policyID, POLICIES_COLUMN_FAMILY,
                              HFactory.createStringColumn(DESCRIPTION, policy.getDescription()));

        // TODO: error handling? Throws HectorException.
        mutator.execute();
    }

    private UUID getInternalPolicyID(Policy policy) {
        return ((HectorPolicyIDImpl) policy.getPolicyID()).getUUID();
    }
}
