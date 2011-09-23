package net.retakethe.policyauction.pages.user;

import java.util.Date;

import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.data.api.dao.UserDAO;
import net.retakethe.policyauction.entities.EntityFactory;
import net.retakethe.policyauction.entities.User;
import net.retakethe.policyauction.pages.Index;

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
	private DAOManager daoManager;
	
	@InjectPage
	private Index indexPage;
	
	@Component
	private Form newUserForm;

	public void setup(User user) {
		this.user = user;
	}
	
	public Object onSuccess() {
		UserDAO userDAO = EntityFactory.getUserDAO(user);
		// Create the other fields behind the scene before we save.
		userDAO.setEmail(userEmail);
		userDAO.setActivationCode("this is a secret ok guys?");
		userDAO.setCreatedTimestamp(new Date());
		daoManager.getUserManager().persist(userDAO);
		return indexPage;
	}
}
