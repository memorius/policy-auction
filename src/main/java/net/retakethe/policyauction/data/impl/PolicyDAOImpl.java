package net.retakethe.policyauction.data.impl;

import net.retakethe.policyauction.data.api.PolicyDAO;

/**
 * @author Nick Clarke
 */
public class PolicyDAOImpl implements PolicyDAO {
    private static final long serialVersionUID = 0L;

    private final HectorPolicyIDImpl policyID;
    private String description;
    private String shortName;

    public PolicyDAOImpl(HectorPolicyIDImpl policyID) {
        if (policyID == null) {
            throw new IllegalArgumentException("policyID must not be null");
        }
        this.policyID = policyID;
    }

    public PolicyDAOImpl(HectorPolicyIDImpl policyID, String shortName, String description) {
        this.policyID = policyID;
        this.shortName = shortName;
        this.description = description;
    }

    @Override
    public HectorPolicyIDImpl getPolicyID() {
        return policyID;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    @Override
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
