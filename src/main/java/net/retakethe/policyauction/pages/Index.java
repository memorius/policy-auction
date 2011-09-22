package net.retakethe.policyauction.pages;

import java.util.Date;

import net.retakethe.policyauction.entities.User;

import org.apache.tapestry5.annotations.SessionState;

/**
 * Start page of application policy-auction.
 */
public class Index {
	@SessionState(create=false)
	private User currentUser;
	
	private boolean currentUserExists;

	public Date getCurrentTime() 
	{ 
		return new Date(); 
	}
	
	public String getUsername() {
		if (currentUserExists) {
			return currentUser.getUsername();
		} else {
			return "";
		}
	}
}
