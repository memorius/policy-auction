package net.retakethe.policyauction.data.api;

import net.retakethe.policyauction.data.api.dao.CurrentUserVotesDAO;
import net.retakethe.policyauction.data.api.types.UserID;

/**
 * Viewing and updating of each user's current vote allocation across all policies.
 *
 * @author Nick Clarke
 */
public interface UserVoteManager {

    /**
     * Get the user's current vote allocation, or an empty allocation if none is found.
     *
     * @return non-null object
     */
    CurrentUserVotesDAO getCurrentUserVoteAllocation(UserID userID);

    byte getVoteWithdrawalPenaltyPercentage();

    void setVoteWithdrawalPenaltyPercentage(byte newValue);

    long getVoteCostToCreatePolicy();

    void setVoteCostToCreatePolicy(long voteCost);

    void save(CurrentUserVotesDAO currentUserVotes);

}
