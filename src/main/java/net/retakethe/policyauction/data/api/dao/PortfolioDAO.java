package net.retakethe.policyauction.data.api.dao;

import java.io.Serializable;

import net.retakethe.policyauction.data.api.types.PortfolioID;

/**
 * A portfolio - one of a fixed set of categories of policies, e.g. "Education", "Health", "Tax" etc.
 *
 * @author Nick Clarke
 */
public interface PortfolioDAO extends Serializable {

    /**
     * @return non-null ID
     */
    PortfolioID getPortfolioID();

    /**
     * @return non-null name
     */
    String getName();

    /**
     * @return non-null description
     */
    String getDescription();
}
