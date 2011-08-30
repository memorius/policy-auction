package net.retakethe.policyauction.data.impl.manager;

import net.retakethe.policyauction.data.api.VotingConfigManager;
import net.retakethe.policyauction.data.api.types.DayOfWeek;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.util.AssertArgument;

/**
 * @author Nick Clarke
 */
public class VotingConfigManagerImpl extends AbstractDAOManagerImpl implements VotingConfigManager {

    public VotingConfigManagerImpl(KeyspaceManager keyspaceManager) {
        super(keyspaceManager);
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

    @Override
    public long getVoteFinalizeDelaySeconds() {
        // TODO: maybe cache in memory for a few minutes
        return readVoteFinalizeDelaySeconds();
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
    public void setVoteFinalizeDelaySeconds(long voteFinalizeDelaySeconds) {
        AssertArgument.isTrue(voteFinalizeDelaySeconds > 0, "voteFinalizeDelaySeconds must be >0", voteFinalizeDelaySeconds);
        Schema.VOTING_CONFIG.VOTE_FINALIZE_DELAY_SECONDS.setColumnValue(getKeyspaceManager(), voteFinalizeDelaySeconds);
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

    private long readVoteFinalizeDelaySeconds() {
        return Schema.VOTING_CONFIG.VOTE_FINALIZE_DELAY_SECONDS.getColumnValueOrSetDefault(getKeyspaceManager());
    }
}
