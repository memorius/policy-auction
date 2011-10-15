package net.retakethe.policyauction.services;

import net.retakethe.policyauction.entities.User;

public interface AuthenticationService {
	
	User login(String username, String password);

}
