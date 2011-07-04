package net.retakethe.policyauction.data.api;


public interface Policy {

    PolicyID getPolicyID();

    String getShortName();

    void setShortName(String shortName);

    String getDescription();

    void setDescription(String description);
}
