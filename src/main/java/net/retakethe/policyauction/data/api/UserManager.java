package net.retakethe.policyauction.data.api;

import java.util.List;

import net.retakethe.policyauction.data.api.dao.UserDAO;
import net.retakethe.policyauction.data.api.types.UserID;

/**
 * @author Mathew Hartley
 */
public interface UserManager {

    public class NoSuchUserException extends Exception {
        private static final long serialVersionUID = 0L;

        public NoSuchUserException(UserID userID) {
            super("No user found with id '" + userID.asString() + "'");
        }
    }

    UserID makeUserID(String asString);

    UserDAO getUser(UserID userID) throws NoSuchUserException;

    UserDAO createUser();

    List<UserDAO> getAllUsers();

    void persist(UserDAO user);

	void deleteUser(UserDAO user);
}
