package net.retakethe.policyauction.pages.policy;

import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.entities.EntityFactory;
import net.retakethe.policyauction.entities.Policy;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;


public class EditPolicy {

    @Property
    @Persist
    private Policy policy;

    private boolean isExisting;

    @Inject
    private DAOManager daoManager;

    @InjectPage
    private AllPolicies allPoliciesPage;

    public void setup(Policy policy, boolean isExisting) {
        this.policy = policy;
        this.isExisting = isExisting;
    }

    public String getCreateOrUpdate() {
        return (isExisting ? "Update" : "Create");
    }

    public Object onSuccess()
    {
        daoManager.getPolicyManager().save(EntityFactory.getPolicyDAO(policy));
        return allPoliciesPage;
    }
}
