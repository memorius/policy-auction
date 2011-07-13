package net.retakethe.policyauction.pages.policy;

import java.util.List;

import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.entities.Policy;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;


public class AllPolicies {

    @InjectPage
    private EditPolicy editPolicyPage;

    @Inject
    private DAOManager daoManager;

    public List<Policy> getAllPolicies() {
        return daoManager.getPolicyManager().getAllPolicies();
    }

    public Object onActionFromAdd() {
        editPolicyPage.setup(daoManager.getPolicyManager().createPolicy(), false);
        return editPolicyPage;
    }
}