package net.retakethe.policyauction.pages.policy;

import java.util.List;

import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.impl.types.UserIDImpl;
import net.retakethe.policyauction.entities.EntityFactory;
import net.retakethe.policyauction.entities.Policy;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;


public class AllPolicies {

    @InjectPage
    private EditPolicy editPolicyPage;

    @Inject
    private DAOManager daoManager;

    public List<Policy> getAllPolicies() {
        return EntityFactory.makePolicyFromDAO(daoManager.getPolicyManager().getAllPolicies());
    }

    public Object onActionFromAdd() {
        UserID userID = getLoggedInUserID();
        editPolicyPage.setup(EntityFactory.makePolicyDetailsFromDAO(
                daoManager.getPolicyManager().createPolicy(userID)), false);
        return editPolicyPage;
    }

    private UserID getLoggedInUserID() {
        // FIXME: get from session once login is implemented
        return new UserIDImpl();
    }
}
