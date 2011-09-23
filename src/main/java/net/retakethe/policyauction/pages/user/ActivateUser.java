package net.retakethe.policyauction.pages.user;

import net.retakethe.policyauction.entities.User;
import net.retakethe.policyauction.pages.Index;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

public class ActivateUser {
	
	@Persist
	@Property
	private User user;
	
	@Property
	private String username;

	@Property
	private String firstName;

	@Property
	private String lastName;

	@Property
	private boolean showRealName;

	@Property
	private String password;

	@Property
	private String repeatPassword;
	
	@InjectPage
	private Index indexPage;
	
	public void setup() {
	}
	
	public Object onSuccess() {
		return indexPage;
	}

}
