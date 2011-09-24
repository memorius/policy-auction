package net.retakethe.policyauction.data.api.dao;

import net.retakethe.policyauction.data.api.types.UserID;

public interface UserPendingDAO {
	
	String getEmail();
	void setEmail(String email);
	
	UserID getUserID();
	void setUserID(UserID userID);
	
	String getActivationCode();
	void setActivationCode(String activationCode);

}
