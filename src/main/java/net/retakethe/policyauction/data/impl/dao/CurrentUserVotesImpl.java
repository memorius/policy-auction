package net.retakethe.policyauction.data.impl.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.retakethe.policyauction.data.api.dao.CurrentUserVotesDAO;
import net.retakethe.policyauction.data.api.exceptions.InsufficientVotesException;
import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.impl.types.internal.VoteRecordID;
import net.retakethe.policyauction.util.AssertArgument;

/**
 * @author Nick Clarke
 */
public class CurrentUserVotesImpl implements CurrentUserVotesDAO {

    private static final long serialVersionUID = 0L;

    private final UserID userID;
    private final VoteRecordID previousVoteID;
    private final Map<PolicyID, PolicyVoteRecord> policyVotes;
    private long unallocatedVotes;
    private PolicyID createdPolicyID;
    private final byte voteWithdrawalPenaltyPercentage;
    private final long voteCostToCreatePolicy;
    private boolean isDirty;

    /**
     * @param policyVotes will be stored unmodified, must be a unique instance for this object
     */
    public CurrentUserVotesImpl(
            UserID userID,
            VoteRecordID previousVoteID,
            Map<PolicyID, PolicyVoteRecord> policyVotes,
            long unallocatedVotes,
            byte voteWithdrawalPenaltyPercentage,
            long voteCostToCreatePolicy) {
        AssertArgument.notNull(userID, "userID");
        AssertArgument.notNull(previousVoteID, "previousVoteID");
        AssertArgument.notNull(policyVotes, "policyVotes");
        AssertArgument.isTrue(unallocatedVotes >= 0, "Unallocated votes must be >= 0", unallocatedVotes);
        AssertArgument.isTrue(voteWithdrawalPenaltyPercentage >= 0 && voteWithdrawalPenaltyPercentage <= 100,
                    "voteWithdrawalPenaltyPercentage out of range", voteWithdrawalPenaltyPercentage);
        AssertArgument.isTrue(voteCostToCreatePolicy >= 0, "voteCostToCreatePolicy must be positive",
                voteCostToCreatePolicy);

        this.userID = userID;
        this.previousVoteID = previousVoteID;
        this.unallocatedVotes = unallocatedVotes;
        this.createdPolicyID = null;
        this.policyVotes = policyVotes;
        this.voteWithdrawalPenaltyPercentage = voteWithdrawalPenaltyPercentage;
        this.voteCostToCreatePolicy = voteCostToCreatePolicy;
        this.isDirty = false;
    }

    public UserID getUserID() {
        return userID;
    }

    public VoteRecordID getPreviousVoteID() {
        return previousVoteID;
    }

    /**
     * Internal use only
     */
    public Map<PolicyID, PolicyVoteRecord> getPolicyVotes() {
        return policyVotes;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public PolicyID getCreatedPolicyID() {
        return createdPolicyID;
    }

    @Override
    public long getUnallocatedVotes() {
        return unallocatedVotes;
    }

    @Override
    public long getVotesAllocated(PolicyID policyID) {
        if (policyID == null) {
            throw new IllegalArgumentException("policyID must not be null");
        }
        PolicyVoteRecord votes = policyVotes.get(policyID);
        if (votes == null) {
            return 0;
        }
        return votes.getVoteTotal();
    }

    @Override
    public void setVotesAllocated(PolicyID policyID, long newVotes) {
        if (policyID == null) {
            throw new IllegalArgumentException("policyID must not be null");
        }
        if (newVotes < 0) {
            throw new IllegalArgumentException("newVotes must be >= 0, got " + newVotes);
        }
        PolicyVoteRecord votes = policyVotes.get(policyID);
        boolean isNew = false;
        if (votes == null) {
            isNew = true;
            votes = new PolicyVoteRecord();
        }
        long currentVotes = votes.getVoteTotal();
        long policyVoteIncrement = newVotes - currentVotes;
        if (policyVoteIncrement == 0) {
            return;
        }
        if (policyVoteIncrement > unallocatedVotes) {
            throw new InsufficientVotesException("User doesn't have enough unallocated votes to allocate " + newVotes
                    + " to policy. Need " + policyVoteIncrement + " votes but only have " + unallocatedVotes);
        }
        isDirty = true;
        long unallocatedVotesChange = -policyVoteIncrement;

        if (unallocatedVotesChange > 0) {
            long voteWithdrawalPenalty = calculatePenaltyForVoteWithdrawal(unallocatedVotesChange);
            votes.setVotePenalty(voteWithdrawalPenalty);
            unallocatedVotesChange -= voteWithdrawalPenalty;
        }

        unallocatedVotes += unallocatedVotesChange;
        votes.setVoteIncrement(policyVoteIncrement);
        if (isNew) {
            policyVotes.put(policyID, votes);
        }
    }
    
    @Override
    public long getUnallocatedVoteBalanceChangeIfWeAllocatedThis(PolicyID policyID, long newVotes) {
        if (policyID == null) {
            throw new IllegalArgumentException("policyID must not be null");
        }
        if (newVotes < 0) {
            throw new IllegalArgumentException("newVotes must be >= 0, got " + newVotes);
        }
        PolicyVoteRecord votes = policyVotes.get(policyID);
        if (votes == null) {
            votes = new PolicyVoteRecord();
        }
        long currentVotes = votes.getVoteTotal();
        long policyVoteIncrement = newVotes - currentVotes;
        if (policyVoteIncrement == 0) {
            return 0;
        }
        if (policyVoteIncrement > unallocatedVotes) {
            throw new InsufficientVotesException("User doesn't have enough unallocated votes to allocate " + newVotes
                    + " to policy. Need " + policyVoteIncrement + " votes but only have " + unallocatedVotes);
        }
        long unallocatedVotesChange = -policyVoteIncrement;

        if (unallocatedVotesChange > 0) {
            long voteWithdrawalPenalty = calculatePenaltyForVoteWithdrawal(unallocatedVotesChange);
            unallocatedVotesChange -= voteWithdrawalPenalty;
        }

        return unallocatedVotesChange;
    }
    
    @Override
    public void recordPolicyCreation(PolicyID policyID) {
        if (policyID == null) {
            throw new IllegalArgumentException("policyID must not be null");
        }
        if (createdPolicyID != null) {
            throw new IllegalStateException("createdPolicyID already set. Save record and re-retrieve first.");
        }
        if (unallocatedVotes < voteCostToCreatePolicy) {
            throw new InsufficientVotesException("User doesn't have enough unallocated votes to create a policy. "
                    + "Mandatory vote allocation on creation is " + voteCostToCreatePolicy + ", only have "
                    + unallocatedVotes);
        }

        setVotesAllocated(policyID, voteCostToCreatePolicy);
        createdPolicyID = policyID;
        isDirty = true;
    }

    private long calculatePenaltyForVoteWithdrawal(long unallocatedVotesChange) {
        double exactPenalty = (unallocatedVotesChange * voteWithdrawalPenaltyPercentage) / 100.0;
        // We round up to ensure that if you withdraw (say) one vote at a time, you still get penalized,
        // otherwise people can trivially bypass the penalty.
        // This means that the average penalty rate will be rather greater than the specified penalty rate
        // for small vote numbers. We could do it by rounding down and applying a minimum of 1, but then the
        // average rate will be rather smaller than the specified rate for small numbers - we have to pick one or other.
        return (long) Math.ceil(exactPenalty);
    }

    @Override
    public Collection<PolicyID> getPolicyIDsVotedOn() {
        return Collections.unmodifiableCollection(policyVotes.keySet());
    }
}
