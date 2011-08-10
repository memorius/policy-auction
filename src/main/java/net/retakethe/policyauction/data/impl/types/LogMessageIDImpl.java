package net.retakethe.policyauction.data.impl.types;

import java.util.UUID;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import net.retakethe.policyauction.data.api.types.LogMessageID;

public final class LogMessageIDImpl implements LogMessageID {

    private final UUID _uuid;

    /**
     * Create with new TimeUUID - current time.
     */
    public LogMessageIDImpl() {
        _uuid = TimeUUIDUtils.getUniqueTimeUUIDinMillis();
    }

    public LogMessageIDImpl(String idString) {
        if (idString == null) {
            throw new IllegalArgumentException("idString must not be null");
        }
        _uuid = UUID.fromString(idString);
    }

    public LogMessageIDImpl(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("uuid must not be null");
        }
        _uuid = uuid;
    }

    @Override
    public String asString() {
        return _uuid.toString();
    }

    @Override
    public String toString() {
        return asString();
    }

    public UUID getUUID() {
        return _uuid;
    }
}
