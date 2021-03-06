package net.retakethe.policyauction.data.api.dao;

import java.io.Serializable;

import net.retakethe.policyauction.data.api.types.PolicyID;

/**
 * Read-only view of minimal information on a policy, for list/ranking views.
 *
 * @author Nick Clarke
 */
public interface PolicyDAO extends Serializable {

    /**
     * @return non-null ID
     */
    PolicyID getPolicyID();

    /**
     * @return non-null name
     */
    String getShortName();

    // TODO: current vote count?
}
