package net.retakethe.policyauction.data.impl;

import net.retakethe.policyauction.data.impl.manager.VoteSalaryManagerImpl;

import org.testng.annotations.BeforeMethod;

import _fixtures.CleanDbEveryMethodDAOManagerTestBase;

/**
 * @author Nick Clarke
 */
public class VoteSalaryManagerImplTest extends CleanDbEveryMethodDAOManagerTestBase {

    private VoteSalaryManagerImpl manager;

    @BeforeMethod(groups = {"dao"})
    public void setupManager() {
        manager = getDAOManager().getVoteSalaryManager();
    }

    // TODO: test NoSuchUserException once manager is actually using the user registration records
}
