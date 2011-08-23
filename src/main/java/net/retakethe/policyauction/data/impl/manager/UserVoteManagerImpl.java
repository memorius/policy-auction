package net.retakethe.policyauction.data.impl.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.api.SystemInfoManager;
import net.retakethe.policyauction.data.api.UserVoteManager;
import net.retakethe.policyauction.data.api.dao.CurrentUserVotesDAO;
import net.retakethe.policyauction.data.api.dao.VoteSalaryPayment;
import net.retakethe.policyauction.data.api.exceptions.NoSuchUserException;
import net.retakethe.policyauction.data.api.types.DayOfWeek;
import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.impl.dao.CurrentUserVotesImpl;
import net.retakethe.policyauction.data.impl.dao.PolicyVoteRecord;
import net.retakethe.policyauction.data.impl.dao.VoteSalaryPaymentImpl;
import net.retakethe.policyauction.data.impl.query.api.ColumnResult;
import net.retakethe.policyauction.data.impl.query.api.ColumnSlice;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.Mutator;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.data.impl.schema.Schema.UserVotesCF;
import net.retakethe.policyauction.data.impl.schema.Schema.VoteSalaryRow;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.MillisTimestamp;
import net.retakethe.policyauction.data.impl.schema.timestamp.UniqueTimestamp;
import net.retakethe.policyauction.data.impl.schema.value.Value;
import net.retakethe.policyauction.data.impl.types.PolicyIDImpl;
import net.retakethe.policyauction.data.impl.types.internal.VoteRecordID;
import net.retakethe.policyauction.data.impl.types.internal.VoteRecordIDImpl;
import net.retakethe.policyauction.data.impl.util.UUIDUtils;
import net.retakethe.policyauction.util.AssertArgument;
import net.retakethe.policyauction.util.Functional;

import org.apache.tapestry5.json.JSONObject;
import org.joda.time.LocalDate;

/**
 * @author Nick Clarke
 */
public class UserVoteManagerImpl extends AbstractDAOManagerImpl implements UserVoteManager {

    /**
     * Internal class representing the column values in the UserVotesCF tables.
     * <p>
     * The information is stored as JSON because it must be a single atomically-updated unit in Cassandra.
     * <p>
     * To read votes, a list of these is retrieved, assembled into a tree structure, undergoes conflict resolution to
     * determine a single winning leaf node, and the winning leaf is returned in the CurrentUserVotesDAO.
     * <p>
     * To write votes, a new instance of this class is created, with parent pointing to the previous record,
     * and stored as a new JSON column value.
     *
     * <p>
     * JSON format:
     * <pre>
     * {
     *     parent: &lt;voteRecordID>
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
        
        public VoteRecord(VoteRecordID voteID, UniqueTimestamp timestamp, JSONObject json) {
            this(voteID, new VoteRecordIDImpl(json.getString(PARENT)), timestamp,
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

        public JSONObject toJSON() {
            JSONObject o = new JSONObject();
            o.put(PARENT, parentVoteID.asString());
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

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("VoteRecord [voteID=").append(this.voteID).append(", parentVoteID=")
                    .append(this.parentVoteID).append(", createdPolicyID=").append(this.createdPolicyID)
                    .append(",\n            policyVotes=").append(this.policyVotes).append("]");
            return builder.toString();
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
    }

    private static final VoteRecordID ZERO_VOTE_RECORD_ID = new VoteRecordIDImpl(UUIDUtils.getZeroTimeUUID());

    private static final Comparator<VoteRecordID> VOTE_RECORD_ID_COMPARATOR = new Comparator<VoteRecordID>() {
            @Override
            public int compare(VoteRecordID o1, VoteRecordID o2) {
                return ((VoteRecordIDImpl) o1).compareTo((VoteRecordIDImpl) o2);
            }
        };

    private static final Comparator<VoteRecord> VOTE_RECORD_COMPARATOR = new Comparator<VoteRecord>() {
        @Override
        public int compare(VoteRecord o1, VoteRecord o2) {
            return VOTE_RECORD_ID_COMPARATOR.compare(o1.getVoteID(), o2.getVoteID());
        }
    };

    private final SystemInfoManager systemInfoManager;

    public UserVoteManagerImpl(KeyspaceManager keyspaceManager, SystemInfoManager systemInfoManager) {
        super(keyspaceManager);
        this.systemInfoManager = systemInfoManager;
    }

    @Override
    public CurrentUserVotesDAO getCurrentUserVoteAllocation(UserID userID) throws NoSuchUserException {
        if (userID == null) {
            throw new IllegalArgumentException("userID must not be null");
        }

        // TODO: Propagate pending votes first.

        final long totalVoteSalary = getTotalUserVoteSalary(userID);

        final byte voteWithdrawalPenaltyPercentage = getVoteWithdrawalPenaltyPercentage();
        final long voteCostToCreatePolicy = getVoteCostToCreatePolicy();

        List<VoteRecord> voteRecords = readUserVoteRecords(userID);

        if (voteRecords.isEmpty()) {
            // First time this user has done anything. Write an empty record and return it.
            return createInitialEmptyVoteAllocation(userID, totalVoteSalary, voteWithdrawalPenaltyPercentage,
                    voteCostToCreatePolicy);
        }

        List<VoteRecord> resolvedRecords = resolveCollisions(userID, voteRecords);

        // Get the most recent record, convert to CurrentUserVotesDAO and return.
        return toCurrentUserVotesDAO(userID, resolvedRecords.get(resolvedRecords.size() - 1),
                totalVoteSalary, voteWithdrawalPenaltyPercentage, voteCostToCreatePolicy);
    }

    @Override
    public List<VoteSalaryPayment> getUserVoteSalaryHistory(UserID userID) throws NoSuchUserException {
        return getSalaryRecordsSince(getUserRegistrationDate(userID));
    }

    @Override
    public List<VoteSalaryPayment> getSystemWideVoteSalaryHistory() {
        return getSalaryRecordsSince(new LocalDate(systemInfoManager.getFirstStartupTime()));
    }

    private List<VoteRecord> readUserVoteRecords(UserID userID) {
        // TODO: keep "conflicts resolved from <version> onwards" and use this to set column range

        UserVotesCF cf = Schema.USER_VOTES;
        QueryResult<ColumnSlice<UniqueTimestamp, VoteRecordID>> qr =
                cf.createSliceQuery(getKeyspaceManager(), userID, null, null, false, Integer.MAX_VALUE).execute();

        return Functional.map(qr.get().getColumns(cf.getColumnRange()),
                new Functional.Converter<ColumnResult<UniqueTimestamp, VoteRecordID, JSONObject>, VoteRecord>() {
                    @Override
                    public VoteRecord convert(ColumnResult<UniqueTimestamp, VoteRecordID, JSONObject> column) {
                        VoteRecordID recordID = column.getName();
                        Value<UniqueTimestamp, JSONObject> v = column.getValue();
                        UniqueTimestamp ts = v.getTimestamp();
                        JSONObject json = v.getValue();

                        return new VoteRecord(recordID, ts, json);
                    }
                });
    }

    /**
     * Read all vote salary records, sum and return total
     *
     * @param userID
     * @return
     */
    private long getTotalUserVoteSalary(UserID userID) throws NoSuchUserException {
        long total = 0L;
        for (VoteSalaryPayment salaryInfo : getUserVoteSalaryHistory(userID)) {
            total += salaryInfo.getVotes();
        }

        return total;
    }

    private LocalDate getUserRegistrationDate(UserID userID) throws NoSuchUserException {
        // FIXME: temporary hack until I have Matt's user reg manager: all users get all votes regardless of reg date.
        //        Need to read real reg date from user CF, throw NoSuchUserException if not found. 
        return new LocalDate(systemInfoManager.getFirstStartupTime());
    }

    private List<VoteSalaryPayment> getSalaryRecordsSince(LocalDate startDate) {
        LocalDate today = LocalDate.now();

        createSalaryRecordsIfNeeded(today);

        return readVoteSalary(startDate, today);
    }

    private void createSalaryRecordsIfNeeded(LocalDate today) {
        LocalDate lastPay = systemInfoManager.getVoteSalaryLastPaid();
        if (lastPay != null && !lastPay.isBefore(today)) {
            // Already updated today (or, well, in the future, like)
            return;
        }

        LocalDate nextPay = getNextPay(lastPay, today);
        if (nextPay == null) {
            // Not due yet
            return;
        }

        // Write any missing records
        do {
            writeVoteSalaryColumn(nextPay, getUserVoteSalaryIncrement());
            systemInfoManager.setVoteSalaryLastPaid(nextPay);
            lastPay = nextPay;
            nextPay = getNextPay(lastPay, today);
        } while (nextPay != null);
    }

    private LocalDate getNextPay(LocalDate lastPay, LocalDate today) {
        if (lastPay == null) {
            // First payment is always on first startup day, even if weekly/specific-day frequency is set and it's
            // out of phase, because it simplifies range retrievals. The next payment will be on the specified day.
            return new LocalDate(systemInfoManager.getFirstStartupTime());
        }

        LocalDate nextPay;
        final short frequencyDays = getUserVoteSalaryFrequencyDays();
        if (7 == frequencyDays) {
            // Weekly frequency is handled differently so that all payments except the first-startup one occur on
            // the specified day of the week, regardless of first-startup day-of-week or day of frequency change.
            // If we're out of phase (e.g. changed the pay-day-of-week since last pay, or changed to weekly from 
            // some other frequency), step back so we have a short week this week, rather than a long week.
            DayOfWeek dayOfWeek = getUserVoteSalaryWeeklyDayOfWeek();
            while (DayOfWeek.fromLocalDate(lastPay) != dayOfWeek) {
                lastPay = lastPay.minusDays(1);
            }
            nextPay = lastPay.plusDays(frequencyDays);
        } else {
            // Non-weekly frequency
            nextPay = lastPay.plusDays(frequencyDays);
        }

        // Return only if it's actually due yet
        return nextPay.isAfter(today) ? null : nextPay;
    }

    /**
     * @param startDate inclusive
     * @param endDate inclusive
     */
    private List<VoteSalaryPayment> readVoteSalary(LocalDate startDate, LocalDate endDate) {
        VoteSalaryRow cf = Schema.VOTE_SALARY;
        ColumnRange<String, MillisTimestamp, LocalDate, Long> columnRange = cf.getColumnRange();

        QueryResult<ColumnSlice<MillisTimestamp, LocalDate>> result =
                cf.createSliceQuery(getKeyspaceManager(), startDate, endDate, false, Integer.MAX_VALUE).execute();
        List<ColumnResult<MillisTimestamp, LocalDate, Long>> columns = result.get().getColumns(columnRange);

        List<VoteSalaryPayment> salary = toSalaryRecords(columns);

        // Find one more record backwards from the start date, if there are any.
        // This is so that if you start ON a salary day, you receive that day's salary;
        // if you start between days, you receive salary from the previous cycle.
        // To avoid reading ALL records every time, we have to do a second read (or in the worst case, several)
        // since we don't know what the salary frequency or phase was at the startDate.

        endDate = startDate.minusDays(1);
        startDate = startDate.minusMonths(1);

        // Oldest possible salary payment date.
        final LocalDate earliest = new LocalDate(systemInfoManager.getFirstStartupTime());

        while (earliest.isBefore(endDate)) {
            result = cf.createSliceQuery(getKeyspaceManager(), startDate, endDate, true, Integer.MAX_VALUE).execute();
            columns = result.get().getColumns(columnRange);

            if (columns.isEmpty()) {
                // No records in this interval - fetch another chunk
                endDate = startDate.minusDays(1);
                startDate = startDate.minusMonths(1);
                continue;
            }

            // Results in reversed order - this is the most recent
            salary.add(0, makeSalaryRecord(columns.get(0)));
            break;
        }

        return salary;
    }

    private VoteSalaryPayment makeSalaryRecord(ColumnResult<MillisTimestamp, LocalDate, Long> columnResult) {
        return new VoteSalaryPaymentImpl(columnResult.getName(), columnResult.getValue().getValue());
    }

    private List<VoteSalaryPayment> toSalaryRecords(List<ColumnResult<MillisTimestamp, LocalDate, Long>> columns) {
        return Functional.map(columns,
                new Functional.Converter<ColumnResult<MillisTimestamp, LocalDate, Long>, VoteSalaryPayment>() {
                    @Override
                    public VoteSalaryPayment convert(ColumnResult<MillisTimestamp, LocalDate, Long> columnResult) {
                        return makeSalaryRecord(columnResult);
                    }
                });
    }

    private void writeVoteSalaryColumn(LocalDate date, long salaryIncrement) {
        VoteSalaryRow cf = Schema.VOTE_SALARY;
        Mutator<String, MillisTimestamp> m = cf.createMutator(getKeyspaceManager());
        cf.addColumnInsertion(m, date, cf.createValue(salaryIncrement));
        m.execute();
    }

    /**
     * Find the chain with the newest child, propagate deletions for the rest and for any with missing parents.
     * Results are returned in order from oldest to newest.
     *
     * @param voteRecords in time order
     */
    private List<VoteRecord> resolveCollisions(UserID userID, List<VoteRecord> voteRecords) {
        Map<VoteRecordID, VoteRecord> allRecords = new LinkedHashMap<VoteRecordID, VoteRecord>(voteRecords.size());
        for (VoteRecord voteRecord : voteRecords) {
            allRecords.put(voteRecord.getVoteID(), voteRecord);
        }

        // Child (leaf) nodes mapped to all their ancestors
        SortedMap<VoteRecordID, Set<VoteRecordID>> leafToAncestors =
                    new TreeMap<VoteRecordID, Set<VoteRecordID>>(VOTE_RECORD_ID_COMPARATOR);
        // Start with just the magic first record which will always be present
        leafToAncestors.put(ZERO_VOTE_RECORD_ID, new HashSet<VoteRecordID>());

        List<VoteRecord> toDelete = new LinkedList<VoteRecord>();
        Set<VoteRecordID> processed = new HashSet<VoteRecordID>(voteRecords.size());

        // Process in newest...oldest order, more efficient
        ListIterator<VoteRecord> i = voteRecords.listIterator(voteRecords.size());
        while (i.hasPrevious()) {
            VoteRecord record = i.previous();
            VoteRecordID id = record.getVoteID();

            if (processed.contains(id)) {
                // I'm in one of the existing meAndMyAncestors sets, so I can't be the leaf of the longest chain.
                continue;
            }
            if (ZERO_VOTE_RECORD_ID.equals(id)) {
                // Magic first record already handled
                continue;
            }

            Set<VoteRecordID> meAndMyAncestors = new HashSet<VoteRecordID>(allRecords.size());
            meAndMyAncestors.add(id);

            boolean foundChain = false;
            boolean missingParent = false;
            VoteRecord next = record;
            while (true) {
                VoteRecordID parentID = next.getParentVoteID();
                if (ZERO_VOTE_RECORD_ID.equals(parentID)) {
                    // End of chain
                    break;
                }
                meAndMyAncestors.add(parentID);

                Set<VoteRecordID> family = leafToAncestors.remove(parentID);
                if (family != null) {
                    // One of my ancestors is the end of one of the chains we're tracking. I extend that chain.
                    family.addAll(meAndMyAncestors);
                    leafToAncestors.put(id, family);
                    foundChain = true;
                    break; // Remaining ancestors must already be in family
                }

                next = allRecords.get(parentID);
                if (next == null) {
                    // Probably due to concurrent execution of this resolve process
                    // logger.info("Missing parent '" + parentID + "' for record '" + id + "'");
                    missingParent = true;
                    for (VoteRecordID idToDelete : meAndMyAncestors) {
                        VoteRecord recordToDelete = allRecords.remove(idToDelete);
                        if (recordToDelete != null) {
                            toDelete.add(recordToDelete);
                        }
                    }
                    break;
                }
            }
            if (!foundChain && !missingParent) {
                // New chain
                leafToAncestors.put(id, meAndMyAncestors);
            }
            processed.addAll(meAndMyAncestors);
        }

        // Keep the chain with the youngest leaf. Any other chains lose.
        if (leafToAncestors.size() > 1) {
            VoteRecordID youngestLeafID = leafToAncestors.lastKey();
            Set<VoteRecordID> winningChain = leafToAncestors.get(youngestLeafID);
            for (Map.Entry<VoteRecordID, Set<VoteRecordID>> entry : leafToAncestors.entrySet()) {
                if (!youngestLeafID.equals(entry.getKey())) {
                    // Delete all children that are not in the winning chain.
                    Set<VoteRecordID> conflictedItems = entry.getValue();
                    conflictedItems.removeAll(winningChain);
                    for (VoteRecordID idToDelete : conflictedItems) {
                        VoteRecord recordToDelete = allRecords.remove(idToDelete);
                        if (recordToDelete != null) {
                            toDelete.add(recordToDelete);
                        }
                    }
                }
            }
        }

        if (!toDelete.isEmpty()) {
            // Delete children first to minimize interaction with concurrent executions
            Collections.sort(toDelete, Collections.reverseOrder(VOTE_RECORD_COMPARATOR));
            for (VoteRecord voteRecord : toDelete) {
                deleteCollidedVoteRecord(userID, voteRecord);
            }
        }

        return new ArrayList<VoteRecord>(allRecords.values());
    }

    private CurrentUserVotesDAO toCurrentUserVotesDAO(UserID userID, VoteRecord mostRecentVoteRecord,
            long totalVoteSalary, byte voteWithdrawalPenaltyPercentage, long voteCostToCreatePolicy) {
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
                voteWithdrawalPenaltyPercentage, voteCostToCreatePolicy);
    }

    private boolean isActivePolicy(PolicyID policyID) {
        // TODO: check active-policies lookup row
        return true;
    }

    private void deleteCollidedVoteRecord(UserID userID, VoteRecord voteRecord) {
        /* TODO: write zero-vote records to ALL active policies for POLICY_VOTES
         * - Write zero-increment vote records to policy_new_votes,
         *   with timestamp and version set newer than the conflict losing record,
         *   (can we use the newest child item which WON the conflict?),
         *   for each policy in the item which LOST the conflict which has a non-zero vote record.
         *   This late write is an optimisation and is needed to make the cross-policy conflict resolution work correctly.
         */

        if (voteRecord.getCreatedPolicyID() != null) {
            // TODO: policy creation has to be rolled back! Obliterate this policy.
        }

        // The above writes have succeeded: delete the conflicted item from user_policy_votes.
        deleteUserVotesRecord(userID, voteRecord.getVoteID(), Schema.USER_VOTES);
    }

    private CurrentUserVotesDAO createInitialEmptyVoteAllocation(UserID userID, long totalVoteSalary,
            byte voteWithdrawalPenaltyPercentage, long voteCostToCreatePolicy) {
        // Initial record has zero ID and zero parent
        VoteRecord voteRecord = new VoteRecord(ZERO_VOTE_RECORD_ID, ZERO_VOTE_RECORD_ID,
                Schema.USER_VOTES.createCurrentTimestamp(), (PolicyID) null);
        writeUserVotesRecord(userID, voteRecord, Schema.USER_VOTES);

        return toCurrentUserVotesDAO(userID, voteRecord, totalVoteSalary, voteWithdrawalPenaltyPercentage,
                voteCostToCreatePolicy);
    }

    private void writeUserVotesRecord(UserID userID, VoteRecord record, UserVotesCF cf) {
        Mutator<UserID, UniqueTimestamp> m = cf.createMutator(getKeyspaceManager());
        cf.addColumnInsertion(m, userID, record.getVoteID(),
                cf.createValue(record.toJSON(), record.getTimestamp()));
        m.execute();
    }

    @Override
    public byte getVoteWithdrawalPenaltyPercentage() {
        // TODO: maybe cache in memory for a few minutes
        return readVoteWithdrawalPenaltyPercentage();
    }
    
    @Override
    public long getVoteCostToCreatePolicy() {
        // TODO: maybe cache in memory for a few minutes
        return readVoteCostToCreatePolicy();
    }

    @Override
    public long getUserVoteSalaryIncrement() {
        // TODO: maybe cache in memory for a few minutes
        return readUserVoteSalaryIncrement();
    }

    @Override
    public short getUserVoteSalaryFrequencyDays() {
        // TODO: maybe cache in memory for a few minutes
        return readUserVoteSalaryFrequencyDays();
    }
    
    @Override
    public DayOfWeek getUserVoteSalaryWeeklyDayOfWeek() {
        // TODO: maybe cache in memory for a few minutes
        return readUserVoteSalaryWeeklyDayOfWeek();
    }

    private byte readVoteWithdrawalPenaltyPercentage() {
        return Schema.VOTING_CONFIG.VOTE_WITHDRAWAL_PENALTY_PERCENTAGE.getColumnValueOrSetDefault(getKeyspaceManager());
    }
    
    private long readVoteCostToCreatePolicy() {
        return Schema.VOTING_CONFIG.VOTE_COST_TO_CREATE_POLICY.getColumnValueOrSetDefault(getKeyspaceManager());
    }

    private long readUserVoteSalaryIncrement() {
        return Schema.VOTING_CONFIG.USER_VOTE_SALARY_INCREMENT.getColumnValueOrSetDefault(getKeyspaceManager());
    }

    private short readUserVoteSalaryFrequencyDays() {
        return Schema.VOTING_CONFIG.USER_VOTE_SALARY_FREQUENCY_DAYS.getColumnValueOrSetDefault(getKeyspaceManager());
    }

    private DayOfWeek readUserVoteSalaryWeeklyDayOfWeek() {
        return Schema.VOTING_CONFIG.USER_VOTE_SALARY_WEEKLY_DAY_OF_WEEK.getColumnValueOrSetDefault(getKeyspaceManager());
    }

    @Override
    public void setVoteCostToCreatePolicy(long voteCost) {
        AssertArgument.isTrue(voteCost >= 0, "voteCost must be positive", voteCost);
        Schema.VOTING_CONFIG.VOTE_COST_TO_CREATE_POLICY.setColumnValue(getKeyspaceManager(), voteCost);
    }

    @Override
    public void setVoteWithdrawalPenaltyPercentage(byte percentage) {
        AssertArgument.isTrue(0 <= percentage && percentage <= 100, "percentage must be between 0 and 100 (inclusive)",
                percentage);
        Schema.VOTING_CONFIG.VOTE_WITHDRAWAL_PENALTY_PERCENTAGE.setColumnValue(getKeyspaceManager(), percentage);
    }

    @Override
    public void setUserVoteSalaryIncrement(long voteSalaryIncrement) {
        AssertArgument.isTrue(voteSalaryIncrement >= 0, "voteSalaryIncrement must be positive", voteSalaryIncrement);
        Schema.VOTING_CONFIG.USER_VOTE_SALARY_INCREMENT.setColumnValue(getKeyspaceManager(), voteSalaryIncrement);
    }

    @Override
    public void setUserVoteSalaryFrequencyDays(short voteSalaryFrequencyDays) {
        AssertArgument.isTrue(voteSalaryFrequencyDays > 0, "voteSalaryIncrement must be greater than zero",
                voteSalaryFrequencyDays);
        Schema.VOTING_CONFIG.USER_VOTE_SALARY_FREQUENCY_DAYS.setColumnValue(getKeyspaceManager(), voteSalaryFrequencyDays);
    }

    @Override
    public void setUserVoteSalaryWeeklyDayOfWeek(DayOfWeek voteSalaryDayOfWeek) {
        AssertArgument.notNull(voteSalaryDayOfWeek, "voteSalaryDayOfWeek");
        Schema.VOTING_CONFIG.USER_VOTE_SALARY_WEEKLY_DAY_OF_WEEK.setColumnValue(getKeyspaceManager(), voteSalaryDayOfWeek);
    }

    @Override
    public void save(CurrentUserVotesDAO currentUserVotes) {
        if (currentUserVotes == null) {
            throw new IllegalArgumentException("currentUserVotes must not be null");
        }
        CurrentUserVotesImpl internal = (CurrentUserVotesImpl) currentUserVotes;
        if (!internal.isDirty()) {
            return;
        }

        VoteRecord record = new VoteRecord(new VoteRecordIDImpl(), internal.getPreviousVoteID(),
                Schema.USER_VOTES.createCurrentTimestamp(), internal.getCreatedPolicyID(), internal.getPolicyVotes());

        UserID userID = internal.getUserID();
        writeUserVotesRecord(userID, record, Schema.USER_VOTES_PENDING);

        propagateUserVotesPending(userID, record);
    }

    private void propagateUserVotesPending(UserID userID, VoteRecord record) {
        // TODO: propagate to policies_current_votes record first

        // Propagate to where it participates in conflict resolution and current count for this user
        writeUserVotesRecord(userID, record, Schema.USER_VOTES);

        // Delete from pending now we've successfully committed to policies, triggered recalc etc.
        // Must be last action.
        deleteUserVotesRecord(userID, record.getVoteID(), Schema.USER_VOTES_PENDING);
    }

    private void deleteUserVotesRecord(UserID userID, VoteRecordID voteRecordID, UserVotesCF cf) {
        Mutator<UserID, UniqueTimestamp> m = cf.createMutator(getKeyspaceManager());
        cf.addColumnDeletion(m, userID, voteRecordID);
        m.execute();
    }
}
