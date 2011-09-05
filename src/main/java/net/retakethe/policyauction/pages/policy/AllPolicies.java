package net.retakethe.policyauction.pages.policy;

import java.util.List;

import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.data.api.PortfolioManager;
import net.retakethe.policyauction.data.api.exceptions.NoSuchPortfolioException;
import net.retakethe.policyauction.data.api.types.PortfolioID;
import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.impl.types.UserIDImpl;
import net.retakethe.policyauction.entities.EntityFactory;
import net.retakethe.policyauction.entities.Policy;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;


public class AllPolicies {

    @InjectPage
    private EditPolicy editPolicyPage;

    @Inject
    private DAOManager daoManager;

    public List<Policy> getAllPolicies() {
        return EntityFactory.makePolicyFromDAO(daoManager.getPolicyManager().getAllPolicies());
    }

    public Object onActionFromAdd()  {
        UserID userID = getLoggedInUserID();

        PortfolioID portfolioID = selectPortfolioID();

        editPolicyPage.setup(EntityFactory.makePolicyDetailsFromDAO(
                daoManager.getPolicyManager().createPolicy(userID, portfolioID)), false);
        return editPolicyPage;
    }

    private PortfolioID selectPortfolioID() {
        // FIXME: this is just for testing; select from the list of all portfolio IDs instead.
        PortfolioManager portfolioManager = daoManager.getPortfolioManager();
        PortfolioID portfolioID;
        try {
            portfolioID = portfolioManager.getPortfolio(portfolioManager.makePortfolioID("Education")).getPortfolioID();
        } catch (NoSuchPortfolioException e) {
            throw new RuntimeException(e);
        }
        return portfolioID;
    }

    private UserID getLoggedInUserID() {
        // FIXME: get from session once login is implemented
        return new UserIDImpl();
    }
}
