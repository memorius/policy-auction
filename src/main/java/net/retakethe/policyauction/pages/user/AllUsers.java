package net.retakethe.policyauction.pages.user;

import java.util.List;

import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.entities.EntityFactory;
import net.retakethe.policyauction.entities.User;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;

public class AllUsers {

    @InjectPage
    private NewUser newUserPage;

    @Inject
    private DAOManager daoManager;

    public List<User> getAllUsers() {
        return EntityFactory.makeUsers(daoManager.getUserManager().getAllUsers());
    }

    public Object onActionFromAdd() {
        return newUserPage;
    }
}
