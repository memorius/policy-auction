package net.retakethe.policyauction.data.impl.types;

import java.util.UUID;

import net.retakethe.policyauction.data.api.types.UserID;

public final class UserIDImpl extends AbstractTimeUUIDImpl implements UserID {
    private static final long serialVersionUID = 0L;

    /**
     * Create with new TimeUUID - current time.
     */
    public UserIDImpl() {
        super();
    }

    /**
     * Create with String representation of a TimeUUID.
     */
    public UserIDImpl(String idString) {
        super(idString);
    }

    /**
     * Create with TimeUUID.
     */
    public UserIDImpl(UUID uuid) {
        super(uuid);
    }
}
