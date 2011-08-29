package net.retakethe.policyauction.data.api;

import java.util.List;

import net.retakethe.policyauction.data.api.dao.PolicyDAO;
import net.retakethe.policyauction.data.api.dao.PolicyDetailsDAO;
import net.retakethe.policyauction.data.api.exceptions.NoSuchPolicyException;
import net.retakethe.policyauction.data.api.types.PolicyID;


/**
 * @author Nick Clarke
 */
public interface PolicyManager {

    PolicyID makePolicyID(String asString);

    PolicyDAO getPolicy(PolicyID policyID) throws NoSuchPolicyException;

    PolicyDetailsDAO getPolicyDetails(PolicyID policyID) throws NoSuchPolicyException;

    PolicyDetailsDAO createPolicy();

    // TODO: two methods - active and ALL. Active can use fast single-row index.
    List<PolicyDAO> getAllPolicies();

    // TODO: remove this: we shouldn't be retrieving details of all policies in one go;
    //       instead use getAllPolicies to list, and retrieve details for individual items only as needed
    List<PolicyDetailsDAO> getAllPolicyDetails();

    void save(PolicyDetailsDAO policy);

    void deletePolicy(PolicyID policy);
}
