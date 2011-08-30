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

    /**
     * Set amount of time to wait before assuming current vote records are final and no late-write conflicts can occur.
     *
     * @return number of seconds, always >0
     */
    long getVoteFinalizeDelaySeconds();

    /**
     * Set amount of time to wait before assuming current vote records are final and no late-write conflicts can occur.
     *
     * @param voteFinalizeDelaySeconds must be >0; normally the same as cassandra GC_GRACE_SECONDS.
     */
    void setVoteFinalizeDelaySeconds(long voteFinalizeDelaySeconds);
}
