package net.retakethe.policyauction.pages.user;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.data.api.dao.UserDAO;
import net.retakethe.policyauction.data.api.types.UserRole;
import net.retakethe.policyauction.entities.EntityFactory;
import net.retakethe.policyauction.entities.User;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class EditUser {

	@Property
	@Persist
	private User user;
	
	@SuppressWarnings("unused")
	@Property
	private String repeatPassword;
	
	@Property
	private Object currentKey;
	
	@Persist
	private Set<UserRole> selection;
	
	private boolean isExisting;

	@Inject
	private DAOManager daoManager;

	@InjectPage
	private AllUsers allUsersPage;

	public void setup(final User user, final boolean isExisting) {
		this.user = user;
		this.isExisting = isExisting;
		this.selection = new HashSet<UserRole>();
	}

	public String getCreateOrUpdate() {
		return (isExisting ? "Update" : "Create");
	}

	public Object onSuccess()
	{
		UserDAO userDAO = EntityFactory.getUserDAO(user);
		if (!isExisting) {
			// Create the other fields behind the scene before we save.
			Date now = new Date();
			userDAO.setCreatedTimestamp(now);
			userDAO.setPasswordExpiryTimestamp(now);
			userDAO.setVoteSalaryDate(now);
			userDAO.setVoteSalaryLastPaidTimestamp(now);
		}
		daoManager.getUserManager().persist(userDAO);
		return allUsersPage;
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
}
