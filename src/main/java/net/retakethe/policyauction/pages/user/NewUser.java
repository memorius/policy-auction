package net.retakethe.policyauction.pages.user;

import javax.servlet.http.HttpServletRequest;

import net.retakethe.policyauction.business.api.BusinessManager;
import net.retakethe.policyauction.entities.User;
import net.retakethe.policyauction.pages.Index;

import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;

public class NewUser {

	@Persist
	@Property
	private User user;
	
    @InjectComponent
    private TextField emailAddress;
    
    @Property
    private String userEmail;
	
	@Inject
	private BusinessManager businessManager;
	
	@Inject
    private HttpServletRequest _request;
	
	@InjectPage
	private Index indexPage;
	
	@Component
	private Form newUserForm;

	@BeginRender
	public void setup() {
		this.user = businessManager.getUserManager().createUser();
	}
	
	public Object onSuccess() {
		
		// Create the other fields behind the scene before we save.
		user.setEmail(userEmail);

		String url = _request.getScheme() + "://" + _request.getServerName() + ":" + _request.getServerPort() + "/" + _request.getContextPath() + "/user/activateuser";
		user = businessManager.getUserManager().provisionUser(user, url);
		return indexPage;
	}
}
