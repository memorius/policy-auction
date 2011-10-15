package net.retakethe.policyauction.pages.session;

import net.retakethe.policyauction.entities.User;
import net.retakethe.policyauction.pages.Index;
import net.retakethe.policyauction.services.AuthenticationService;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * The Class NewSession. Page used for a user to logon to the system.
 */
public class NewSession {
	
	@SuppressWarnings("unused")
	@SessionState
	private User currentUser;
	
    @InjectPage
    private Index index;
    
    @Persist
    @Property
    private String userName;
    
    @Property
    private String password;
	
	@Inject
	private AuthenticationService authenticationService;

    @Component(id = "password")
    private PasswordField passwordField;

    @Component
    private Form form;

    Object onSuccess() {
    	User loggedInUser = authenticationService.login(userName, password);
        if (loggedInUser==null)
        {
            form.recordError(passwordField, "Invalid user name or password.");
            return null;
        } else {
        	currentUser = loggedInUser;
        }
        return index;
    }
}
