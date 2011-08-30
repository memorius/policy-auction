package net.retakethe.policyauction.data.impl.manager;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.util.List;

import net.retakethe.policyauction.data.api.dao.PortfolioDAO;
import net.retakethe.policyauction.data.api.exceptions.NoSuchPortfolioException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import _fixtures.CleanDbEveryMethodDAOManagerTestBase;

public class PortfolioManagerImplTest extends CleanDbEveryMethodDAOManagerTestBase {

    // These values will need updating with Schema.PortfoliosCF.initialize
    private static String KNOWN_PORTFOLIO_ID = "Education";
    private static String KNOWN_PORTFOLIO_NAME = KNOWN_PORTFOLIO_ID;
    private static String KNOWN_PORTFOLIO_DESCRIPTION = "Schools, universities, ECE";
    private static int NUMBER_OF_PORTFOLIOS = 3;

    private PortfolioManagerImpl manager;

    @BeforeMethod(groups = {"dao"})
    public void setupManager() {
        manager = getDAOManager().getPortfolioManager();
    }

    @Test(groups = {"dao"})
    public void testGetPortfolio() throws NoSuchPortfolioException {
        PortfolioDAO dao = manager.getPortfolio(manager.makePortfolioID(KNOWN_PORTFOLIO_ID));
        assertNotNull(dao);
        assertEquals(dao.getPortfolioID(), manager.makePortfolioID(KNOWN_PORTFOLIO_ID));
        assertEquals(dao.getName(), KNOWN_PORTFOLIO_NAME);
        assertEquals(dao.getDescription(), KNOWN_PORTFOLIO_DESCRIPTION);
    }

    @Test(groups = {"dao"}, expectedExceptions = NoSuchPortfolioException.class)
    public void testGetNonExistentPortfolio() throws NoSuchPortfolioException {
        manager.getPortfolio(manager.makePortfolioID("this portfolio does not exist"));
    }

    @Test(groups = {"dao"})
    public void testGetAllPortfolios() {
        List<PortfolioDAO> all = manager.getAllPortfolios();
        assertEquals(all.size(), NUMBER_OF_PORTFOLIOS);

        // Currently random ordered; just check for one known ID
        // TODO: check ordering once "all IDs" lookup row implemented
        PortfolioDAO found = null;
        for (PortfolioDAO dao : all) {
            if (dao.getPortfolioID().equals(manager.makePortfolioID(KNOWN_PORTFOLIO_ID))) {
                found = dao;
            }
        }
        if (found == null) {
            fail();
        } else {
            assertEquals(found.getName(), KNOWN_PORTFOLIO_NAME);
            assertEquals(found.getDescription(), KNOWN_PORTFOLIO_DESCRIPTION);
        }
    }
}
