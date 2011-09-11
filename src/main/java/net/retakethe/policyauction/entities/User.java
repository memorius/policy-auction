package net.retakethe.policyauction.entities;

import java.io.Serializable;
import java.util.Date;

import net.retakethe.policyauction.data.api.dao.UserDAO;
import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.api.types.UserRole;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;

public class User implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1822423197198171663L;

	private final UserDAO userDAO;
	
	/**
	 * Instantiates a new user - this will be called by tapestry, one should always expect there to be a user object, but not nec. a logged in one (with any data in the DAO).
	 */
	public User() {
		this.userDAO = null;
	}

	public User(final UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@NonVisual
	protected UserDAO getUserDAO() {
		return userDAO;
	}

	public UserID getUserID() {
		return userDAO.getUserID();
	}

	@Validate("required")
	public String getUsername() {
		return userDAO.getUsername();
	}

	public void setUsername(final String username) {
		this.userDAO.setUsername(username);
	}

	@Validate("required")
	public String getEmail() {
		return userDAO.getEmail();
	}

	public void setEmail(final String email) {
		userDAO.setEmail(email);
	}

	public String getPasswordHash() {
		return userDAO.getPasswordHash();
	}

	public void setPasswordHash(final String password) {
		userDAO.setPassword(password);
	}

	public String getFirstName() {
		return userDAO.getFirstName();
	}

	public void setFirstName(final String firstName) {
		userDAO.setFirstName(firstName);
	}

	public String getLastName() {
		return userDAO.getLastName();
	}

	public void setLastName(final String lastName) {
		userDAO.setLastName(lastName);
	}

	public boolean isShowRealName() {
		return userDAO.isShowRealName();
	}

	public void setShowRealName(final boolean showRealName) {
		userDAO.setShowRealName(showRealName);
	}

	@NonVisual
	public Date getCreatedTimestamp() {
		return userDAO.getCreatedTimestamp();
	}

	@NonVisual
	public Date getPasswordExpiryTimestamp() {
		return userDAO.getPasswordExpiryTimestamp();
	}

	@NonVisual
	public Date getVoteSalaryLastPaidTimestamp() {
		return userDAO.getVoteSalaryLastPaidTimestamp();
	}

	@NonVisual
	public Date getVoteSalaryDate() {
		return userDAO.getVoteSalaryDate();
	}

	public UserRole getRole() {
		return userDAO.getUserRole();
	}

	public void setRole(final UserRole role) {
		userDAO.setUserRole(role);
	}

	public void setRole(final String role) {
		for (UserRole userRole : UserRole.values()) {
			if (userRole.equals(role)) {
				userDAO.setUserRole(userRole);
				break;
			}
		}
		if (userDAO.getUserRole() == null) {
			userDAO.setUserRole(UserRole.USER);
		}
	}
	
	public boolean isLoggedIn() {
		return userDAO != null;
	}
}
