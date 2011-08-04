package net.retakethe.policyauction.data.impl;

import static org.testng.Assert.assertEquals;

import java.util.List;

import net.retakethe.policyauction.data.api.PolicyManager.NoSuchPolicyException;
import net.retakethe.policyauction.entities.Policy;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import _fixtures.CleanDbEveryMethodHectorTestBase;

public class HectorPolicyManagerImplTest extends CleanDbEveryMethodHectorTestBase {

    private HectorPolicyManagerImpl manager;

    @BeforeMethod(groups = {"dao"})
    public void setupManager() {
        manager = getDAOManager().getPolicyManager();
    }

    @Test(groups = {"dao"})
    public void testCreatePersistGetPolicy() throws NoSuchPolicyException {
        Policy p = manager.createPolicy();
        p.setDescription("My policy");
        p.setShortName("My short name");
        manager.persist(p);

        Policy retrieved = manager.getPolicy(p.getPolicyID());
        assertEquals(retrieved.getPolicyID(), p.getPolicyID());
        assertEquals(retrieved.getDescription(), p.getDescription());
        assertEquals(retrieved.getShortName(), p.getShortName());
    }

    @Test(groups = {"dao"})
    public void testGetAllPolicies() {
        Policy p1 = manager.createPolicy();
        p1.setDescription("My policy 1");
        p1.setShortName("My short name 1");
        manager.persist(p1);
        
        Policy p2 = manager.createPolicy();
        p2.setDescription("My policy 2");
        p2.setShortName("My short name 2");
        manager.persist(p2);

        List<Policy> policies = manager.getAllPolicies();
        assertEquals(policies.size(), 2);
    }
}
