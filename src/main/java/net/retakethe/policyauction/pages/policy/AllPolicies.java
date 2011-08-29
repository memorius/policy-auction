package net.retakethe.policyauction.pages.policy;

import java.util.List;

import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.entities.EntityFactory;
import net.retakethe.policyauction.entities.PolicyDetails;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;


public class AllPolicies {

    @InjectPage
    private EditPolicy editPolicyPage;

    @Inject
    private DAOManager daoManager;

    public List<PolicyDetails> getAllPolicyDetails() {
        return EntityFactory.makePolicyDetailsFromDAO(daoManager.getPolicyManager().getAllPolicyDetails());
    }

    public Object onActionFromAdd() {
        editPolicyPage.setup(EntityFactory.makePolicyDetailsFromDAO(daoManager.getPolicyManager().createPolicy()), false);
        return editPolicyPage;
    }
}
