package net.retakethe.policyauction.data.api.exceptions;

import net.retakethe.policyauction.data.api.types.UserID;

public class NoSuchUserException extends Exception {
    private static final long serialVersionUID = 0L;

    public NoSuchUserException(UserID userID) {
        super("No user found with id '" + userID.asString() + "'");
    }
}