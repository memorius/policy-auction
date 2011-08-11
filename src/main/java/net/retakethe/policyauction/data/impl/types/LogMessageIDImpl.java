package net.retakethe.policyauction.data.impl.types;

import java.util.UUID;

import net.retakethe.policyauction.data.api.types.LogMessageID;

public final class LogMessageIDImpl extends AbstractTimeUUIDImpl implements LogMessageID {
    private static final long serialVersionUID = 0L;

    /**
     * Create with new TimeUUID - current time.
     */
    public LogMessageIDImpl() {
        super();
    }

    /**
     * Create with String representation of a TimeUUID.
     */
    public LogMessageIDImpl(String idString) {
        super(idString);
    }

    /**
     * Create with TimeUUID.
     */
    public LogMessageIDImpl(UUID uuid) {
        super(uuid);
    }
}
