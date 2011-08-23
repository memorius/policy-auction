package net.retakethe.policyauction.data.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import net.retakethe.policyauction.data.api.exceptions.InsufficientVotesException;
import net.retakethe.policyauction.data.api.exceptions.NoSuchUserException;
import net.retakethe.policyauction.data.api.types.DayOfWeek;
import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.impl.dao.CurrentUserVotesImpl;
import net.retakethe.policyauction.data.impl.manager.UserVoteManagerImpl;
import net.retakethe.policyauction.data.impl.types.PolicyIDImpl;
import net.retakethe.policyauction.data.impl.types.UserIDImpl;
import net.retakethe.policyauction.data.impl.types.internal.VoteRecordID;
import net.retakethe.policyauction.data.impl.types.internal.VoteRecordIDImpl;
import net.retakethe.policyauction.data.impl.util.UUIDUtils;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import _fixtures.CleanDbEveryMethodDAOManagerTestBase;

/**
 * @author Nick Clarke
 */
public class UserVoteManagerImplTest extends CleanDbEveryMethodDAOManagerTestBase {

    private static final long DEFAULT_VOTE_SALARY = 100L;
    private static final long DEFAULT_VOTE_COST_TO_CREATE_POLICY = 100L;
    private static final byte DEFAULT_VOTE_WITHDRAWAL_PENALTY_PERCENTAGE = 40;
    private static final long DEFAULT_USER_VOTE_SALARY_INCREMENT = 100L;
    private static final short DEFAULT_USER_VOTE_SALARY_FREQUENCY_DAYS = 7;

    private static final VoteRecordID ZERO_VOTE_RECORD_ID = new VoteRecordIDImpl(UUIDUtils.getZeroTimeUUID());
    private static final DayOfWeek DEFAULT_USER_VOTE_SALARY_WEEKLY_DAY_OF_WEEK = DayOfWeek.MONDAY;

    private UserVoteManagerImpl manager;

    @BeforeMethod(groups = {"dao"})
    public void setupManager() {
        manager = getDAOManager().getUserVoteManager();
    }

    private void assertEmptyVoteAllocation(CurrentUserVotesImpl dao, UserID userID) {
        assertNull(dao.getCreatedPolicyID());
        assertEquals(dao.getPreviousVoteID(), ZERO_VOTE_RECORD_ID);
        assertEquals(dao.getUserID(), userID);
        assertEquals(dao.getUnallocatedVotes(), DEFAULT_VOTE_SALARY);
        assertFalse(dao.isDirty());
        assertEquals(dao.getPolicyIDsVotedOn().size(), 0);
        assertEquals(dao.getPolicyVotes().size(), 0);
    }

    @Test(groups = {"dao"})
    public void testVoteAllocation() throws NoSuchUserException {
        // Set values convenient to our test
        final long voteCostToCreatePolicy = 20L;
        manager.setVoteCostToCreatePolicy(voteCostToCreatePolicy);
        final byte voteWithdrawalPenaltyPercentage = (byte) 40;
        manager.setVoteWithdrawalPenaltyPercentage(voteWithdrawalPenaltyPercentage);

        PolicyID policyID1 = new PolicyIDImpl();
        PolicyID policyID2 = new PolicyIDImpl();

        UserID userID1 = new UserIDImpl();
        CurrentUserVotesImpl dao = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID1);
        assertEmptyVoteAllocation(dao, userID1);
        assertEquals(dao.getVotesAllocated(policyID1), 0);
        assertEquals(dao.getVotesAllocated(policyID2), 0);

        // Setting to current value does nothing
        dao.setVotesAllocated(policyID1, 0);
        assertEquals(dao.getPolicyIDsVotedOn().size(), 0);
        assertEquals(dao.getPolicyVotes().size(), 0);
        assertFalse(dao.isDirty());

        // Save is a no op when not modified
        manager.save(dao);
        dao = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID1);
        assertEquals(dao.getPreviousVoteID(), ZERO_VOTE_RECORD_ID);
        long votesUnallocated = dao.getUnallocatedVotes();
        assertEquals(votesUnallocated, DEFAULT_VOTE_SALARY);
        assertEquals(dao.getPolicyVotes().size(), 0);

        // Allocate votes
        long policy1Votes = 10;
        dao.setVotesAllocated(policyID1, policy1Votes);
        votesUnallocated -= 10;

        long policy2Votes = 30;
        dao.setVotesAllocated(policyID2, policy2Votes);
        votesUnallocated -= 30;

        assertEquals(dao.getVotesAllocated(policyID1), policy1Votes);
        assertEquals(dao.getVotesAllocated(policyID2), policy2Votes);
        assertEquals(dao.getPolicyIDsVotedOn().size(), 2);
        assertEquals(dao.getUnallocatedVotes(), votesUnallocated);
        assertTrue(dao.isDirty());

        // Save changes and re-read
        manager.save(dao);
        dao = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID1);
        assertEquals(dao.getVotesAllocated(policyID1), policy1Votes);
        assertEquals(dao.getVotesAllocated(policyID2), policy2Votes);
        assertEquals(dao.getPolicyIDsVotedOn().size(), 2);
        assertEquals(dao.getUnallocatedVotes(), votesUnallocated);
        assertNull(dao.getCreatedPolicyID());
        assertEquals(dao.getUserID(), userID1);
        assertFalse(dao.isDirty());
        // New ID was written and will be the parent of this one if saved again
        assertNotEquals(dao.getPreviousVoteID(), ZERO_VOTE_RECORD_ID);

        // Other user has empty allocation
        UserID userID2 = new UserIDImpl();
        assertEmptyVoteAllocation((CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID2), userID2);

        // Policy creation
        PolicyID policyID3 = new PolicyIDImpl();
        dao.recordPolicyCreation(policyID3);
        long policy3Votes = voteCostToCreatePolicy;
        votesUnallocated -= voteCostToCreatePolicy;
        assertEquals(dao.getUnallocatedVotes(), votesUnallocated);
        assertEquals(dao.getCreatedPolicyID(), policyID3);
        assertEquals(dao.getVotesAllocated(policyID3), policy3Votes);
        assertEquals(dao.getPolicyIDsVotedOn().size(), 3);
        assertEquals(dao.getPolicyVotes().size(), 3);
        assertTrue(dao.isDirty());
        manager.save(dao);

        // Trying to allocate more votes than we have will fail
        dao = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID1);
        assertEquals(dao.getUnallocatedVotes(), votesUnallocated);
        // Allowed
        assertEquals(dao.getVotesAllocated(policyID2), policy2Votes);
        long policy2MaxVotes = policy2Votes + votesUnallocated;
        dao.setVotesAllocated(policyID2, policy2MaxVotes);
        assertEquals(dao.getVotesAllocated(policyID2), policy2MaxVotes);
        assertTrue(dao.isDirty());

        dao = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID1);
        assertEquals(dao.getUnallocatedVotes(), votesUnallocated);
        // Not allowed
        assertEquals(dao.getVotesAllocated(policyID2), policy2Votes);
        try {
            dao.setVotesAllocated(policyID2, policy2MaxVotes + 1);
            fail();
        } catch (InsufficientVotesException expected) {}
        // Shoud be unchanged
        assertEquals(dao.getVotesAllocated(policyID2), policy2Votes);
        assertFalse(dao.isDirty());

        // Vote withdrawals and associated penalty
        policy2Votes -= 10;
        dao.setVotesAllocated(policyID2, policy2Votes);
        votesUnallocated += (10 * (1.0 - (voteWithdrawalPenaltyPercentage / 100.0)));
        assertEquals(dao.getUnallocatedVotes(), votesUnallocated);
        assertEquals(dao.getVotesAllocated(policyID2), policy2Votes);
        assertTrue(dao.isDirty());

        // Withdrawal of one vote is entirely consumed by the penalty due to rounding
        assertEquals(dao.getVotesAllocated(policyID1), policy1Votes);
        policy1Votes -= 1;
        dao.setVotesAllocated(policyID1, policy1Votes);
        assertEquals(dao.getUnallocatedVotes(), votesUnallocated);
        assertEquals(dao.getVotesAllocated(policyID1), policy1Votes);
        manager.save(dao);

        dao = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID1);
        assertEquals(dao.getUnallocatedVotes(), votesUnallocated);
        assertEquals(dao.getVotesAllocated(policyID1), policy1Votes);
        assertEquals(dao.getVotesAllocated(policyID2), policy2Votes);
        assertEquals(dao.getVotesAllocated(policyID3), policy3Votes);
        assertEquals(dao.getPolicyIDsVotedOn().size(), 3);

        // Prospective allocations don't change anything
        assertFalse(dao.isDirty());
        assertEquals(dao.getUnallocatedVoteBalanceChangeIfWeAllocatedThis(policyID3, policy3Votes), 0);
        assertEquals(dao.getUnallocatedVoteBalanceChangeIfWeAllocatedThis(policyID3, policy3Votes + 10), -10);
        assertEquals(dao.getUnallocatedVoteBalanceChangeIfWeAllocatedThis(policyID3, policy3Votes - 10),
                (long) (10 * (1.0 - (voteWithdrawalPenaltyPercentage / 100.0))));
        assertEquals(dao.getUnallocatedVoteBalanceChangeIfWeAllocatedThis(policyID3, policy3Votes + votesUnallocated),
                -votesUnallocated);
        try {
            dao.getUnallocatedVoteBalanceChangeIfWeAllocatedThis(policyID3, policy3Votes + votesUnallocated + 1);
            fail();
        } catch (InsufficientVotesException expected) {}
        assertEquals(dao.getVotesAllocated(policyID3), policy3Votes);
        assertFalse(dao.isDirty());

        // Allocations can't be negative
        try {
            dao.getUnallocatedVoteBalanceChangeIfWeAllocatedThis(policyID3, -1);
            fail();
        } catch (IllegalArgumentException expected) {}
        try {
            dao.setVotesAllocated(policyID3, -1);
            fail();
        } catch (IllegalArgumentException expected) {}
    }

    @Test(groups = {"dao"})
    public void testVotingConfig() {
        assertEquals(DEFAULT_VOTE_COST_TO_CREATE_POLICY, manager.getVoteCostToCreatePolicy());
        assertEquals(DEFAULT_VOTE_WITHDRAWAL_PENALTY_PERCENTAGE, manager.getVoteWithdrawalPenaltyPercentage());
        assertEquals(DEFAULT_USER_VOTE_SALARY_INCREMENT, manager.getUserVoteSalaryIncrement());
        assertEquals(DEFAULT_USER_VOTE_SALARY_FREQUENCY_DAYS, manager.getUserVoteSalaryFrequencyDays());
        assertEquals(DEFAULT_USER_VOTE_SALARY_WEEKLY_DAY_OF_WEEK, manager.getUserVoteSalaryWeeklyDayOfWeek());

        manager.setVoteCostToCreatePolicy(DEFAULT_VOTE_COST_TO_CREATE_POLICY + 10);
        manager.setVoteWithdrawalPenaltyPercentage((byte) (DEFAULT_VOTE_WITHDRAWAL_PENALTY_PERCENTAGE - 5));
        manager.setUserVoteSalaryIncrement(DEFAULT_USER_VOTE_SALARY_INCREMENT + 20);
        manager.setUserVoteSalaryFrequencyDays((short) (DEFAULT_USER_VOTE_SALARY_FREQUENCY_DAYS + 25));
        manager.setUserVoteSalaryWeeklyDayOfWeek(DayOfWeek.THURSDAY);

        assertEquals(DEFAULT_VOTE_COST_TO_CREATE_POLICY + 10, manager.getVoteCostToCreatePolicy());
        assertEquals(DEFAULT_VOTE_WITHDRAWAL_PENALTY_PERCENTAGE - 5, manager.getVoteWithdrawalPenaltyPercentage());
        assertEquals(DEFAULT_USER_VOTE_SALARY_INCREMENT + 20 , manager.getUserVoteSalaryIncrement());
        assertEquals(DEFAULT_USER_VOTE_SALARY_FREQUENCY_DAYS + 25, manager.getUserVoteSalaryFrequencyDays());
        assertEquals(DayOfWeek.THURSDAY, manager.getUserVoteSalaryWeeklyDayOfWeek());
    }

    @Test(groups = {"dao"})
    public void testCollisionResolution() throws NoSuchUserException {
        PolicyID policyID1 = new PolicyIDImpl();
        PolicyID policyID2 = new PolicyIDImpl();

        UserID userID = new UserIDImpl();
        CurrentUserVotesImpl sharedBranch = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID);
        assertEmptyVoteAllocation(sharedBranch, userID);
        final long initialUnallocated = sharedBranch.getUnallocatedVotes();
        long branch1Unallocated = initialUnallocated;

        // Assign and save 2 vote records to 1
        sharedBranch.setVotesAllocated(policyID1, 2);
        sharedBranch.setVotesAllocated(policyID2, 1);
        branch1Unallocated -= (2 + 1);
        manager.save(sharedBranch);
        sharedBranch = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID);
        assertEquals(sharedBranch.getUnallocatedVotes(), initialUnallocated - (2 + 1));
        assertEquals(sharedBranch.getVotesAllocated(policyID1), 2);
        assertEquals(sharedBranch.getVotesAllocated(policyID2), 1);

        sharedBranch.setVotesAllocated(policyID1, 4);
        sharedBranch.setVotesAllocated(policyID2, 2);
        branch1Unallocated -= (2 + 1);
        manager.save(sharedBranch);
        // This will be the last common parent after which the two branches diverge
        sharedBranch = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID);
        assertEquals(sharedBranch.getUnallocatedVotes(), initialUnallocated - (4 + 2));
        assertEquals(sharedBranch.getVotesAllocated(policyID1), 4);
        assertEquals(sharedBranch.getVotesAllocated(policyID2), 2);

        // Use split point as basis for 2nd "branch" of vote saves
        CurrentUserVotesImpl branch1 = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID);
        branch1.setVotesAllocated(policyID1, 16);
        branch1.setVotesAllocated(policyID2, 13);
        manager.save(branch1);
        branch1 = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID);
        assertEquals(branch1.getUnallocatedVotes(), initialUnallocated - (16 + 13));
        assertEquals(branch1.getVotesAllocated(policyID1), 16);
        assertEquals(branch1.getVotesAllocated(policyID2), 13);

        branch1.setVotesAllocated(policyID1, 18);
        branch1.setVotesAllocated(policyID2, 14);
        manager.save(branch1);
        branch1 = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID);
        assertEquals(branch1.getUnallocatedVotes(), initialUnallocated - (18 + 14));
        assertEquals(branch1.getVotesAllocated(policyID1), 18);
        assertEquals(branch1.getVotesAllocated(policyID2), 14);

        // Save on branch2 from the common ancestor, confirm retrievable
        CurrentUserVotesImpl branch2 = sharedBranch;
        branch2.setVotesAllocated(policyID1, 6);
        branch2.setVotesAllocated(policyID2, 3);
        manager.save(branch2);
        branch2 = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID);
        assertEquals(branch2.getUnallocatedVotes(), initialUnallocated - (6 + 3));
        assertEquals(branch2.getVotesAllocated(policyID1), 6);
        assertEquals(branch2.getVotesAllocated(policyID2), 3);
        
        branch2.setVotesAllocated(policyID1, 8);
        branch2.setVotesAllocated(policyID2, 4);
        manager.save(branch2);
        branch2 = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID);
        assertEquals(branch2.getUnallocatedVotes(), initialUnallocated - (8 + 4));
        assertEquals(branch2.getVotesAllocated(policyID1), 8);
        assertEquals(branch2.getVotesAllocated(policyID2), 4);
        VoteRecordID branch2Parent = branch2.getPreviousVoteID();

        // Try saving on branch1 head again; retrieve; it should be lost and we should get the head of branch2 instead.
        // This is because branch1 already lost the conflict resolution when we first re-retrieved branch2:
        // - the nodes only on branch1 were already deleted.
        branch1.setVotesAllocated(policyID1, 20);
        branch1.setVotesAllocated(policyID2, 16);
        manager.save(branch1);
        CurrentUserVotesImpl conflicted = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID);
        assertEquals(conflicted.getUnallocatedVotes(), initialUnallocated - (8 + 4));
        assertEquals(conflicted.getVotesAllocated(policyID1), 8);
        assertEquals(conflicted.getVotesAllocated(policyID2), 4);
        assertEquals(conflicted.getPreviousVoteID(), branch2Parent);
    }

    // TODO: test NoSuchUserException once manager is actually using the user registration records
}
