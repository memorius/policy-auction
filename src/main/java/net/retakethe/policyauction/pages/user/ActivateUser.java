package net.retakethe.policyauction.pages.user;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.data.api.UserManager;
import net.retakethe.policyauction.data.api.UserManager.NoSuchUserException;
import net.retakethe.policyauction.data.api.dao.UserPendingDAO;
import net.retakethe.policyauction.data.api.types.UserRole;
import net.retakethe.policyauction.entities.EntityFactory;
import net.retakethe.policyauction.entities.User;
import net.retakethe.policyauction.pages.Index;
import net.retakethe.policyauction.pages.Problem;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.mindrot.jbcrypt.BCrypt;

public class ActivateUser {
	
	@SessionState
	private User currentUser;

	@ActivationRequestParameter
	@Property
	private String email;
	
	@ActivationRequestParameter
	@Property
	private String code;
	
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
	
	@Property
	private Object currentKey;
	
	@Persist
	private Set<UserRole> selection;
	
	@Component
	private Form activateUserForm;
	
	@InjectPage
	private Index indexPage;
	
	@InjectPage
	private Problem errorPage;
	
	@Inject
	private DAOManager daoManager;
	
	public void setup() {
		if (selection==null) {
			 selection = new HashSet<UserRole>();
		}
	}

	public Object onActivate() {
		setup();
		
		// Pre-validation of activation codes
		if (StringUtils.isBlank(code) || StringUtils.isBlank(email)) {
			errorPage.setup("invalid", "Parts of the required parameters are missing.");
			return errorPage;
		}
		
		UserManager userManager = daoManager.getUserManager();
		UserPendingDAO userPendingDAO;
		try {
			userPendingDAO = userManager.getUserPending(email);
			if (userPendingDAO.getActivationCode().equalsIgnoreCase(code)) {
				user = EntityFactory.makeUser(userManager.getUser(userPendingDAO.getUserID()));
				return null;
			}
		} catch (NoSuchUserException e) {
			return generalActivationError();
		}
		return generalActivationError();
	}
	
	public Map<String,UserRole> getMyMap() {
	    HashMap<String, UserRole> hashMap = new HashMap<String, UserRole>();
	    for (UserRole role : UserRole.values()) {
	    	hashMap.put(role.toString(), role);
	    }
		return hashMap;
	}

	public boolean getCurrentValue() {
	     return this.selection.contains(this.currentKey);
	}

	public void setCurrentValue(final boolean currentValue) {
	    final UserRole mapValue = this.getMapValue();

	    if (currentValue) {
	        this.selection.add(mapValue);
	    } else {
	        this.selection.remove(mapValue);
	    }
	}


	public UserRole getMapValue() {
	    return this.getMyMap().get(this.currentKey);
	}

	private Object generalActivationError() {
		errorPage.setup("error", "Invalid email address or code.");
		return errorPage;
	}
	
	@OnEvent(value=EventConstants.VALIDATE, component="activateUserForm")
	public Object onValidate() {
		if (!StringUtils.equals(password, repeatPassword)) {
			activateUserForm.recordError("Passwords do not match.");
			return null;
		}
		return null;
	}
	
	@OnEvent(value=EventConstants.SUCCESS, component="activateUserForm")
	public Object saveAndPersistActivatedUser() {
		user.setUsername(username);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setShowRealName(showRealName);

		user.setPasswordExpiryTimestamp(new Date());
		user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
		
		
		// FIXME I'm expecting that a service-layer call will set these values, and should not be set here in future.
		user.setVoteSalaryDate(new Date());
		user.setVoteSalaryLastPaidTimestamp(new Date());
		
		daoManager.getUserManager().update(EntityFactory.getUserDAO(user));

		currentUser = user;
		return indexPage;
	}

}
