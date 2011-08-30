package net.retakethe.policyauction.data.api.dao;

import java.io.Serializable;
import java.util.Date;

import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.data.api.types.UserID;

public interface PolicyDetailsDAO extends Serializable {

    /**
     * @return non-null ID
     */
    PolicyID getPolicyID();

    PolicyState getPolicyState();

    Date getStateChanged();

    Date getLastEdited();

    /**
     * Get the user that created this policy.
     *
     * @return non-null userID
     */
    UserID getOwnerUserID();

    /* TODO: add when portfolios are implemented
    PortfolioID getPortfolioID();
    */

    /*
    long getTotalVotes();
    */

    /* TODO: do we want this?
    PartyID getPartyID();
    */
    
    /* TODO: do we want this?
    PolicyID getReplacedByPolicyID();
     */


    String getShortName();

    void setShortName(String shortName);


    String getDescription();

    void setDescription(String description);


    String getRationaleDescription();

    void setRationaleDescription(String rationaleDescription);


    String getCostsToTaxpayersDescription();
    
    void setCostsToTaxpayersDescription(String costsToTaxpayersDescription);


    String getWhoIsAffectedDescription();

    void setWhoIsAffectedDescription(String whoIsAffectedDescription);


    String getHowAffectedDescription();

    void setHowAffectedDescription(String howAffectedDescription);


    boolean isPartyOfficialPolicy();

    void setIsPartyOfficialPolicy(boolean isPartyOfficialPolicy);
}
