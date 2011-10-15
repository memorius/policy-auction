package net.retakethe.policyauction.components;

import net.retakethe.policyauction.services.AuthenticationService;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * The Class Logon, a tapestry component to provide a logon form (as well as a link to register a new user).
 */
public class Logon {

	/** The authentication service. */
	@Inject
	private AuthenticationService authenticationService;

	/** The login. */
	@Property
	@Persist
	private String username;

	/** The password. */
	@Property
	private String password;

	/** The success page. */
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String successPage;

	@Component
	private Form form;
	
	@Component(id="password")
	private PasswordField passwordField;
	
	/**
	 * On success.
	 *
	 * @return the object
	 */
	public Object onSuccess() {
		if (authenticationService.login(username, password) != null) {
			return successPage;
		}
		form.recordError(passwordField, "Invalid user name or password.");
		return null;
	}
}
