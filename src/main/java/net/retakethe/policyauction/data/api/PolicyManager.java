package net.retakethe.policyauction.data.api;

import java.util.List;

import net.retakethe.policyauction.entities.Policy;

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

    Policy getPolicy(PolicyID policyID) throws NoSuchPolicyException;

    Policy createPolicy();

    List<Policy> getAllPolicies();

    void persist(Policy policy);
}
