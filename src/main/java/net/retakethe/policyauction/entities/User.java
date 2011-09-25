package net.retakethe.policyauction.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import net.retakethe.policyauction.data.api.dao.UserDAO;
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

	public void setPasswordExpiryTimestamp(final Date passwordExpiryTimestamp) {
		userDAO.setPasswordExpiryTimestamp(passwordExpiryTimestamp);
	}

	@NonVisual
	public Date getVoteSalaryLastPaidTimestamp() {
		return userDAO.getVoteSalaryLastPaidTimestamp();
	}

	public void setVoteSalaryLastPaidTimestamp(final Date voteSalaryLastPaidTimestamp) {
		userDAO.setVoteSalaryLastPaidTimestamp(voteSalaryLastPaidTimestamp);
	}

	@NonVisual
	public Date getVoteSalaryDate() {
		return userDAO.getVoteSalaryDate();
	}

	public void setVoteSalaryDate(final Date voteSalaryDate) {
		userDAO.setVoteSalaryDate(voteSalaryDate);
	}

	public List<UserRole> getRoles() {
		return userDAO.getUserRoles();
	}

	public void setRoles(final List<UserRole> role) {
		userDAO.setUserRoles(role);
	}
	
	public boolean isRole(UserRole role) {
		return userDAO.getUserRoles().contains(role);
	}
	
	public boolean isLoggedIn() {
		return userDAO != null;
	}
}
