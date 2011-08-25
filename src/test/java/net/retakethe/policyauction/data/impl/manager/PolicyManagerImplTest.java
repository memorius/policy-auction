package net.retakethe.policyauction.data.impl.manager;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import java.util.Date;
import java.util.List;

import net.retakethe.policyauction.data.api.dao.PolicyDAO;
import net.retakethe.policyauction.data.api.exceptions.NoSuchPolicyException;

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
        PolicyDAO p = manager.createPolicy();
        p.setDescription("My policy");
        p.setShortName("My short name");

        Date editedAfter = new Date();

        manager.save(p);

        PolicyDAO retrieved = manager.getPolicy(p.getPolicyID());
        assertEquals(retrieved.getPolicyID(), p.getPolicyID());
        assertEquals(retrieved.getDescription(), p.getDescription());
        assertEquals(retrieved.getShortName(), p.getShortName());
        assertFalse(retrieved.getLastEdited().before(editedAfter));
    }

    @Test(groups = {"dao"}, expectedExceptions = NoSuchPolicyException.class)
    public void testGetNonExistentPolicy() throws NoSuchPolicyException {
        // Get for ID that hasn't been stored yet
        PolicyDAO p = manager.createPolicy();

        manager.getPolicy(p.getPolicyID());
    }

    @Test(groups = {"dao"})
    public void testGetDeletedPolicy() throws NoSuchPolicyException {
        // Get for ID that's been stored and deleted
        PolicyDAO p = manager.createPolicy();

        p.setDescription("blah");
        p.setShortName("blah");
        manager.save(p);

        PolicyDAO retrieved = manager.getPolicy(p.getPolicyID());
        Assert.assertNotNull(retrieved);

        manager.deletePolicy(p);

        try {
            manager.getPolicy(p.getPolicyID());
            fail();
        } catch (NoSuchPolicyException expected) {}
    }

    @Test(groups = {"dao"})
    public void testGetAllPolicies() {
        PolicyDAO p1 = manager.createPolicy();
        p1.setDescription("My policy 1");
        p1.setShortName("My short name 1");
        manager.save(p1);

        PolicyDAO p2 = manager.createPolicy();
        p2.setDescription("My policy 2");
        p2.setShortName("My short name 2");
        manager.save(p2);

        List<PolicyDAO> policies = manager.getAllPolicies();
        assertEquals(policies.size(), 2);
    }
}
