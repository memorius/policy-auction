package net.retakethe.policyauction.data.impl.manager;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.api.UserVoteManager;
import net.retakethe.policyauction.data.api.dao.CurrentUserVotesDAO;
import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.impl.dao.CurrentUserVotesImpl;
import net.retakethe.policyauction.data.impl.dao.PolicyVoteRecord;
import net.retakethe.policyauction.data.impl.query.api.ColumnResult;
import net.retakethe.policyauction.data.impl.query.api.ColumnSlice;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.Mutator;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.data.impl.schema.Schema.UserVotesCF;
import net.retakethe.policyauction.data.impl.schema.timestamp.UniqueTimestamp;
import net.retakethe.policyauction.data.impl.schema.value.Value;
import net.retakethe.policyauction.data.impl.types.PolicyIDImpl;
import net.retakethe.policyauction.data.impl.types.internal.VoteRecordID;
import net.retakethe.policyauction.data.impl.types.internal.VoteRecordIDImpl;
import net.retakethe.policyauction.data.impl.util.UUIDUtils;
import net.retakethe.policyauction.util.AssertArgument;
import net.retakethe.policyauction.util.Functional;

import org.apache.tapestry5.json.JSONObject;

/**
 * @author Nick Clarke
 */
public class UserVoteManagerImpl extends AbstractDAOManagerImpl implements UserVoteManager {

    /**
     * JSON format:
     * <pre>
     * {
     *     parent|version: &lt;voteRecordID>
     *     [votes: {
     *         &lt;policyID> ... : {
     *             vote: int
     *             [penalty: int]
     *             voteTotal: int
     *             penaltyTotal: int
     *         }
     *     }]
     *     [createdPolicy: &lt;policyID>]
     * }
     * </pre>
     */
    private static final class VoteRecord {
        protected static final String VERSION = "version";
        protected static final String PARENT = "parent";
        private static final String CREATED_POLICY = "createdPolicy";
        private static final String VOTES = "votes";

        private final VoteRecordID voteID;
        private final VoteRecordID parentVoteID;
        private final UniqueTimestamp timestamp;
        private final PolicyID createdPolicyID;
        private final Map<PolicyID, PolicyVoteRecord> policyVotes;

        public VoteRecord(VoteRecordID voteID, VoteRecordID parentVoteID, UniqueTimestamp timestamp,
                PolicyID createdPolicyID) {
            this(voteID, parentVoteID, timestamp, createdPolicyID, new HashMap<PolicyID, PolicyVoteRecord>());
        }

        public VoteRecord(VoteRecordID voteID, VoteRecordID parentVoteID, UniqueTimestamp timestamp,
                PolicyID createdPolicyID, Map<PolicyID, PolicyVoteRecord> policyVotes) {
            this.voteID = voteID;
            this.parentVoteID = parentVoteID;
            this.timestamp = timestamp;
            this.createdPolicyID = createdPolicyID;
            this.policyVotes = policyVotes;
        }
        
        protected VoteRecord(VoteRecordID voteID, VoteRecordID parentVoteID, UniqueTimestamp timestamp,
                JSONObject json) {
            this(voteID, parentVoteID, timestamp,
                    (json.has(CREATED_POLICY) ? new PolicyIDImpl(json.getString(CREATED_POLICY)) : null));

            if (json.has(VOTES)) {
                JSONObject votes = json.getJSONObject(VOTES);
                for (String policyID : votes.keys()) {
                    addPolicyVote(new PolicyIDImpl(policyID), new PolicyVoteRecord(votes.getJSONObject(policyID)));
                }
            }
        }

        public void addPolicyVote(PolicyID policyID, PolicyVoteRecord policyVote) {
            policyVotes.put(policyID, policyVote);
        }

        public JSONObject toSelfVersionedJSON() {
            JSONObject o = new JSONObject();
            o.put(PARENT, parentVoteID.asString());
            addVotes(o);
            return o;
        }

        public JSONObject toParentVersionedJSON() {
            JSONObject o = new JSONObject();
            o.put(VERSION, voteID.asString());
            addVotes(o);
            return o;
        }

        public VoteRecordID getVoteID() {
            return voteID;
        }

        public VoteRecordID getParentVoteID() {
            return parentVoteID;
        }

        public UniqueTimestamp getTimestamp() {
            return timestamp;
        }

        /**
         * @return created policy if any - may be null
         */
        public PolicyID getCreatedPolicyID() {
            return createdPolicyID;
        }

        public Map<PolicyID, PolicyVoteRecord> getPolicyVotes() {
            return policyVotes;
        }

        private void addVotes(JSONObject o) {
            if (!policyVotes.isEmpty()) {
                JSONObject votes = new JSONObject();
                for (Map.Entry<PolicyID, PolicyVoteRecord> entry : policyVotes.entrySet()) {
                    votes.put(entry.getKey().asString(), entry.getValue().toJSON());
                }
                o.put(VOTES, votes);
            }
            if (createdPolicyID != null) {
                o.put(CREATED_POLICY, createdPolicyID.asString());
            }
        }

        /**
         * Records whose column name is their own unique ID - designed never to collide.
         */
        public static VoteRecord createSelfVersioned(VoteRecordID columnName, UniqueTimestamp timestamp,
                JSONObject json) {
            return new VoteRecord(columnName, new VoteRecordIDImpl(json.getString(PARENT)), timestamp, json);
        }

        /**
         * Records whose column name is the parent ID - designed to collide if two different records are written.
         */
        public static VoteRecord createParentVersioned(VoteRecordID columnName, UniqueTimestamp timestamp,
                JSONObject json) {
            return new VoteRecord(new VoteRecordIDImpl(json.getString(VERSION)), columnName, timestamp, json);
        }
    }

    private static final VoteRecordID ZERO_VOTE_RECORD_ID = new VoteRecordIDImpl(UUIDUtils.getZeroTimeUUID());

    private static final Comparator<VoteRecord> VOTE_RECORD_COMPARATOR = new Comparator<VoteRecord>() {
            @Override
            public int compare(VoteRecord o1, VoteRecord o2) {
                return ((VoteRecordIDImpl) o1.getVoteID()).compareTo((VoteRecordIDImpl) o2.getVoteID());
            }
        };

    private final KeyspaceManager keyspaceManager;

    public UserVoteManagerImpl(KeyspaceManager keyspaceManager) {
        super();
        if (keyspaceManager == null) {
            throw new IllegalArgumentException("keyspace must not be null");
        }
        this.keyspaceManager = keyspaceManager;
    }

    @Override
    public CurrentUserVotesDAO getCurrentUserVoteAllocation(UserID userID) {
        if (userID == null) {
            throw new IllegalArgumentException("userID must not be null");
        }

        // TODO: Propagate pending votes first.
        // TODO: Load user votes row, return empty allocation record with all-zeros version UUIDs if not found.

        final byte voteWithdrawalPenaltyPercentage = getVoteWithdrawalPenaltyPercentage();
        final long totalVoteSalary = getTotalUserVoteSalary(userID); 

        // TODO: keep "conflicts resolved from <version> onwards" and use this to limit column count
        UserVotesCF cf = Schema.USER_VOTES;
        QueryResult<ColumnSlice<UniqueTimestamp, VoteRecordID>> qr =
                cf.createSliceQuery(keyspaceManager, userID, null, null, false, Integer.MAX_VALUE).execute();

        List<VoteRecord> voteRecords = Functional.map(qr.get().getColumns(cf.getColumnRange()),
                new Functional.Converter<ColumnResult<UniqueTimestamp, VoteRecordID, JSONObject>, VoteRecord>() {
                    @Override
                    public VoteRecord convert(ColumnResult<UniqueTimestamp, VoteRecordID, JSONObject> column) {
                        VoteRecordID recordID = column.getName();
                        Value<UniqueTimestamp, JSONObject> v = column.getValue();
                        UniqueTimestamp ts = v.getTimestamp();
                        JSONObject json = v.getValue();

                        return VoteRecord.createParentVersioned(recordID, ts, json);
                    }
                });
        if (voteRecords.isEmpty()) {
            // First time this user has done anything. Write an empty record and return it.
            return createInitialEmptyVoteAllocation(userID, totalVoteSalary, voteWithdrawalPenaltyPercentage);
        }

        List<VoteRecord> resolvedRecords = resolveCollisions(userID, voteRecords);

        // Get the most recent record, convert to CurrentUserVotesDAO and return.
        return toCurrentUserVotesDAO(userID, resolvedRecords.get(resolvedRecords.size() - 1),
                totalVoteSalary, voteWithdrawalPenaltyPercentage);
    }

    private long getTotalUserVoteSalary(UserID userID) {
        // TODO: read all vote salary records, sum and return total
        return 100L;
    }

    private List<VoteRecord> resolveCollisions(UserID userID, List<VoteRecord> voteRecords) {
        Map<VoteRecordID, VoteRecord> recordsByID = new HashMap<VoteRecordID, VoteRecord>(voteRecords.size());
        for (VoteRecord voteRecord : voteRecords) {
            recordsByID.put(voteRecord.getVoteID(), voteRecord);
        }

        // Note that collisions will be rare: this looping will usually only go through once.
        boolean anythingRemoved;
        do {
            anythingRemoved = false;
            Iterator<VoteRecord> i = voteRecords.iterator();
            while (i.hasNext()) {
                VoteRecord voteRecord = i.next();

                VoteRecordID parentVoteID = voteRecord.getParentVoteID();
                // If it has the magic ID it's the first vote record, otherwise its parent must exist
                if (!ZERO_VOTE_RECORD_ID.equals(parentVoteID)) {
                    if (!recordsByID.containsKey(parentVoteID)) {
                        // The parent record got overwritten by a duplicate vote submit, so this one loses too
                        deleteCollidedVoteRecord(userID, voteRecord);
                        recordsByID.remove(voteRecord.getVoteID());
                        i.remove();
                        anythingRemoved = true;
                    }
                }
            }
        } while (anythingRemoved);

        // TODO: arrange the remaining records into chains by child -> parent,
        //       find the chain whose child has the newest timestamp, delete all the others,
        //       then return in order with oldest record first

        Collections.sort(voteRecords, VOTE_RECORD_COMPARATOR);

        return voteRecords;
    }

    private CurrentUserVotesDAO toCurrentUserVotesDAO(UserID userID, VoteRecord mostRecentVoteRecord,
            long totalVoteSalary, byte voteWithdrawalPenaltyPercentage) {
        // Votes remaining to spend is historic salary total minus everything spent so far
        long unallocatedVotes = totalVoteSalary;

        Map<PolicyID, PolicyVoteRecord> policyVotes = mostRecentVoteRecord.getPolicyVotes();
        for (Map.Entry<PolicyID, PolicyVoteRecord> entry : policyVotes.entrySet()) {
            if (!isActivePolicy(entry.getKey())) {
                continue;
            }
            PolicyVoteRecord record = entry.getValue();
            unallocatedVotes -= record.getVoteTotal();
            unallocatedVotes -= record.getPenaltyTotal();
        }

        return new CurrentUserVotesImpl(userID, mostRecentVoteRecord.getVoteID(), policyVotes, unallocatedVotes,
                voteWithdrawalPenaltyPercentage);
    }

    private boolean isActivePolicy(PolicyID policyID) {
        // TODO: check active-policies lookup row
        return true;
    }

    private void deleteCollidedVoteRecord(UserID userID, VoteRecord voteRecord) {
        // TODO: write zero-vote records to ALL active policies for POLICY_VOTES

        if (voteRecord.getCreatedPolicyID() != null) {
            // TODO: policy creation has to be rolled back! Obliterate this policy.
        }

        deleteUserVotesRecord(userID, voteRecord);
    }

    private CurrentUserVotesDAO createInitialEmptyVoteAllocation(UserID userID, long totalVoteSalary,
            byte voteWithdrawalPenaltyPercentage) {
        VoteRecordID initialVoteID = new VoteRecordIDImpl();

        // Initial record has zero parent
        VoteRecord voteRecord = new VoteRecord(initialVoteID, ZERO_VOTE_RECORD_ID,
                Schema.USER_VOTES.createCurrentTimestamp(), (PolicyID) null);
        writeUserVotesRecord(userID, voteRecord);

        return toCurrentUserVotesDAO(userID, voteRecord, totalVoteSalary, voteWithdrawalPenaltyPercentage);
    }

    private void writeUserVotesRecord(UserID userID, VoteRecord record) {
        // Column name is parent vote ID so duplicate submits collide
        UserVotesCF cf = Schema.USER_VOTES;
        Mutator<UserID, UniqueTimestamp> m = cf.createMutator(keyspaceManager);
        cf.addColumnInsertion(m, userID, record.getParentVoteID(),
                cf.createValue(record.toParentVersionedJSON(), record.getTimestamp()));
        m.execute();
    }

    private void writeUserVotesPendingRecord(UserID userID, VoteRecord record) {
        // Column name is vote ID so records never collide
        UserVotesCF cf = Schema.USER_VOTES_PENDING;
        Mutator<UserID, UniqueTimestamp> m = cf.createMutator(keyspaceManager);
        cf.addColumnInsertion(m, userID, record.getVoteID(),
                cf.createValue(record.toSelfVersionedJSON(), record.getTimestamp()));
        m.execute();
    }

    @Override
    public byte getVoteWithdrawalPenaltyPercentage() {
        // TODO: maybe cache in memory for a few minutes
        return readVoteWithdrawalPenaltyPercentage();
    }

    private byte readVoteWithdrawalPenaltyPercentage() {
        return Schema.VOTING_CONFIG.VOTE_WITHDRAWAL_PENALTY_PERCENTAGE.getColumnValueOrSetDefault(keyspaceManager);
    }

    @Override
    public void setVoteWithdrawalPenaltyPercentage(byte percentage) {
        AssertArgument.isTrue(0 <= percentage && percentage <= 100, "percentage must be between 0 and 100 (inclusive)",
                percentage);
        Schema.VOTING_CONFIG.VOTE_WITHDRAWAL_PENALTY_PERCENTAGE.setColumnValue(keyspaceManager, percentage);
    }

    @Override
    public void save(CurrentUserVotesDAO currentUserVotes) {
        if (currentUserVotes == null) {
            throw new IllegalArgumentException("currentUserVotes must not be null");
        }
        CurrentUserVotesImpl internal = (CurrentUserVotesImpl) currentUserVotes;

        // TODO: handle created policy IDs
        VoteRecord record = new VoteRecord(new VoteRecordIDImpl(), internal.getPreviousVoteID(),
                Schema.USER_VOTES.createCurrentTimestamp(), null, internal.getPolicyVotes());

        UserID userID = internal.getUserID();
        writeUserVotesPendingRecord(userID, record);

        propagateUserVotesPending(userID, record);
    }

    private void propagateUserVotesPending(UserID userID, VoteRecord record) {
        // TODO: propagate to policies_current_votes record first

        // Propagate to where it participates in conflict resolution and current count for this user
        writeUserVotesRecord(userID, record);

        // Delete from pending now we've successfully committed to policies, triggered recalc etc.
        // Must be last action.
        deleteUserVotesPendingRecord(userID, record);
    }

    private void deleteUserVotesPendingRecord(UserID userID, VoteRecord record) {
        // Column name is vote ID so records never collide
        UserVotesCF cf = Schema.USER_VOTES_PENDING;
        Mutator<UserID, UniqueTimestamp> m = cf.createMutator(keyspaceManager);
        cf.addColumnDeletion(m, userID, record.getVoteID());
        m.execute();
    }

    private void deleteUserVotesRecord(UserID userID, VoteRecord record) {
        // Column name is parent vote ID so duplicate submits collide
        UserVotesCF cf = Schema.USER_VOTES;
        Mutator<UserID, UniqueTimestamp> m = cf.createMutator(keyspaceManager);
        cf.addColumnDeletion(m, userID, record.getParentVoteID());
        m.execute();
    }
}
