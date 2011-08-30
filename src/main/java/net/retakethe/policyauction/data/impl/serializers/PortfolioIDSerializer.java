package net.retakethe.policyauction.data.impl.serializers;

import net.retakethe.policyauction.data.api.types.PortfolioID;
import net.retakethe.policyauction.data.impl.types.PortfolioIDImpl;

/**
 * @author Nick Clarke
 */
public class PortfolioIDSerializer extends AbstractStringSerializer<PortfolioID> {

    private static final PortfolioIDSerializer INSTANCE = new PortfolioIDSerializer();

    public static PortfolioIDSerializer get() {
        return INSTANCE;
    }

    /**
     * @see #get()
     */
    private PortfolioIDSerializer() {}

    @Override
    protected String toString(PortfolioID obj) {
        return obj.asString();
    }

    @Override
    protected PortfolioID fromString(String obj) {
        return new PortfolioIDImpl(obj);
    }
}
