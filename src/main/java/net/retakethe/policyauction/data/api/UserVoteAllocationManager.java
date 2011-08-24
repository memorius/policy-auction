package net.retakethe.policyauction.data.api;

import net.retakethe.policyauction.data.api.dao.CurrentUserVotesDAO;
import net.retakethe.policyauction.data.api.exceptions.NoSuchUserException;
import net.retakethe.policyauction.data.api.types.UserID;

/**
 * Viewing and updating of each user's current vote allocation across all policies.
 *
 * @author Nick Clarke
 */
public interface UserVoteAllocationManager {

    /**
     * Get the user's current vote allocation (or an empty allocation if none is found),
     * and unallocated vote balance info.
     *
     * @return non-null object
     */
    CurrentUserVotesDAO getCurrentUserVoteAllocation(UserID userID) throws NoSuchUserException;

    void save(CurrentUserVotesDAO currentUserVotes);
}
