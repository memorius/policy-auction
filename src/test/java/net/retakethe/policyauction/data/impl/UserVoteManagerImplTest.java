package net.retakethe.policyauction.data.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import net.retakethe.policyauction.data.api.exceptions.InsufficientVotesException;
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

public class UserVoteManagerImplTest extends CleanDbEveryMethodDAOManagerTestBase {

    private static final long DEFAULT_VOTE_SALARY = 100L;
    private static final long DEFAULT_VOTE_COST_TO_CREATE_POLICY = 100L;
    private static final byte DEFAULT_VOTE_WITHDRAWAL_PENALTY_PERCENTAGE = 40;

    private static final VoteRecordID ZERO_VOTE_RECORD_ID = new VoteRecordIDImpl(UUIDUtils.getZeroTimeUUID());

    private UserVoteManagerImpl manager;

    @BeforeMethod(groups = {"dao"})
    public void setupManager() {
        manager = getDAOManager().getUserVoteManager();
    }

    @Test(groups = {"dao"})
    public void testVoteAllocation() {
        // Set values convenient to our test
        final long voteCostToCreatePolicy = 20L;
        manager.setVoteCostToCreatePolicy(voteCostToCreatePolicy);
        final byte voteWithdrawalPenaltyPercentage = (byte) 40;
        manager.setVoteWithdrawalPenaltyPercentage(voteWithdrawalPenaltyPercentage);

        UserID userID = new UserIDImpl();
        CurrentUserVotesImpl dao = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID);
        assertNull(dao.getCreatedPolicyID());
        assertEquals(dao.getPreviousVoteID(), ZERO_VOTE_RECORD_ID);
        assertEquals(dao.getUserID(), userID);
        assertEquals(dao.getUnallocatedVotes(), DEFAULT_VOTE_SALARY);
        assertFalse(dao.isDirty());
        PolicyID policyID1 = new PolicyIDImpl();
        PolicyID policyID2 = new PolicyIDImpl();
        assertEquals(dao.getVotesAllocated(policyID1), 0);
        assertEquals(dao.getVotesAllocated(policyID2), 0);
        assertEquals(dao.getPolicyIDsVotedOn().size(), 0);
        assertEquals(dao.getPolicyVotes().size(), 0);

        // Setting to current value does nothing
        dao.setVotesAllocated(policyID1, 0);
        assertEquals(dao.getPolicyIDsVotedOn().size(), 0);
        assertEquals(dao.getPolicyVotes().size(), 0);
        assertFalse(dao.isDirty());

        // Save is a no op when not modified
        manager.save(dao);
        dao = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID);
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
        dao = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID);
        assertEquals(dao.getVotesAllocated(policyID1), policy1Votes);
        assertEquals(dao.getVotesAllocated(policyID2), policy2Votes);
        assertEquals(dao.getPolicyIDsVotedOn().size(), 2);
        assertEquals(dao.getUnallocatedVotes(), votesUnallocated);
        assertNull(dao.getCreatedPolicyID());
        assertEquals(dao.getUserID(), userID);
        assertFalse(dao.isDirty());
        // New ID was written and will be the parent of this one if saved again
        assertNotEquals(dao.getPreviousVoteID(), ZERO_VOTE_RECORD_ID);

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
        dao = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID);
        assertEquals(dao.getUnallocatedVotes(), votesUnallocated);
        // Allowed
        assertEquals(dao.getVotesAllocated(policyID2), policy2Votes);
        long policy2MaxVotes = policy2Votes + votesUnallocated;
        dao.setVotesAllocated(policyID2, policy2MaxVotes);
        assertEquals(dao.getVotesAllocated(policyID2), policy2MaxVotes);
        assertTrue(dao.isDirty());

        dao = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID);
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

        dao = (CurrentUserVotesImpl) manager.getCurrentUserVoteAllocation(userID);
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

        manager.setVoteCostToCreatePolicy(DEFAULT_VOTE_COST_TO_CREATE_POLICY + 10);
        assertEquals(DEFAULT_VOTE_COST_TO_CREATE_POLICY + 10, manager.getVoteCostToCreatePolicy());

        manager.setVoteWithdrawalPenaltyPercentage((byte) (DEFAULT_VOTE_WITHDRAWAL_PENALTY_PERCENTAGE - 5));
        assertEquals(DEFAULT_VOTE_WITHDRAWAL_PENALTY_PERCENTAGE - 5, manager.getVoteWithdrawalPenaltyPercentage());
    }
}
