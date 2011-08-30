package net.retakethe.policyauction.data.impl.manager;

import static org.testng.Assert.assertEquals;

import net.retakethe.policyauction.data.api.types.DayOfWeek;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import _fixtures.CleanDbEveryMethodDAOManagerTestBase;

/**
 * @author Nick Clarke
 */
public class VotingConfigManagerImplTest extends CleanDbEveryMethodDAOManagerTestBase {

    private static final long DEFAULT_VOTE_COST_TO_CREATE_POLICY = 100L;
    private static final byte DEFAULT_VOTE_WITHDRAWAL_PENALTY_PERCENTAGE = 40;
    static final long DEFAULT_USER_VOTE_SALARY_INCREMENT = 100L;
    private static final short DEFAULT_USER_VOTE_SALARY_FREQUENCY_DAYS = 7;
    private static final DayOfWeek DEFAULT_USER_VOTE_SALARY_WEEKLY_DAY_OF_WEEK = DayOfWeek.MONDAY;
    private static final long DEFAULT_VOTE_FINALIZE_DELAY_SECONDS = 10 * 24 * 60 * 60L;

    private VotingConfigManagerImpl manager;

    @BeforeMethod(groups = {"dao"})
    public void setupManager() {
        manager = getDAOManager().getVotingConfigManager();
    }

    @Test(groups = {"dao"})
    public void testVotingConfig() {
        assertEquals(DEFAULT_VOTE_COST_TO_CREATE_POLICY, manager.getVoteCostToCreatePolicy());
        assertEquals(DEFAULT_VOTE_WITHDRAWAL_PENALTY_PERCENTAGE, manager.getVoteWithdrawalPenaltyPercentage());
        assertEquals(DEFAULT_USER_VOTE_SALARY_INCREMENT, manager.getUserVoteSalaryIncrement());
        assertEquals(DEFAULT_USER_VOTE_SALARY_FREQUENCY_DAYS, manager.getUserVoteSalaryFrequencyDays());
        assertEquals(DEFAULT_USER_VOTE_SALARY_WEEKLY_DAY_OF_WEEK, manager.getUserVoteSalaryWeeklyDayOfWeek());
        assertEquals(DEFAULT_VOTE_FINALIZE_DELAY_SECONDS, manager.getVoteFinalizeDelaySeconds());

        manager.setVoteCostToCreatePolicy(DEFAULT_VOTE_COST_TO_CREATE_POLICY + 10);
        manager.setVoteWithdrawalPenaltyPercentage((byte) (DEFAULT_VOTE_WITHDRAWAL_PENALTY_PERCENTAGE - 5));
        manager.setUserVoteSalaryIncrement(DEFAULT_USER_VOTE_SALARY_INCREMENT + 20);
        manager.setUserVoteSalaryFrequencyDays((short) (DEFAULT_USER_VOTE_SALARY_FREQUENCY_DAYS + 25));
        manager.setUserVoteSalaryWeeklyDayOfWeek(DayOfWeek.THURSDAY);
        manager.setVoteFinalizeDelaySeconds(DEFAULT_VOTE_FINALIZE_DELAY_SECONDS + 753577);

        assertEquals(DEFAULT_VOTE_COST_TO_CREATE_POLICY + 10, manager.getVoteCostToCreatePolicy());
        assertEquals(DEFAULT_VOTE_WITHDRAWAL_PENALTY_PERCENTAGE - 5, manager.getVoteWithdrawalPenaltyPercentage());
        assertEquals(DEFAULT_USER_VOTE_SALARY_INCREMENT + 20 , manager.getUserVoteSalaryIncrement());
        assertEquals(DEFAULT_USER_VOTE_SALARY_FREQUENCY_DAYS + 25, manager.getUserVoteSalaryFrequencyDays());
        assertEquals(DayOfWeek.THURSDAY, manager.getUserVoteSalaryWeeklyDayOfWeek());
        assertEquals(DEFAULT_VOTE_FINALIZE_DELAY_SECONDS + 753577, manager.getVoteFinalizeDelaySeconds());
    }
}
