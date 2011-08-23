package net.retakethe.policyauction.data.api;

import java.util.List;

import net.retakethe.policyauction.data.api.dao.CurrentUserVotesDAO;
import net.retakethe.policyauction.data.api.dao.VoteSalaryPayment;
import net.retakethe.policyauction.data.api.exceptions.NoSuchUserException;
import net.retakethe.policyauction.data.api.types.DayOfWeek;
import net.retakethe.policyauction.data.api.types.UserID;

/**
 * Viewing and updating of each user's current vote allocation across all policies.
 *
 * @author Nick Clarke
 */
public interface UserVoteManager {

    /**
     * Get the user's current vote allocation (or an empty allocation if none is found),
     * and unallocated vote balance info.
     *
     * @return non-null object
     */
    CurrentUserVotesDAO getCurrentUserVoteAllocation(UserID userID) throws NoSuchUserException;

    void save(CurrentUserVotesDAO currentUserVotes);

    /**
     * Get history of all vote salary payments received by a particular user.
     * <p>
     * Note that this is only needed if we want to actually display all the history,
     * not to retrieve current available votes;
     * {@link #getCurrentUserVoteAllocation(UserID)} already includes the unallocated vote count balance.
     *
     * @param userID
     * @return non-null list in date order
     */
    List<VoteSalaryPayment> getUserVoteSalaryHistory(UserID userID) throws NoSuchUserException;

    /**
     * Get history of all vote salary payments for the entire system.
     * <p>
     * (Vote salary payments are the same for all users - users' balance depends only on their registration date.)
     *
     * @return non-null list in date order
     */
    List<VoteSalaryPayment> getSystemWideVoteSalaryHistory();

    byte getVoteWithdrawalPenaltyPercentage();

    void setVoteWithdrawalPenaltyPercentage(byte newValue);

    long getVoteCostToCreatePolicy();

    void setVoteCostToCreatePolicy(long voteCost);

    long getUserVoteSalaryIncrement();

    void setUserVoteSalaryIncrement(long voteSalaryIncrement);

    short getUserVoteSalaryFrequencyDays();

    void setUserVoteSalaryFrequencyDays(short voteSalaryFrequencyDays);

    /**
     * Get the day of week on which vote salary should be paid IF frequency is set to 7 days
     *
     * @see #getUserVoteSalaryFrequencyDays()
     */
    DayOfWeek getUserVoteSalaryWeeklyDayOfWeek();

    /**
     * Set the day of week on which vote salary should be paid IF frequency is set to 7 days
     *
     * @see #getUserVoteSalaryFrequencyDays()
     */
    void setUserVoteSalaryWeeklyDayOfWeek(DayOfWeek voteSalaryDayOfWeek);

}
