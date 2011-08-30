package net.retakethe.policyauction.entities;

import java.io.Serializable;

import net.retakethe.policyauction.data.api.dao.PolicyDAO;

import org.apache.tapestry5.beaneditor.NonVisual;

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

    public String getShortName() {
        return policyDAO.getShortName();
    }
}
