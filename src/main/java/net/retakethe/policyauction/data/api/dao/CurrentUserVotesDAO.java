package net.retakethe.policyauction.data.api.dao;

import java.io.Serializable;
import java.util.Collection;

import net.retakethe.policyauction.data.api.exceptions.InsufficientVotesException;
import net.retakethe.policyauction.data.api.types.PolicyID;

/**
 * Allows viewing and modifying the allocation of votes to policies for a specific user.
 * 
 * @author Nick Clarke
 */
public interface CurrentUserVotesDAO extends Serializable {

    /**
     * Get the number of votes this user currently has available to allocate to policies.
     * This will increase or decrease when {@link #setVotesAllocated(PolicyID, long)} is called.
     *
     * @return number votes remaining, may be zero, never negative
     */
    long getUnallocatedVotes();

    /**
     * Get the vote allocation assigned to this policy.
     * If there is currently no entry for this policy, zero is returned.
     *
     * @param policyID must not be null
     * @return the number of votes allocated to this policy, may be zero, never negative
     */
    long getVotesAllocated(PolicyID policyID);

    /**
     * Change the number of votes assigned to this policy.
     * The number of {@link CurrentUserVotesDAO#getUnallocatedVotes() unallocated votes} will be updated accordingly.
     * <p>
     * Note that decreasing the number of votes allocated to a policy may result in "penalty votes":
     * you may only get back a percentage of them.
     *
     * @param policyID must not be null
     * @param newVotes the new number of votes to allocate to this policy, must be >0
     * @throws InsufficientVotesException if there aren't enough unallocated votes to make this change
     * @throws IllegalArgumentException if newVotes is negative
     * @see CurrentUserVotesDAO#getUnallocatedVotes()
     */
    void setVotesAllocated(PolicyID policyID, long newVotes);

    /**
     * Record creation of a policy.
     * This will also set the mandatory initial vote allocation; the user must have sufficient unallocated votes
     * to do this. 
     *
     * @param policyID must not be null
     * @throws InsufficientVotesException if there aren't enough unallocated votes to create a policy
     * @see CurrentUserVotesDAO#getUnallocatedVotes()
     */
    void recordPolicyCreation(PolicyID policyID);

    /**
     * Calculate how much it would cost or how many votes would be refunded if we actually called
     * {@link #setVotesAllocated(PolicyID, long)} with these values, without actually making the change.
     *
     * @throws InsufficientVotesException if there aren't enough unallocated votes to make this change
     * @throws IllegalArgumentException if newVotes is negative
     * @see CurrentUserVotesDAO#getUnallocatedVotes()
     */
    long getUnallocatedVoteBalanceChangeIfWeAllocatedThis(PolicyID policyID, long newVotes);

    /**
     * Get all PolicyIDs for which this user has allocated votes.
     * Note in some situations the collection may contain IDs for which the vote allocation is zero.
     *
     * @return unmodifiable collection, possibly empty
     */
    Collection<PolicyID> getPolicyIDsVotedOn();
}
