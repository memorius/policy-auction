package net.retakethe.policyauction.data.impl.dao;

import net.retakethe.policyauction.data.api.dao.UsernameDAO;
import net.retakethe.policyauction.data.api.types.UserID;

public class UsernameDAOImpl implements UsernameDAO {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8815363670500339873L;

	private final UserID userID;
	
	private String username;
	
	public UsernameDAOImpl(final UserID userID) {
		if (userID == null ){
			throw new IllegalArgumentException("userID must not be null");
		}
		this.userID = userID;	
	}
	
	public UsernameDAOImpl(final UserID userID, final String username) {
		if (userID == null ){
			throw new IllegalArgumentException("userID must not be null");
		}
		this.userID = userID;
		this.username = username;
	}

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

}
