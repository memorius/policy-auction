package net.retakethe.policyauction.data.api.dao;

import java.io.Serializable;
import java.util.Date;

import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.api.types.UserRole;

public interface UserDAO extends Serializable {
	
	UserID getUserID();
	
	String getUsername();
	void setUsername(String username);
	String getEmail();
	void setEmail(String email);
	String getPasswordHash();
	void setPasswordHash(String passwordHash);
	Date getPasswordExpiryTimestamp();
	void setPasswordExpiryTimestamp(Date passwordExpiryTimestamp);
	
	String getFirstName();
	void setFirstName(String firstName);
	String getLastName();
	void setLastName(String lastName);
	
	Boolean isShowRealName();
	void setShowRealName(Boolean showRealName);
	
	Date getCreatedTimestamp();
	void setCreatedTimestamp(Date createdTimestamp);
	Date getVoteSalaryLastPaidTimestamp();
	void setVoteSalaryLastPaidTimestamp(Date voteSalaryLastPaidTimestamp);
	
	Date getVoteSalaryDate();
	void setVoteSalaryDate(Date voteSalaryDate);
	
	UserRole getUserRole();
	void setUserRole(UserRole userRole);
	
}
