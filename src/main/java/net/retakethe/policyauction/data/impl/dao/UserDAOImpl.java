package net.retakethe.policyauction.data.impl.dao;

import java.util.Date;

import net.retakethe.policyauction.data.api.dao.UserDAO;
import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.api.types.UserRole;

public class UserDAOImpl implements UserDAO {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1186674628028659343L;
	
	private final UserID userID;
	
	private String username;
	private String passwordHash;
	private String email;
	
	private String firstName;
	private String lastName;
	
	private Boolean showRealName;

	private Date createdTimestamp;
	private Date voteSalaryLastPaidTimestamp;
	private Date voteSalaryDate;
	
	private UserRole userRole;

	public UserDAOImpl(UserID userID) {
		if (userID == null ){
			throw new IllegalArgumentException("userID must not be null");
		}
		this.userID = userID;
	}
	
	public UserDAOImpl(final UserID userID, final String username, final String passwordHash, final String email, final String firstName, final String lastName, final Boolean showRealName, final Date createdTimestamp, final Date voteSalaryLastPaidTimestamp, final Date voteSalaryDate, final UserRole userRole) {
		this.userID = userID;
		this.username = username;
		this.passwordHash = passwordHash;
		this.email = email;
		
		this.firstName = firstName;
		this.lastName = lastName;
		
		this.showRealName = showRealName;
		
		this.createdTimestamp = createdTimestamp;
		this.voteSalaryLastPaidTimestamp = voteSalaryLastPaidTimestamp;
		this.voteSalaryDate = voteSalaryDate;
		
		this.userRole = userRole;
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
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getPasswordHash() {
		return passwordHash;
	}

	@Override
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public Boolean isShowRealName() {
		return showRealName;
	}

	@Override
	public void setShowRealName(Boolean showRealName) {
		this.showRealName = showRealName;
	}

	@Override
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	@Override
	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	@Override
	public Date getVoteSalaryLastPaidTimestamp() {
		return voteSalaryLastPaidTimestamp;
	}

	@Override
	public void setVoteSalaryLastPaidTimestamp(Date voteSalaryLastPaidTimestamp) {
		this.voteSalaryLastPaidTimestamp = voteSalaryLastPaidTimestamp;
	}

	@Override
	public Date getVoteSalaryDate() {
		return voteSalaryDate;
	}

	@Override
	public void setVoteSalaryDate(Date voteSalaryDate) {
		this.voteSalaryDate = voteSalaryDate;
	}

	@Override
	public UserRole getUserRole() {
		return userRole;
	}

	@Override
	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

}
