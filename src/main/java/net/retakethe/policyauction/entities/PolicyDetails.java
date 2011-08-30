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

    public String getShortName() {
        return policyDAO.getShortName();
    }

    @Validate("required")
    public void setShortName(String shortName) {
        policyDAO.setShortName(shortName);
    }

    public String getDescription() {
        return policyDAO.getDescription();
    }

    @Validate("required")
    public void setDescription(String description) {
        policyDAO.setDescription(description);
    }

    public String getRationaleDescription() {
        return policyDAO.getRationaleDescription();
    }

    @Validate("required")
    public void setRationaleDescription(String rationaleDescription) {
        policyDAO.setRationaleDescription(rationaleDescription);
    }

    public String getCostsToTaxpayersDescription() {
        return policyDAO.getCostsToTaxpayersDescription();
    }

    @Validate("required")
    public void setCostsToTaxpayersDescription(String costsToTaxpayersDescription) {
        policyDAO.setCostsToTaxpayersDescription(costsToTaxpayersDescription);
    }

    public String getWhoIsAffectedDescription() {
        return policyDAO.getWhoIsAffectedDescription();
    }

    @Validate("required")
    public void setWhoIsAffectedDescription(String whoIsAffectedDescription) {
        policyDAO.setWhoIsAffectedDescription(whoIsAffectedDescription);
    }

    public String getHowAffectedDescription() {
        return policyDAO.getHowAffectedDescription();
    }

    @Validate("required")
    public void setHowAffectedDescription(String howAffectedDescription) {
        policyDAO.setHowAffectedDescription(howAffectedDescription);
    }

    public boolean isPartyOfficialPolicy() {
        return policyDAO.isPartyOfficialPolicy();
    }

    @Validate("required")
    public void setIsPartyOfficialPolicy(boolean isPartyOfficialPolicy) {
        policyDAO.setIsPartyOfficialPolicy(isPartyOfficialPolicy);
    }

    public Date getLastEdited() {
        return policyDAO.getLastEdited();
    }
}
