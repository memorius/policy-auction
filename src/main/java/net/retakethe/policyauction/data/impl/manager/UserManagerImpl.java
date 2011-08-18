package net.retakethe.policyauction.data.impl.manager;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.api.UserManager;
import net.retakethe.policyauction.data.api.dao.PolicyDAO;
import net.retakethe.policyauction.data.api.dao.UserDAO;
import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.impl.dao.UserDAOImpl;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedColumnSlice;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedOrderedRows;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRangeSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedRow;
import net.retakethe.policyauction.data.impl.query.api.VariableValueTypedSliceQuery;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.data.impl.schema.Schema.UsersCF;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.MillisTimestamp;
import net.retakethe.policyauction.data.impl.types.UserIDImpl;
import net.retakethe.policyauction.util.CollectionUtils;
import net.retakethe.policyauction.util.Functional;
import net.retakethe.policyauction.util.Functional.Filter;
import net.retakethe.policyauction.util.Functional.SkippedElementException;

/**
 * @author Mathew Hartley
 */
public class UserManagerImpl extends AbstractDAOManagerImpl implements UserManager {

    private final KeyspaceManager keyspaceManager;

    public UserManagerImpl(KeyspaceManager keyspaceManager) {
        super();
        if (keyspaceManager == null) {
            throw new IllegalArgumentException("keyspace must not be null");
        }
        this.keyspaceManager = keyspaceManager;
    }

    @Override
    public UserID makeUserID(String idString) {
        return new UserIDImpl(idString);
    }

    @Override
    public UserDAO getUser(UserID userID) throws NoSuchUserException {
        List<NamedColumn<UserID, MillisTimestamp, String, ?>> list = CollectionUtils.list(
                (NamedColumn<UserID, MillisTimestamp, String, ?>) Schema.USERS.USERNAME,
                (NamedColumn<UserID, MillisTimestamp, String, ?>) Schema.USERS.PASSWORD_HASH,
                (NamedColumn<UserID, MillisTimestamp, String, ?>) Schema.USERS.EMAIL,
                (NamedColumn<UserID, MillisTimestamp, String, ?>) Schema.USERS.FIRST_NAME,
                (NamedColumn<UserID, MillisTimestamp, String, ?>) Schema.USERS.LAST_NAME,
                (NamedColumn<UserID, MillisTimestamp, String, ?>) Schema.USERS.SHOW_REAL_NAME,
                (NamedColumn<UserID, MillisTimestamp, String, ?>) Schema.USERS.CREATED_TIMESTAMP);
        VariableValueTypedSliceQuery<UserID, MillisTimestamp, String> query =
                Schema.USERS.createVariableValueTypedSliceQuery(keyspaceManager, userID, list);

        QueryResult<VariableValueTypedColumnSlice<MillisTimestamp, String>> queryResult = query.execute();

        VariableValueTypedColumnSlice<MillisTimestamp, String> cs = queryResult.get();

        String shortName;
        String description;
        Date lastEdited;
        try {
            shortName = getNonNullColumn(cs, Schema.USERS.SHORT_NAME);
        } catch (NoSuchColumnException e) {
            throw new NoSuchUserException(userID);
        }
        try {
            description = getNonNullColumn(cs, Schema.USERS.DESCRIPTION);
            lastEdited = getNonNullColumn(cs, Schema.USERS.LAST_EDITED);
        } catch (NoSuchColumnException e) {
            throw new RuntimeException("Invalid user record for key " + userID, e);
        }

        return new UserDAOImpl(userID, shortName, description, description, description, description, null, lastEdited, lastEdited, lastEdited, null);
    }

    @Override
    public UserDAO createUser() {
        return new UserDAOImpl(new UserIDImpl());
    }

    @Override
    public List<UserDAO> getAllUsers() {
        List<NamedColumn<UserID, MillisTimestamp, String, ?>> list = CollectionUtils.list(
                (NamedColumn<PolicyID, MillisTimestamp, String, ?>) Schema.USERS.SHORT_NAME,
                (NamedColumn<PolicyID, MillisTimestamp, String, ?>) Schema.USERS.DESCRIPTION,
                (NamedColumn<PolicyID, MillisTimestamp, String, ?>) Schema.USERS.LAST_EDITED);
        VariableValueTypedRangeSlicesQuery<UserID, MillisTimestamp, String> query =
                Schema.USERS.createVariableValueTypedRangeSlicesQuery(keyspaceManager,
                        list);

        // TODO: may need paging of data once we have more than a few hundred.
        //       This may need some sort of indexing since we're using RandomPartitioner,
        //       in order to return them in a useful order.
        query.setRowCount(1000);
        // TODO: needed?
        // query.setKeys("fake_key_0", "fake_key_4");

        QueryResult<VariableValueTypedOrderedRows<UserID, MillisTimestamp, String>> result = query.execute();

        VariableValueTypedOrderedRows<UserID, MillisTimestamp, String> orderedRows = result.get();
        if (orderedRows == null) {
            return Collections.emptyList();
        }

        return Functional.filter(orderedRows.getList(),
                new Filter<VariableValueTypedRow<PolicyID, MillisTimestamp, String>, PolicyDAO>() {
                    @Override
                    public PolicyDAO filter(VariableValueTypedRow<PolicyID, MillisTimestamp, String> row)
                            throws SkippedElementException {
                        VariableValueTypedColumnSlice<MillisTimestamp, String> cs = row.getColumnSlice();
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
                            throw new RuntimeException("Invalid user record for key " + row.getKey(), e);
                        }

                        return new UserDAOImpl(row.getKey(), shortName, description,
                                lastEdited);
                    }
                });
    }

    @Override
    public void persist(UserDAO user) {
        PolicyID policyID = user.getPolicyID();

        UsersCF cf = Schema.USERS;
        MillisTimestamp ts = cf.createCurrentTimestamp();
        MutatorWrapper<UserID, MillisTimestamp> m = cf.createMutator(keyspaceManager);

        cf.SHORT_NAME.addColumnInsertion(m, policyID, cf.createValue(user.getShortName(), ts));
        cf.DESCRIPTION.addColumnInsertion(m, policyID, cf.createValue(user.getDescription(), ts));

        // We're saving changes, so update the edit time
        cf.LAST_EDITED.addColumnInsertion(m, policyID, cf.createValue(new Date(), ts));

        // TODO: error handling? Throws HectorException.
        m.execute();
    }

    @Override
    public void deleteUser(UserDAO user) {
        UserID userID = user.getUserID();

        MutatorWrapper<UserID, MillisTimestamp> m = Schema.USERS.createMutator(keyspaceManager);

        Schema.USERS.addRowDeletion(m, userID);

        m.execute();

        // TODO: this will need to delete from other ColumnFamilies too and trigger recalcs
    }
}
