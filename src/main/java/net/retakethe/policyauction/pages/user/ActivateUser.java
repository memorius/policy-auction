package net.retakethe.policyauction.pages.user;

import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.data.api.UserManager;
import net.retakethe.policyauction.data.api.UserManager.NoSuchUserException;
import net.retakethe.policyauction.data.api.dao.UserPendingDAO;
import net.retakethe.policyauction.entities.EntityFactory;
import net.retakethe.policyauction.entities.User;
import net.retakethe.policyauction.pages.Index;
import net.retakethe.policyauction.pages.Problem;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.mindrot.jbcrypt.BCrypt;

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
	
	@Component
	private Form activationForm;
	
	@InjectPage
	private Index indexPage;
	
	@InjectPage
	private Problem errorPage;
	
	@Inject
	private DAOManager daoManager;

	public Object onActivate(@RequestParameter("code") String code, @RequestParameter("email") String email) {
		// Pre-validation of activation codes
		if (StringUtils.isBlank(code) || StringUtils.isBlank(email)) {
			errorPage.setup("invalid", "Parts of the required parameters are missing.");
			return errorPage;
		}
		
		UserManager userManager = daoManager.getUserManager();
		UserPendingDAO userPendingDAO;
		try {
			userPendingDAO = userManager.getUserPending(email);
		} catch (NoSuchUserException e) {
			return generalActivationError();
		}
		if (userPendingDAO.getActivationCode().equalsIgnoreCase(code)) {
			return null;
		}
		return generalActivationError();
	}

	private Object generalActivationError() {
		errorPage.setup("error", "Invalid email address or code.");
		return errorPage;
	}
	
	public Object onValidate() {
		if (!StringUtils.equals(password, repeatPassword)) {
			activationForm.recordError("Passwords do not match.");
			return null;
		}
		return null;
	}
	
	public Object onSuccess() {
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setShowRealName(showRealName);
		user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
		
		daoManager.getUserManager().update(EntityFactory.getUserDAO(user));
		return indexPage;
	}

}
