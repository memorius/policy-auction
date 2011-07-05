package net.retakethe.policyauction.entities;

import net.retakethe.policyauction.data.api.PolicyID;

import org.apache.tapestry5.beaneditor.Validate;

/**
 * @author Nick Clarke
 */
public class Policy  {

    private final PolicyID _policyID;
    private String _description;
    private String _shortName;

    /**
     * @param policyID must not be null
     */
    public Policy(PolicyID policyID) {
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
    public Policy(PolicyID policyID, String shortName, String description) {
        _policyID = policyID;
        _shortName = shortName;
        _description = description;
    }

    public PolicyID getPolicyID() {
        return _policyID;
    }

    @Validate("required")
    public String getShortName() {
        return _shortName;
    }

    public void setShortName(String shortName) {
        _shortName = shortName;
    }

    @Validate("required")
    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }
}
