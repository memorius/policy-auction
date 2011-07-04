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

    Policy getPolicy(PolicyID policyID) throws NoSuchPolicyException;

    Policy createPolicy();

    // TODO: should this be on the Policy interface?
    //       Need to see some of the use cases on how multiple data objects interact before we decide this.
    void storePolicy(Policy policy);

    List<Policy> getAllPolicies();
}
