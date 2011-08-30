package net.retakethe.policyauction.data.impl.dao;

import java.util.Date;

import net.retakethe.policyauction.data.api.dao.PolicyDetailsDAO;
import net.retakethe.policyauction.data.api.dao.PolicyState;
import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.util.AssertArgument;

/**
 * @author Nick Clarke
 */
public class PolicyDetailsDAOImpl implements PolicyDetailsDAO {
    private static final long serialVersionUID = 0L;

    private final PolicyID policyID;
    private final UserID ownerUserID;

    private PolicyState policyState;
    private Date lastEdited;
    private Date stateChanged;

    private String description;
    private String shortName;
    private String rationaleDescription;
    private String costsToTaxpayersDescription;
    private String whoIsAffectedDescription;
    private String howAffectedDescription;
    private boolean isPartyOfficialPolicy;

    public PolicyDetailsDAOImpl(PolicyID policyID, UserID ownerUserID) {
        AssertArgument.notNull(policyID, "policyID");
        this.policyID = policyID;
        AssertArgument.notNull(ownerUserID, "ownerUserID");
        this.ownerUserID = ownerUserID;

        this.policyState = PolicyState.ACTIVE;
        Date now = new Date();
        this.stateChanged = now;
        this.lastEdited = now;

        this.shortName = "";
        this.description = "";
        this.rationaleDescription = "";
        this.costsToTaxpayersDescription = "";
        this.whoIsAffectedDescription = "";
        this.howAffectedDescription = "";
        this.isPartyOfficialPolicy = false;
    }

    public PolicyDetailsDAOImpl(
            PolicyID policyID,
            UserID ownerUserID,
            PolicyState policyState,
            Date stateChanged,
            Date lastEdited,
            String shortName,
            String description,
            String rationaleDescription,
            String costsToTaxpayersDescription,
            String whoIsAffectedDescription,
            String howAffectedDescription,
            boolean isPartyOfficialPolicy) {
        AssertArgument.notNull(policyID, "policyID");
        this.policyID = policyID;
        AssertArgument.notNull(ownerUserID, "ownerUserID");
        this.ownerUserID = ownerUserID;
        AssertArgument.notNull(policyState, "policyState");
        this.policyState = policyState;
        AssertArgument.notNull(stateChanged, "stateChanged");
        this.stateChanged = stateChanged;
        AssertArgument.notNull(lastEdited, "lastEdited");
        this.lastEdited = lastEdited;
        AssertArgument.notNull(shortName, "shortName");
        this.shortName = shortName;
        AssertArgument.notNull(description, "description");
        this.description = description;
        AssertArgument.notNull(rationaleDescription, "rationaleDescription");
        this.rationaleDescription = rationaleDescription;
        AssertArgument.notNull(costsToTaxpayersDescription, "costsToTaxpayersDescription");
        this.costsToTaxpayersDescription = costsToTaxpayersDescription;
        AssertArgument.notNull(whoIsAffectedDescription, "whoIsAffectedDescription");
        this.whoIsAffectedDescription = whoIsAffectedDescription;
        AssertArgument.notNull(howAffectedDescription, "howAffectedDescription");
        this.howAffectedDescription = howAffectedDescription;
        this.isPartyOfficialPolicy = isPartyOfficialPolicy;
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
        AssertArgument.notNull(shortName, "shortName");
        this.shortName = shortName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        AssertArgument.notNull(description, "description");
        this.description = description;
    }

    @Override
    public Date getLastEdited() {
        return lastEdited;
    }

    @Override
    public PolicyState getPolicyState() {
        return policyState;
    }

    public void setPolicyState(PolicyState newState) {
        AssertArgument.notNull(newState, "newState");
        if (newState != policyState) {
            policyState = newState;
            stateChanged = new Date();
        }
    }

    @Override
    public Date getStateChanged() {
        return stateChanged;
    }

    @Override
    public UserID getOwnerUserID() {
        return ownerUserID;
    }

    @Override
    public String getRationaleDescription() {
        return rationaleDescription;
    }

    @Override
    public void setRationaleDescription(String rationaleDescription) {
        AssertArgument.notNull(rationaleDescription, "rationaleDescription");
        this.rationaleDescription = rationaleDescription;
    }

    @Override
    public String getCostsToTaxpayersDescription() {
        return costsToTaxpayersDescription;
    }

    @Override
    public void setCostsToTaxpayersDescription(String costsToTaxpayersDescription) {
        AssertArgument.notNull(costsToTaxpayersDescription, "costsToTaxpayersDescription");
        this.costsToTaxpayersDescription = costsToTaxpayersDescription;
    }

    @Override
    public String getWhoIsAffectedDescription() {
        return whoIsAffectedDescription;
    }

    @Override
    public void setWhoIsAffectedDescription(String whoIsAffectedDescription) {
        AssertArgument.notNull(whoIsAffectedDescription, "whoIsAffectedDescription");
        this.whoIsAffectedDescription = whoIsAffectedDescription;
    }

    @Override
    public String getHowAffectedDescription() {
        return howAffectedDescription;
    }

    @Override
    public void setHowAffectedDescription(String howAffectedDescription) {
        AssertArgument.notNull(howAffectedDescription, "howAffectedDescription");
        this.howAffectedDescription = howAffectedDescription;
    }

    @Override
    public boolean isPartyOfficialPolicy() {
        return isPartyOfficialPolicy;
    }

    @Override
    public void setIsPartyOfficialPolicy(boolean isPartyOfficialPolicy) {
        this.isPartyOfficialPolicy = isPartyOfficialPolicy;
    }
}
