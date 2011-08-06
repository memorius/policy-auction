package net.retakethe.policyauction.data.api;

import java.io.Serializable;
import java.util.Date;

public interface PolicyDAO extends Serializable {

    PolicyID getPolicyID();

    String getShortName();

    void setShortName(String shortName);

    String getDescription();

    void setDescription(String description);

    Date getLastEdited();
}
