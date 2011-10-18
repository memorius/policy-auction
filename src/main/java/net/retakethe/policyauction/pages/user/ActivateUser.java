package net.retakethe.policyauction.pages.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.retakethe.policyauction.business.api.BusinessManager;
import net.retakethe.policyauction.data.api.types.UserRole;
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
	private BusinessManager businessManager;
	
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
		
		user = businessManager.getUserManager().loadNewUser(email, code);
		if (user == null) {
			errorPage.setup("error", "Invalid email or activation code");
			return errorPage;
		}
		return null;
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

		currentUser = businessManager.getUserManager().activateUser(user);
		return indexPage;
	}

}
