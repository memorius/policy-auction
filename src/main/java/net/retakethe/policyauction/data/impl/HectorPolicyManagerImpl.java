package net.retakethe.policyauction.data.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

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
        List<NamedColumn<PolicyID, String, ?>> list = CollectionUtils.list(
                (NamedColumn<PolicyID, String, ?>) Schema.POLICIES.SHORT_NAME,
                (NamedColumn<PolicyID, String, ?>) Schema.POLICIES.DESCRIPTION,
                (NamedColumn<PolicyID, String, ?>) Schema.POLICIES.LAST_EDITED);
        VariableValueTypedSliceQuery<PolicyID, String> query =
                Schema.POLICIES.createVariableValueTypedSliceQuery(keyspaceManager, policyID, list);

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
            throw new RuntimeException("Invalid policy record for key " + policyID, e);
        }

        return new PolicyDAOImpl(policyID, shortName, description, lastEdited);
    }

    @Override
    public PolicyDAO createPolicy() {
        return new PolicyDAOImpl(new HectorPolicyIDImpl());
    }

    @Override
    public List<PolicyDAO> getAllPolicies() {
        List<NamedColumn<PolicyID, String, ?>> list = CollectionUtils.list(
                (NamedColumn<PolicyID, String, ?>) Schema.POLICIES.SHORT_NAME,
                (NamedColumn<PolicyID, String, ?>) Schema.POLICIES.DESCRIPTION,
                (NamedColumn<PolicyID, String, ?>) Schema.POLICIES.LAST_EDITED);
        VariableValueTypedRangeSlicesQuery<PolicyID, String> query =
                Schema.POLICIES.createVariableValueTypedRangeSlicesQuery(keyspaceManager,
                        list);

        // TODO: may need paging of data once we have more than a few hundred.
        //       This may need some sort of indexing since we're using RandomPartitioner,
        //       in order to return them in a useful order.
        query.setRowCount(1000);
        // TODO: needed?
        // query.setKeys("fake_key_0", "fake_key_4");

        QueryResult<VariableValueTypedOrderedRows<PolicyID, String>> result = query.execute();

        VariableValueTypedOrderedRows<PolicyID, String> orderedRows = result.get();
        if (orderedRows == null) {
            return Collections.emptyList();
        }

        return Functional.filter(orderedRows.getList(),
                new Filter<VariableValueTypedRow<PolicyID, String>, PolicyDAO>() {
                    @Override
                    public PolicyDAO filter(VariableValueTypedRow<PolicyID, String> row) throws SkippedElementException {
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

                        return new PolicyDAOImpl(row.getKey(), shortName, description,
                                lastEdited);
                    }
                });
    }

    @Override
    public void persist(PolicyDAO policy) {
        PolicyID policyID = policy.getPolicyID();

        MutatorWrapper<PolicyID> m = Schema.POLICIES.createMutator(keyspaceManager);

        Schema.POLICIES.SHORT_NAME.addColumnInsertion(m, policyID, policy.getShortName());
        Schema.POLICIES.DESCRIPTION.addColumnInsertion(m, policyID, policy.getDescription());

        // We're saving changes, so update the edit time
        Schema.POLICIES.LAST_EDITED.addColumnInsertion(m, policyID, new Date());

        // TODO: error handling? Throws HectorException.
        m.execute();
    }

    @Override
    public void deletePolicy(PolicyDAO policy) {
        PolicyID policyID = policy.getPolicyID();

        MutatorWrapper<PolicyID> m = Schema.POLICIES.createMutator(keyspaceManager);

        Schema.POLICIES.addRowDeletion(m, policyID);

        m.execute();

        // TODO: this will need to delete from other ColumnFamilies too and trigger recalcs
    }
}
