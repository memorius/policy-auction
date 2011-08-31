package net.retakethe.policyauction.services;

import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.data.api.UserManager;
import net.retakethe.policyauction.data.api.UserManager.NoSuchUserException;
import net.retakethe.policyauction.data.api.dao.UsernameDAO;
import net.retakethe.policyauction.entities.EntityFactory;
import net.retakethe.policyauction.entities.User;

/**
 * The Class Security, for user authentication and loading of a user into a session state once authentication succeeds.
 */
public class Security {
	
	public static final User AuthenticateUser(final DAOManager daoManager, final String username, final String password) {
		UserManager userManager = daoManager.getUserManager();
		
		try {
			UsernameDAO usernameDAO = userManager.getUsername(username);
			if (usernameDAO != null) {
				return EntityFactory.makeUser(userManager.getUser(usernameDAO.getUserID()));
			}
		} catch (NoSuchUserException e) {
			// TODO this should be logging.
		}
		return null;
	}

}
