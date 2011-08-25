package net.retakethe.policyauction.data.api;

import net.retakethe.policyauction.data.api.types.DayOfWeek;

/**
 * Retrieval and modification of parameters affecting the voting system.
 *
 * @author Nick Clarke
 */
public interface VotingConfigManager {

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
