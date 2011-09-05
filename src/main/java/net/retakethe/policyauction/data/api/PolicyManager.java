package net.retakethe.policyauction.data.api;

import java.util.List;

import net.retakethe.policyauction.data.api.dao.PolicyDAO;
import net.retakethe.policyauction.data.api.dao.PolicyDetailsDAO;
import net.retakethe.policyauction.data.api.exceptions.NoSuchPolicyException;
import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.data.api.types.PortfolioID;
import net.retakethe.policyauction.data.api.types.UserID;


/**
 * @author Nick Clarke
 */
public interface PolicyManager {

    PolicyID makePolicyID(String asString);

    PolicyDAO getPolicy(PolicyID policyID) throws NoSuchPolicyException;

    PolicyDetailsDAO getPolicyDetails(PolicyID policyID) throws NoSuchPolicyException;

    /**
     * @param ownerUserID must not be null
     */
    PolicyDetailsDAO createPolicy(UserID ownerUserID, PortfolioID portfolioID);

    // TODO: two methods - active and ALL. Active can use fast single-row index.
    List<PolicyDAO> getAllPolicies();

    void save(PolicyDetailsDAO policy);

    void deletePolicy(PolicyID policy);
}
