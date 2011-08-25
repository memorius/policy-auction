package net.retakethe.policyauction.data.api;

import java.util.List;

import net.retakethe.policyauction.data.api.dao.VoteSalaryPayment;
import net.retakethe.policyauction.data.api.exceptions.NoSuchUserException;
import net.retakethe.policyauction.data.api.types.UserID;

import org.joda.time.LocalDate;

/**
 * Retrieval of vote salary records - the periodic votes bundles that users receive, which they can then allocate
 * to policies.
 * 
 * @author Nick Clarke
 */
public interface VoteSalaryManager {

    /**
     * @return null if no pay run yet
     */
    LocalDate getVoteSalaryLastPaid();
    
    /**
     * Get history of all vote salary payments for the entire system.
     * <p>
     * (Vote salary payments are the same for all users - users' balance depends only on their registration date.)
     *
     * @return non-null list in date order
     */
    List<VoteSalaryPayment> getSystemWideVoteSalaryHistory();

    /**
     * Get history of all vote salary payments received by a particular user.
     * <p>
     * Note that this is only needed if we want to actually display all the history,
     * not to retrieve current available votes; {@link UserVoteAllocationManager#getCurrentUserVoteAllocation(UserID)}
     * already includes the unallocated vote count balance.
     *
     * @param userID
     * @return non-null list in date order
     * @see UserVoteAllocationManager#getCurrentUserVoteAllocation(UserID)
     * @see net.retakethe.policyauction.data.api.dao.CurrentUserVotesDAO#getUnallocatedVotes()
     */
    List<VoteSalaryPayment> getUserVoteSalaryHistory(UserID userID) throws NoSuchUserException;

}
