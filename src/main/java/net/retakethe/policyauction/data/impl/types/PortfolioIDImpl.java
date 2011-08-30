package net.retakethe.policyauction.data.impl.types;

import net.retakethe.policyauction.data.api.types.PortfolioID;

/**
 * @author Nick Clarke
 */
public final class PortfolioIDImpl extends AbstractStringIDImpl implements PortfolioID {
    private static final long serialVersionUID = 0L;

    /**
     * Create with String representation of an ID.
     */
    public PortfolioIDImpl(String id) {
        super(id);
    }
}
