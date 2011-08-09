package net.retakethe.policyauction.data.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.api.PolicyManager;
import net.retakethe.policyauction.data.api.dao.PolicyDAO;
import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.data.impl.dao.PolicyDAOImpl;
import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedColumnSlice;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedOrderedRows;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRangeSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRow;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSliceQuery;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.types.HectorPolicyIDImpl;
import net.retakethe.policyauction.util.CollectionUtils;
import net.retakethe.policyauction.util.Functional;
import net.retakethe.policyauction.util.Functional.Filter;
import net.retakethe.policyauction.util.Functional.SkippedElementException;

/**
 * @author Nick Clarke
 */
public class HectorPolicyManagerImpl extends AbstractHectorDAOManager implements PolicyManager {

    private final KeyspaceManager keyspaceManager;

    public HectorPolicyManagerImpl(KeyspaceManager keyspaceManager) {
        super();
        if (keyspaceManager == null) {
            throw new IllegalArgumentException("keyspace must not be null");
        }
        this.keyspaceManager = keyspaceManager;
    }

    @Override
    public PolicyID makePolicyID(String idString) {
        return new HectorPolicyIDImpl(idString);
    }

    @Override
    public PolicyDAO getPolicy(PolicyID policyID) throws NoSuchPolicyException {
        HectorPolicyIDImpl idImpl = getPolicyIDImpl(policyID);
        UUID key = idImpl.getUUID();

        List<NamedColumn<UUID, String, ?>> list = CollectionUtils.list(
                (NamedColumn<UUID, String, ?>) Schema.POLICIES.SHORT_NAME,
                (NamedColumn<UUID, String, ?>) Schema.POLICIES.DESCRIPTION,
                (NamedColumn<UUID, String, ?>) Schema.POLICIES.LAST_EDITED);
        VariableValueTypedSliceQuery<UUID, String> query =
                Schema.POLICIES.createVariableValueTypedSliceQuery(keyspaceManager, key, list);

        QueryResult<VariableValueTypedColumnSlice<String>> queryResult = query.execute();

        VariableValueTypedColumnSlice<String> cs = queryResult.get();

        String shortName;
        String description;
        Date lastEdited;
        try {
            shortName = getNonNullColumn(cs, Schema.POLICIES.SHORT_NAME);
        } catch (NoSuchColumnException e) {
            throw new NoSuchPolicyException(policyID);
        }
        try {
            description = getNonNullColumn(cs, Schema.POLICIES.DESCRIPTION);
            lastEdited = getNonNullColumn(cs, Schema.POLICIES.LAST_EDITED);
        } catch (NoSuchColumnException e) {
            throw new RuntimeException("Invalid policy record for key " + key, e);
        }

        return new PolicyDAOImpl(idImpl, shortName, description, lastEdited);
    }

    @Override
    public PolicyDAO createPolicy() {
        UUID uuid = TimeUUIDUtils.getUniqueTimeUUIDinMillis();
        HectorPolicyIDImpl policyID = new HectorPolicyIDImpl(uuid);
        return new PolicyDAOImpl(policyID);
    }

    @Override
    public List<PolicyDAO> getAllPolicies() {
        List<NamedColumn<UUID, String, ?>> list = CollectionUtils.list(
                (NamedColumn<UUID, String, ?>) Schema.POLICIES.SHORT_NAME,
                (NamedColumn<UUID, String, ?>) Schema.POLICIES.DESCRIPTION,
                (NamedColumn<UUID, String, ?>) Schema.POLICIES.LAST_EDITED);
        VariableValueTypedRangeSlicesQuery<UUID, String> query =
                Schema.POLICIES.createVariableValueTypedRangeSlicesQuery(keyspaceManager,
                        list);

        // TODO: may need paging of data once we have more than a few hundred.
        //       This may need some sort of indexing since we're using RandomPartitioner,
        //       in order to return them in a useful order.
        query.setRowCount(1000);
        // TODO: needed?
        // query.setKeys("fake_key_0", "fake_key_4");

        QueryResult<VariableValueTypedOrderedRows<UUID, String>> result = query.execute();

        VariableValueTypedOrderedRows<UUID, String> orderedRows = result.get();
        if (orderedRows == null) {
            return Collections.emptyList();
        }

        return Functional.filter(orderedRows.getList(),
                new Filter<VariableValueTypedRow<UUID, String>, PolicyDAO>() {
                    @Override
                    public PolicyDAO filter(VariableValueTypedRow<UUID, String> row) throws SkippedElementException {
                        VariableValueTypedColumnSlice<String> cs = row.getColumnSlice();
                        if (cs == null) {
                            throw new SkippedElementException();
                        }

                        String shortName;
                        try {
                            shortName = getNonNullColumn(cs, Schema.POLICIES.SHORT_NAME);
                        } catch (NoSuchColumnException e) {
                            // Tombstone row
                            throw new SkippedElementException();
                        }

                        String description;
                        Date lastEdited;
                        try {
                            description = getNonNullColumn(cs, Schema.POLICIES.DESCRIPTION);
                            lastEdited = getNonNullColumn(cs, Schema.POLICIES.LAST_EDITED);
                        } catch (NoSuchColumnException e) {
                            throw new RuntimeException("Invalid policy record for key " + row.getKey(), e);
                        }

                        return new PolicyDAOImpl(new HectorPolicyIDImpl(row.getKey()), shortName, description,
                                lastEdited);
                    }
                });
    }

    @Override
    public void persist(PolicyDAO policy) {
        PolicyDAOImpl impl = getImpl(policy, PolicyDAOImpl.class);
        UUID policyID = impl.getPolicyID().getUUID();

        MutatorWrapper<UUID> m = Schema.POLICIES.createMutator(keyspaceManager);

        Schema.POLICIES.SHORT_NAME.addColumnInsertion(m, policyID, policy.getShortName());
        Schema.POLICIES.DESCRIPTION.addColumnInsertion(m, policyID, policy.getDescription());

        // We're saving changes, so update the edit time
        Schema.POLICIES.LAST_EDITED.addColumnInsertion(m, policyID, new Date());

        // TODO: error handling? Throws HectorException.
        m.execute();
    }

    @Override
    public void deletePolicy(PolicyDAO policy) {
        PolicyDAOImpl impl = getImpl(policy, PolicyDAOImpl.class);
        UUID policyID = impl.getPolicyID().getUUID();

        MutatorWrapper<UUID> m = Schema.POLICIES.createMutator(keyspaceManager);

        Schema.POLICIES.addRowDeletion(m, policyID);

        m.execute();

        // TODO: this will need to delete from other ColumnFamilies too and trigger recalcs
    }
}
