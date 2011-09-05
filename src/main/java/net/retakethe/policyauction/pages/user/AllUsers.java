package net.retakethe.policyauction.pages.user;

import java.util.List;

import net.retakethe.policyauction.annotations.PublicPage;
import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.entities.EntityFactory;
import net.retakethe.policyauction.entities.User;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;

@PublicPage
public class AllUsers {

    @InjectPage
    private EditUser editUserPage;

    @Inject
    private DAOManager daoManager;

    public List<User> getAllUsers() {
        return EntityFactory.makeUsers(daoManager.getUserManager().getAllUsers());
    }

    public Object onActionFromAdd() {
    	editUserPage.setup(EntityFactory.makeUser(daoManager.getUserManager().createUser()), false);
        return editUserPage;
    }
}
