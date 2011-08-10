package net.retakethe.policyauction.data.impl.types;

import java.util.UUID;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import net.retakethe.policyauction.data.api.types.LogMessageID;

public final class HectorLogMessageIDImpl implements LogMessageID {

    private final UUID _uuid;

    /**
     * Create with new TimeUUID - current time.
     */
    public HectorLogMessageIDImpl() {
        _uuid = TimeUUIDUtils.getUniqueTimeUUIDinMillis();
    }

    public HectorLogMessageIDImpl(String idString) {
        if (idString == null) {
            throw new IllegalArgumentException("idString must not be null");
        }
        _uuid = UUID.fromString(idString);
    }

    public HectorLogMessageIDImpl(UUID uuid) {
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
