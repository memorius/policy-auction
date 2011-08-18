package net.retakethe.policyauction.entities;

import java.util.Date;

import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.api.types.UserRole;


public class User {
	
	private final UserID userID;
	
	private String _username;
	private String _email;
	
	private String _passwordHash;
	
	private String _firstName;
	private String _lastName;
	
	private boolean _showRealName;
	
	private long _createdTimestamp;
	
	private long _passwordExpiryTimestamp;
	
	private long _voteSalaryLastPaidTimestamp;
	
	private long _voteSalaryDate;
	
	private UserRole _role;
	
	public User(final UserID userID) {
		this.userID = userID;
		_createdTimestamp = new Date().getTime();
		_passwordExpiryTimestamp = new Date().getTime();
	}
	
	public User(final UserID userID, final String _username, final String _email, final String _passwordHash, final String _firstName, final String _lastName, final boolean _showRealName, final long _createdTimestamp, final long _passwordExpiryTimestamp, final long _voteSalaryLastPaidTimestamp, final long _voteSalaryDate, final String _role) {
		this.userID = userID;
		this._username = _username;
		this._email = _email;
		this._passwordHash = _passwordHash;
		this._firstName = _firstName;
		this._lastName = _lastName;
		this._showRealName = _showRealName;
		this._createdTimestamp = _createdTimestamp;
		this._passwordExpiryTimestamp = _passwordExpiryTimestamp;
		this._voteSalaryLastPaidTimestamp = _voteSalaryLastPaidTimestamp;
		this._voteSalaryDate = _voteSalaryDate;
		setRole(_role);
	}

	public String getUsername() {
		return _username;
	}

	public void setUsername(String _username) {
		this._username = _username;
	}

	public String getEmail() {
		return _email;
	}

	public void setEmail(String _email) {
		this._email = _email;
	}

	public String getPasswordHash() {
		return _passwordHash;
	}

	public void setPasswordHash(String _passwordHash) {
		this._passwordHash = _passwordHash;
	}

	public String getFirstName() {
		return _firstName;
	}

	public void setFirstName(String _firstName) {
		this._firstName = _firstName;
	}

	public String getLastName() {
		return _lastName;
	}

	public void setLastName(String _lastName) {
		this._lastName = _lastName;
	}

	public boolean isShowRealName() {
		return _showRealName;
	}

	public void setShowRealName(boolean _showRealName) {
		this._showRealName = _showRealName;
	}

	public long getCreatedTimestamp() {
		return _createdTimestamp;
	}

	public void setCreatedTimestamp(long _createdTimestamp) {
		this._createdTimestamp = _createdTimestamp;
	}

	public long getPasswordExpiryTimestamp() {
		return _passwordExpiryTimestamp;
	}

	public void setPasswordExpiryTimestamp(long _passwordExpiryTimestamp) {
		this._passwordExpiryTimestamp = _passwordExpiryTimestamp;
	}

	public long getVoteSalaryLastPaidTimestamp() {
		return _voteSalaryLastPaidTimestamp;
	}

	public void setVoteSalaryLastPaidTimestamp(long _voteSalaryLastPaidTimestamp) {
		this._voteSalaryLastPaidTimestamp = _voteSalaryLastPaidTimestamp;
	}

	public long getVoteSalaryDate() {
		return _voteSalaryDate;
	}

	public void setVoteSalaryDate(long _voteSalaryDate) {
		this._voteSalaryDate = _voteSalaryDate;
	}

	public UserRole getRole() {
		return _role;
	}

	public void setRole(UserRole role) {
		this._role = role;
	}
	
	public void setRole(String role) {
		for (UserRole userRole : UserRole.values()) {
			if (userRole.equals(role)) {
				this._role = userRole;
				break;
			}
		}
		if (this._role == null) {
			this._role = UserRole.USER;
		}
	}

	public UserID getUserID() {
		return userID;
	}
	
}
