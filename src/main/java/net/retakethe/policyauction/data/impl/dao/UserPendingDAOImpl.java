package net.retakethe.policyauction.data.impl.dao;

import net.retakethe.policyauction.data.api.dao.UserPendingDAO;
import net.retakethe.policyauction.data.api.types.UserID;

public class UserPendingDAOImpl implements UserPendingDAO {

	private String email;
	
	private UserID userID;
	
	private String activationCode;
	
	public UserPendingDAOImpl(final String email, final UserID userID, final String activationCode) {
		this.email = email;
		this.userID = userID;
		this.activationCode = activationCode;
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
	public UserID getUserID() {
		return userID;
	}

	@Override
	public void setUserID(UserID userID) {
		this.userID = userID;
	}

	@Override
	public String getActivationCode() {
		return activationCode;
	}

	@Override
	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

}
