package net.retakethe.policyauction.data.api.dao;

import java.io.Serializable;
import java.util.Date;

import net.retakethe.policyauction.data.api.types.PolicyID;

public interface PolicyDAO extends Serializable {

    PolicyID getPolicyID();

    String getShortName();

    void setShortName(String shortName);

    String getDescription();

    void setDescription(String description);

    Date getLastEdited();
}
