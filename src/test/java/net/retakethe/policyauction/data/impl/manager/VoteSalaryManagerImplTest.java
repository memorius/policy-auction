package net.retakethe.policyauction.data.impl.manager;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.List;

import net.retakethe.policyauction.data.api.dao.VoteSalaryPaymentDAO;
import net.retakethe.policyauction.data.api.exceptions.NoSuchUserException;
import net.retakethe.policyauction.data.api.types.DayOfWeek;
import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.data.impl.types.UserIDImpl;

import org.joda.time.LocalDate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import _fixtures.CleanDbEveryMethodDAOManagerTestBase;

/**
 * @author Nick Clarke
 */
public class VoteSalaryManagerImplTest extends CleanDbEveryMethodDAOManagerTestBase {

    private VoteSalaryManagerImpl manager;
    private VotingConfigManagerImpl votingConfigManager;

    @BeforeMethod(groups = {"dao"})
    public void setupManager() {
        manager = getDAOManager().getVoteSalaryManager();
        votingConfigManager = getDAOManager().getVotingConfigManager();
    }

    @Test(groups = {"dao"})
    public void testVoteSalaryOnFirstStartupDay() throws NoSuchUserException {
        LocalDate today = LocalDate.now();

        // Initially there are no records
        assertNull(manager.getVoteSalaryLastPaid());

        // First retrieval assigns records
        List<VoteSalaryPaymentDAO> systemSalary = manager.getSystemWideVoteSalaryHistory();
        LocalDate lastPaid = manager.getVoteSalaryLastPaid();
        assertEquals(lastPaid, today);

        assertEquals(systemSalary.size(), 1);
        VoteSalaryPaymentDAO payment = systemSalary.get(0);
        assertEquals(payment.getVotes(), VotingConfigManagerImplTest.DEFAULT_USER_VOTE_SALARY_INCREMENT);
        assertEquals(payment.getDate(), today);

        // TODO: test with a real user record once manager is actually using the user registration records
        UserID userID1 = new UserIDImpl();
        List<VoteSalaryPaymentDAO> userSalary = manager.getUserVoteSalaryHistory(userID1);
        assertEquals(userSalary.size(), 1);
        payment = systemSalary.get(0);
        assertEquals(payment.getVotes(), VotingConfigManagerImplTest.DEFAULT_USER_VOTE_SALARY_INCREMENT);
        assertEquals(payment.getDate(), today);

        // TODO: test NoSuchUserException once manager is actually using the user registration records
    }

    @Test(groups = {"dao"})
    public void testVoteSalaryAfterFirstStartup() throws NoSuchUserException {
        LocalDate today = LocalDate.now();

        // Set the config we want to test, so it puts today part-way through the cycle
        final short frequencyDays = 7;
        final long votesPerInterval = 10;
        final LocalDate mostRecentCycleStart = today.minusDays(2);
        final DayOfWeek salaryDayOfWeek = DayOfWeek.fromLocalDate(mostRecentCycleStart);
        votingConfigManager.setUserVoteSalaryFrequencyDays(frequencyDays);
        votingConfigManager.setUserVoteSalaryIncrement(votesPerInterval);
        votingConfigManager.setUserVoteSalaryWeeklyDayOfWeek(salaryDayOfWeek);

        // Frig the first startup date so it looks as though it's in the past
        final int expectedPayments = 10;
        final int numberOfCompleteCycles = expectedPayments - 1;
        // Offset the first startup date forwards so it's not on salaryDayOfWeek - should get a short first cycle
        final int offsetDays = 3;
        final LocalDate firstStartupDate = mostRecentCycleStart.minusDays(
                (frequencyDays * numberOfCompleteCycles) - offsetDays);
        overrideFirstStartupDate(firstStartupDate);

        // Initially there are no records
        assertNull(manager.getVoteSalaryLastPaid());

        // First retrieval assigns records
        List<VoteSalaryPaymentDAO> systemWide = manager.getSystemWideVoteSalaryHistory();
        LocalDate lastPaid = manager.getVoteSalaryLastPaid();
        assertEquals(lastPaid, mostRecentCycleStart);

        assertEquals(systemWide.size(), expectedPayments);

        LocalDate expectedNextDate = firstStartupDate;
        boolean first = true;
        for (VoteSalaryPaymentDAO payment : systemWide) {
            assertEquals(payment.getVotes(), votesPerInterval);
            assertEquals(payment.getDate(), expectedNextDate);
            if (first) {
                first = false;
                expectedNextDate = firstStartupDate.plusDays(frequencyDays - offsetDays);
            } else {
                expectedNextDate = expectedNextDate.plusDays(frequencyDays);
            }
        }

        // TODO: test properly once we have user registration date in user record:
        //       set system startup date in the past, set registration date in the user record, then retrieve votes
        UserID userID1 = new UserIDImpl();
        List<VoteSalaryPaymentDAO> userSalary = manager.getUserVoteSalaryHistory(userID1);

        assertEquals(userSalary.size(), expectedPayments);

        expectedNextDate = firstStartupDate;
        first = true;
        for (VoteSalaryPaymentDAO payment : userSalary) {
            assertEquals(payment.getVotes(), votesPerInterval);
            assertEquals(payment.getDate(), expectedNextDate);
            if (first) {
                first = false;
                expectedNextDate = firstStartupDate.plusDays(frequencyDays - offsetDays);
            } else {
                expectedNextDate = expectedNextDate.plusDays(frequencyDays);
            }
        }

        // TODO: test NoSuchUserException once manager is actually using the user registration records
    }

    private void overrideFirstStartupDate(LocalDate firstStartupDate) {
        Schema.SYSTEM_INFO.FIRST_STARTUP.setColumnValue(manager.getKeyspaceManager(), firstStartupDate.toDate());
    }
}
