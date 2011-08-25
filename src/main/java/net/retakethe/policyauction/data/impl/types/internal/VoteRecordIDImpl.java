package net.retakethe.policyauction.data.impl.types.internal;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.types.AbstractTimeUUIDImpl;

/**
 * @author Nick Clarke
 */
public final class VoteRecordIDImpl extends AbstractTimeUUIDImpl implements VoteRecordID {
    private static final long serialVersionUID = 0L;

    /**
     * Create with new TimeUUID - current time.
     */
    public VoteRecordIDImpl() {
        super();
    }

    /**
     * Create with String representation of a TimeUUID.
     */
    public VoteRecordIDImpl(String idString) {
        super(idString);
    }

    /**
     * Create with TimeUUID.
     */
    public VoteRecordIDImpl(UUID uuid) {
        super(uuid);
    }
}
