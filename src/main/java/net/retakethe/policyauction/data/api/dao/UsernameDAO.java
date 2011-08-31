package net.retakethe.policyauction.data.api.dao;

import java.io.Serializable;

import net.retakethe.policyauction.data.api.types.UserID;

public interface UsernameDAO extends Serializable { 

	UserID getUserID();
	
	String getUsername();
	void setUsername(String username);

}
