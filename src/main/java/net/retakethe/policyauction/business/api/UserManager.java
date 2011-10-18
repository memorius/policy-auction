/*
 * 
 */
package net.retakethe.policyauction.business.api;

import net.retakethe.policyauction.entities.User;

/**
 * The Interface UserManager.
 */
public interface UserManager {
	
	/**
	 * Creates the user, not yet stored into database.
	 *
	 * @return the user
	 */
	User createUser();
	
	/**
	 * Provision user, giving them an activation code, emailing their
	 * email address and storing that activation code in the database
	 * along with the skeleton user object.
	 *
	 * @param user the user
	 * @param url the url (up to but excluding user-specific parameters).
	 * @return the user
	 */
	User provisionUser(User user, String url);
	
	/**
	 * Load new user (That is in the process of being activated). For registered users, use {@link authenticateUser }.
	 *
	 * @param email the email
	 * @param activationCode the activation code
	 * @return the user
	 */
	User loadNewUser(String email, String activationCode);
	
	/**
	 * Activate user, saving new details to the database and removing old code.
	 *
	 * @param user the user
	 * @return the user
	 */
	User activateUser(User user);
	
	/**
	 * Authenticate user, check that username exists, has valid configuration and that the password matches the stored one.
	 *
	 * @param username the username
	 * @param password the password
	 * @return the user
	 */
	User authenticateUser(String username, String password);
}
