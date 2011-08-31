package net.retakethe.policyauction.pages.session;

import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.pages.Index;
import net.retakethe.policyauction.services.Security;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.ioc.annotations.Inject;

public class NewSession {
	
    @InjectPage
    private Index index;
    
    @Persist
    @Property
    private String userName;
    
    @Property
    private String password;

	@Inject
	private DAOManager daoManager;

    @Component(id = "password")
    private PasswordField passwordField;

    @Component
    private Form form;

    Object onSuccess()
    {
        if (Security.AuthenticateUser(daoManager, userName, password)==null)
        {
            form.recordError(passwordField, "Invalid user name or password.");
            return null;
        }

        return index;
    }
}
