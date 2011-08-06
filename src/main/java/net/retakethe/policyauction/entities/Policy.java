package net.retakethe.policyauction.entities;

import java.io.Serializable;

import net.retakethe.policyauction.data.api.PolicyDAO;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;

/**
 * @author Nick Clarke
 */
public class Policy implements Serializable {
    private static final long serialVersionUID = 0L;

    private final PolicyDAO policyDAO;

    public Policy(PolicyDAO policyDAO) {
        this.policyDAO = policyDAO;
    }

    @NonVisual
    protected PolicyDAO getPolicyDAO() {
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
}
