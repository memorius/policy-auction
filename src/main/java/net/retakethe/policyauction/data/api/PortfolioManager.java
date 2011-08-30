package net.retakethe.policyauction.data.api;

import java.util.List;

import net.retakethe.policyauction.data.api.dao.PortfolioDAO;
import net.retakethe.policyauction.data.api.exceptions.NoSuchPortfolioException;
import net.retakethe.policyauction.data.api.types.PortfolioID;

/**
 * Manages "portfolios" - the fixed set of categories of policies, e.g. "Education", "Health", "Tax" etc.
 *
 * @author Nick Clarke
 */
public interface PortfolioManager {

    PortfolioID makePortfolioID(String asString);

    PortfolioDAO getPortfolio(PortfolioID portfolioID) throws NoSuchPortfolioException;

    List<PortfolioDAO> getAllPortfolios();
}
