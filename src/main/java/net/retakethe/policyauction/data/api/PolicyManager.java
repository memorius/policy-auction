package net.retakethe.policyauction.data.api;

import java.util.List;

import net.retakethe.policyauction.data.api.dao.PolicyDAO;
import net.retakethe.policyauction.data.api.exceptions.NoSuchPolicyException;
import net.retakethe.policyauction.data.api.types.PolicyID;


/**
 * @author Nick Clarke
 */
public interface PolicyManager {

    PolicyID makePolicyID(String asString);

    PolicyDAO getPolicy(PolicyID policyID) throws NoSuchPolicyException;

    PolicyDAO createPolicy();

    List<PolicyDAO> getAllPolicies();

    void save(PolicyDAO policy);

    void deletePolicy(PolicyDAO policy);
}
