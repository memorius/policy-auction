package net.retakethe.policyauction.entities;

import java.io.Serializable;
import java.util.Date;

import net.retakethe.policyauction.data.api.dao.PolicyDetailsDAO;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;

/**
 * @author Nick Clarke
 */
public class PolicyDetails implements Serializable {
    private static final long serialVersionUID = 0L;

    private final PolicyDetailsDAO policyDAO;

    public PolicyDetails(PolicyDetailsDAO policyDAO) {
        this.policyDAO = policyDAO;
    }

    @NonVisual
    protected PolicyDetailsDAO getPolicyDetailsDAO() {
        return policyDAO;
    }

    @Validate("required")
    public String getShortName() {
        return policyDAO.getShortName();
    }

    public void setShortName(String shortName) {
        policyDAO.setShortName(shortName);
    }

    @Validate("required")
    public String getDescription() {
        return policyDAO.getDescription();
    }

    public void setDescription(String description) {
        policyDAO.setDescription(description);
    }

    public Date getLastEdited() {
        return policyDAO.getLastEdited();
    }
}
