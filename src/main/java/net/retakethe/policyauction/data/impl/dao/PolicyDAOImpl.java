package net.retakethe.policyauction.data.impl.dao;

import net.retakethe.policyauction.data.api.dao.PolicyDAO;
import net.retakethe.policyauction.data.api.types.PolicyID;

/**
 * @author Nick Clarke
 */
public class PolicyDAOImpl implements PolicyDAO {
    private static final long serialVersionUID = 0L;

    private final PolicyID policyID;
    private String shortName;

    public PolicyDAOImpl(PolicyID policyID, String shortName) {
        if (policyID == null) {
            throw new IllegalArgumentException("policyID must not be null");
        }
        if (shortName == null) {
            throw new IllegalArgumentException("shortName must not be null");
        }
        this.policyID = policyID;
        this.shortName = shortName;
    }

    @Override
    public PolicyID getPolicyID() {
        return policyID;
    }

    @Override
    public String getShortName() {
        return shortName;
    }
}
