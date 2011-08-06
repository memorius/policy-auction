package net.retakethe.policyauction.data.api;

import java.util.List;


/**
 * @author Nick Clarke
 */
public interface PolicyManager {

    public class NoSuchPolicyException extends Exception {
        private static final long serialVersionUID = 0L;

        public NoSuchPolicyException(PolicyID policyID) {
            super("No policy found with id '" + policyID.asString() + "'");
        }
    }

    PolicyID makePolicyID(String asString);

    PolicyDAO getPolicy(PolicyID policyID) throws NoSuchPolicyException;

    PolicyDAO createPolicy();

    List<PolicyDAO> getAllPolicies();

    void persist(PolicyDAO policy);

    void deletePolicy(PolicyDAO policy);
}
