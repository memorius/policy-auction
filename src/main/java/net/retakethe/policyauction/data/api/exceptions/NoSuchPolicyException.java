package net.retakethe.policyauction.data.api.exceptions;

import net.retakethe.policyauction.data.api.types.PolicyID;

public class NoSuchPolicyException extends Exception {
    private static final long serialVersionUID = 0L;

    public NoSuchPolicyException(PolicyID policyID) {
        super("No policy found with id '" + policyID.asString() + "'");
    }
}