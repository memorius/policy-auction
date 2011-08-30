package net.retakethe.policyauction.data.api.exceptions;

import net.retakethe.policyauction.data.api.types.PortfolioID;

public class NoSuchPortfolioException extends Exception {
    private static final long serialVersionUID = 0L;

    public NoSuchPortfolioException(PortfolioID portfolioID) {
        super("No portfolio found with id '" + portfolioID.asString() + "'");
    }
}