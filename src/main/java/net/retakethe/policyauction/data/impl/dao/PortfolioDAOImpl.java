package net.retakethe.policyauction.data.impl.dao;

import net.retakethe.policyauction.data.api.dao.PortfolioDAO;
import net.retakethe.policyauction.data.api.types.PortfolioID;
import net.retakethe.policyauction.util.AssertArgument;

/**
 * @author Nick Clarke
 */
public class PortfolioDAOImpl implements PortfolioDAO {
    private static final long serialVersionUID = 0L;

    private final PortfolioID portfolioID;
    private final String name;
    private final String description;

    public PortfolioDAOImpl(PortfolioID portfolioID, String name, String description) {
        AssertArgument.notNull(portfolioID, "portfolioID");
        this.portfolioID = portfolioID;
        AssertArgument.notNull(name, "name");
        this.name = name;
        AssertArgument.notNull(description, "description");
        this.description = description;
    }

    @Override
    public PortfolioID getPortfolioID() {
        return portfolioID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
