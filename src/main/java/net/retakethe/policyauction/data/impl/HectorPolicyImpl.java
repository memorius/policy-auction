package net.retakethe.policyauction.data.impl;

import net.retakethe.policyauction.data.api.Policy;
import net.retakethe.policyauction.data.impl.HectorPolicyManagerImpl.HectorPolicyIDImpl;

/**
 * @author Nick Clarke
 *
 */
public class HectorPolicyImpl implements Policy {

    private final HectorPolicyIDImpl _policyID;
    private String _description;
    private String _shortName;

    /**
     * @param policyID must not be null
     */
    public HectorPolicyImpl(HectorPolicyIDImpl policyID) {
        if (policyID == null) {
            throw new IllegalArgumentException("policyID must not be null");
        }
        _policyID = policyID;
    }

    /**
     * @param policyID must not be null
     * @param shortName can be null
     * @param description can be null
     */
    public HectorPolicyImpl(HectorPolicyIDImpl policyID, String shortName, String description) {
        _policyID = policyID;
        _shortName = shortName;
        _description = description;
    }

    @Override
    public HectorPolicyIDImpl getPolicyID() {
        return _policyID;
    }

    @Override
    public String getShortName() {
        return _shortName;
    }

    @Override
    public void setShortName(String shortName) {
        _shortName = shortName;
    }

    @Override
    public String getDescription() {
        return _description;
    }

    @Override
    public void setDescription(String description) {
        _description = description;
    }
}
