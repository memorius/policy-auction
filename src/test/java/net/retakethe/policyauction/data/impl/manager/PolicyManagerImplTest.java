package net.retakethe.policyauction.data.impl.manager;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import java.util.Date;
import java.util.List;

import net.retakethe.policyauction.data.api.dao.PolicyDAO;
import net.retakethe.policyauction.data.api.dao.PolicyDetailsDAO;
import net.retakethe.policyauction.data.api.dao.PolicyState;
import net.retakethe.policyauction.data.api.exceptions.NoSuchPolicyException;
import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.data.api.types.PortfolioID;
import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.impl.types.PortfolioIDImpl;
import net.retakethe.policyauction.data.impl.types.UserIDImpl;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import _fixtures.CleanDbEveryMethodDAOManagerTestBase;

public class PolicyManagerImplTest extends CleanDbEveryMethodDAOManagerTestBase {

    private PolicyManagerImpl manager;

    @BeforeMethod(groups = {"dao"})
    public void setupManager() {
        manager = getDAOManager().getPolicyManager();
    }

    @Test(groups = {"dao"})
    public void testCreatePersistGetPolicy() throws NoSuchPolicyException {
        UserID userID = createUserID();
        PortfolioID portfolioID = createPortfolioID();
        Date stateChangeAfter = new Date();
        PolicyDetailsDAO p = manager.createPolicy(userID, portfolioID);
        p.setDescription("My policy");
        p.setShortName("My short name");
        p.setRationaleDescription("none whatsoever");
        p.setCostsToTaxpayersDescription("lots");
        p.setWhoIsAffectedDescription("everyone");
        p.setHowAffectedDescription("badly");
        p.setIsPartyOfficialPolicy(true);

        PolicyID id = p.getPolicyID();

        Date editedAfter = new Date();

        manager.save(p);

        PolicyDAO policy = manager.getPolicy(id);
        assertEquals(policy.getPolicyID(), id);
        assertEquals(policy.getShortName(), p.getShortName());

        PolicyDetailsDAO details = manager.getPolicyDetails(id);
        assertEquals(details.getPolicyID(), id);
        assertEquals(details.getShortName(), p.getShortName());
        assertEquals(details.getPolicyState(), PolicyState.ACTIVE);
        assertEquals(details.getOwnerUserID(), userID);
        assertEquals(details.getPortfolioID(), portfolioID);
        assertEquals(details.getRationaleDescription(), p.getRationaleDescription());
        assertEquals(details.getWhoIsAffectedDescription(), p.getWhoIsAffectedDescription());
        assertEquals(details.getHowAffectedDescription(), p.getHowAffectedDescription());
        assertEquals(details.getCostsToTaxpayersDescription(), p.getCostsToTaxpayersDescription());
        assertEquals(details.isPartyOfficialPolicy(), p.isPartyOfficialPolicy());
        assertEquals(details.getDescription(), p.getDescription());
        assertFalse(details.getLastEdited().before(editedAfter));
        assertFalse(details.getStateChanged().before(stateChangeAfter));
    }

    private UserID createUserID() {
        return new UserIDImpl();
    }

    private PortfolioID createPortfolioID() {
        return new PortfolioIDImpl(PortfolioManagerImplTest.KNOWN_PORTFOLIO_ID);
    }

    @Test(groups = {"dao"}, expectedExceptions = NoSuchPolicyException.class)
    public void testGetNonExistentPolicy() throws NoSuchPolicyException {
        // Get for ID that hasn't been stored yet
        PolicyDetailsDAO p = manager.createPolicy(createUserID(), createPortfolioID());

        manager.getPolicy(p.getPolicyID());
    }

    @Test(groups = {"dao"})
    public void testGetDeletedPolicy() throws NoSuchPolicyException {
        // Get for ID that's been stored and deleted
        PolicyDetailsDAO p = manager.createPolicy(createUserID(), createPortfolioID());

        p.setDescription("blah");
        p.setShortName("blah");
        manager.save(p);

        PolicyDAO retrieved = manager.getPolicy(p.getPolicyID());
        Assert.assertNotNull(retrieved);

        manager.deletePolicy(p.getPolicyID());

        try {
            manager.getPolicy(p.getPolicyID());
            fail();
        } catch (NoSuchPolicyException expected) {}
    }

    @Test(groups = {"dao"})
    public void testGetAllPolicies() {
        PolicyDetailsDAO p1 = manager.createPolicy(createUserID(), createPortfolioID());
        p1.setShortName("My short name 1");
        manager.save(p1);

        PolicyDetailsDAO p2 = manager.createPolicy(createUserID(), createPortfolioID());
        p2.setShortName("My short name 2");
        manager.save(p2);

        List<PolicyDAO> policies = manager.getAllPolicies();
        assertEquals(policies.size(), 2);
    }
}
