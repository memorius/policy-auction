package net.retakethe.policyauction.pages.policy;

import java.util.List;

import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.data.api.exceptions.NoSuchUserException;
import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.entities.EntityFactory;
import net.retakethe.policyauction.entities.Policy;
import net.retakethe.policyauction.entities.User;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;

public class VoteAllocation {
    @SessionState(create = false)
    private User currentUser;

    private boolean currentUserExists;

    @Property
    private Policy policy;

    @Inject
    private DAOManager daoManager;

    public List<Policy> getAllPolicies() {
        return EntityFactory.makePolicyFromDAO(daoManager.getPolicyManager()
                .getAllPolicies());
    }

    public long getUnallocatedVotes() {
        long unallocatedVotes = 0;
        if (currentUserExists) {
            try {
                unallocatedVotes = daoManager.getUserVoteAllocationManager()
                        .getCurrentUserVoteAllocation(currentUser.getUserID())
                        .getUnallocatedVotes();
            } catch (NoSuchUserException e) {
                throw new RuntimeException(e);
            }
        }
        return unallocatedVotes;
    }

    public long getPolicyVotes(PolicyID policyID) {
        long votesAllocated = 0;
        if (currentUserExists) {
            try {
                votesAllocated = daoManager.getUserVoteAllocationManager()
                        .getCurrentUserVoteAllocation(currentUser.getUserID())
                        .getVotesAllocated(policyID);
            } catch (NoSuchUserException e) {
                throw new RuntimeException(e);
            }
        }
        return votesAllocated;
    }
}
