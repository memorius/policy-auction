package net.retakethe.policyauction.data.impl.dao;

import java.util.Date;

import net.retakethe.policyauction.data.api.dao.PolicyDAO;
import net.retakethe.policyauction.data.api.types.PolicyID;

/**
 * @author Nick Clarke
 */
public class PolicyDAOImpl implements PolicyDAO {
    private static final long serialVersionUID = 0L;

    private final PolicyID policyID;
    private String description;
    private String shortName;
    private Date lastEdited;

    public PolicyDAOImpl(PolicyID policyID) {
        if (policyID == null) {
            throw new IllegalArgumentException("policyID must not be null");
        }
        this.policyID = policyID;
    }

    public PolicyDAOImpl(PolicyID policyID, String shortName, String description, Date lastEdited) {
        this.policyID = policyID;
        this.shortName = shortName;
        this.description = description;
        this.lastEdited = lastEdited;
    }

    @Override
    public PolicyID getPolicyID() {
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

    @Override
    public Date getLastEdited() {
        return lastEdited;
    }
}
