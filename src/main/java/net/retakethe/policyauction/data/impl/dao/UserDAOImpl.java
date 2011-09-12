package net.retakethe.policyauction.data.impl.dao;

import java.util.Date;
import java.util.List;

import net.retakethe.policyauction.data.api.dao.UserDAO;
import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.api.types.UserRole;

import org.mindrot.jbcrypt.BCrypt;

public class UserDAOImpl implements UserDAO {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1186674628028659343L;

	private final UserID userID;

	private String username;
	private String email;
	private String passwordHash;
	private Date passwordExpiryTimestamp;

	private String firstName;
	private String lastName;

	private boolean showRealName;

	private Date createdTimestamp;
	private Date voteSalaryLastPaidTimestamp;
	private Date voteSalaryDate;

	private List<UserRole> userRoles;

	public UserDAOImpl(final UserID userID) {
		if (userID == null ){
			throw new IllegalArgumentException("userID must not be null");
		}
		this.userID = userID;
	}

	public UserDAOImpl(final UserID userID, final String username, final String email, final String passwordHash, final Date passwordExpiryTimestamp, final String firstName, final String lastName, final Boolean showRealName, final Date createdTimestamp, final Date voteSalaryLastPaidTimestamp, final Date voteSalaryDate, final List<UserRole> userRoles) {
		this.userID = userID;
		this.username = username;
		this.email = email;

		this.passwordHash = passwordHash;
		this.passwordExpiryTimestamp = passwordExpiryTimestamp;

		this.firstName = firstName;
		this.lastName = lastName;

		this.showRealName = showRealName;

		this.createdTimestamp = createdTimestamp;
		this.voteSalaryLastPaidTimestamp = voteSalaryLastPaidTimestamp;
		this.voteSalaryDate = voteSalaryDate;

		this.userRoles = userRoles;
	}

	@Override
	public UserID getUserID() {
		return userID;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(final String username) {
		this.username = username;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(final String email) {
		this.email = email;
	}

	@Override
	public String getPasswordHash() {
		return passwordHash;
	}

	@Override
	public void setPassword(final String password) {
		this.passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
	}

	@Override
	public Date getPasswordExpiryTimestamp() {
		return passwordExpiryTimestamp;
	}

	@Override
	public void setPasswordExpiryTimestamp(final Date passwordExpiryTimestamp) {
		this.passwordExpiryTimestamp = passwordExpiryTimestamp;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	@Override
	public boolean isShowRealName() {
		return showRealName;
	}

	@Override
	public void setShowRealName(final boolean showRealName) {
		this.showRealName = showRealName;
	}

	@Override
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	@Override
	public void setCreatedTimestamp(final Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	@Override
	public Date getVoteSalaryLastPaidTimestamp() {
		return voteSalaryLastPaidTimestamp;
	}

	@Override
	public void setVoteSalaryLastPaidTimestamp(final Date voteSalaryLastPaidTimestamp) {
		this.voteSalaryLastPaidTimestamp = voteSalaryLastPaidTimestamp;
	}

	@Override
	public Date getVoteSalaryDate() {
		return voteSalaryDate;
	}

	@Override
	public void setVoteSalaryDate(final Date voteSalaryDate) {
		this.voteSalaryDate = voteSalaryDate;
	}

	@Override
	public List<UserRole> getUserRoles() {
		return userRoles;
	}

	@Override
	public void setUserRoles(final List<UserRole> userRole) {
		this.userRoles = userRole;
	}
}
