package net.retakethe.policyauction.data.impl.types;

import java.util.UUID;

import net.retakethe.policyauction.data.api.types.PolicyID;

public final class PolicyIDImpl extends AbstractTimeUUIDImpl implements PolicyID {
    private static final long serialVersionUID = 0L;

    /**
     * Create with new TimeUUID - current time.
     */
    public PolicyIDImpl() {
        super();
    }

    /**
     * Create with String representation of a TimeUUID.
     */
    public PolicyIDImpl(String idString) {
        super(idString);
    }

    /**
     * Create with TimeUUID.
     */
    public PolicyIDImpl(UUID uuid) {
        super(uuid);
    }
}
