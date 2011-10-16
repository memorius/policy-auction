package net.retakethe.policyauction.pages.user;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.data.api.dao.UserDAO;
import net.retakethe.policyauction.entities.EntityFactory;
import net.retakethe.policyauction.entities.User;
import net.retakethe.policyauction.pages.Index;
import net.retakethe.policyauction.services.EmailSender;
import net.retakethe.policyauction.services.EmailSender.EmailNotSentException;
import net.retakethe.policyauction.util.CollectionUtils;

import org.apache.commons.lang.RandomStringUtils;
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
	private DAOManager daoManager;
	
	@Inject
	private EmailSender emailSender;
	
	@Inject
    private HttpServletRequest _request;
	
	@InjectPage
	private Index indexPage;
	
	@Component
	private Form newUserForm;

	@BeginRender
	public void setup() {
		this.user = EntityFactory.makeUser(daoManager.getUserManager().createUser());
	}
	
	public Object onSuccess() {
		UserDAO userDAO = EntityFactory.getUserDAO(user);
		// Create the other fields behind the scene before we save.
		userDAO.setEmail(userEmail);
		userDAO.setActivationCode(RandomStringUtils.randomAlphanumeric(32));
		userDAO.setCreatedTimestamp(new Date());
		daoManager.getUserManager().persist(userDAO);
		// FIXME this needs to be in a business layer class, and more configurable.
		String body = "Hi, you're almost there, just click through on the following link and you're in - " + _request.getScheme() + "://" + _request.getServerName() + ":" + _request.getServerPort() + "/" + _request.getContextPath() + "/user/activateuser?email=" + userEmail + "&code=" + userDAO.getActivationCode()+ " \r\n";
		try {
			emailSender.sendMail("newuser@idvoteforthat.co.nz", CollectionUtils.list(userEmail), "New user activation", body);
		} catch (EmailNotSentException e) {
			throw new RuntimeException("Email unable to be sent", e);
		}
		return indexPage;
	}
}
