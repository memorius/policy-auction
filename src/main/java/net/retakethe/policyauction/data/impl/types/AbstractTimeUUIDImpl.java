package net.retakethe.policyauction.data.impl.types;

import java.io.Serializable;
import java.util.UUID;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;

/**
 * @author Nick Clarke
 */
public abstract class AbstractTimeUUIDImpl implements Serializable {
    private static final long serialVersionUID = 0L;

    private final UUID _uuid;

    /**
     * Create with new TimeUUID - current time.
     */
    protected AbstractTimeUUIDImpl() {
        _uuid = TimeUUIDUtils.getUniqueTimeUUIDinMillis();
    }

    /**
     * Create with String representation of a TimeUUID.
     */
    protected AbstractTimeUUIDImpl(String idString) {
        if (idString == null) {
            throw new IllegalArgumentException("idString must not be null");
        }
        _uuid = UUID.fromString(idString);
    }

    /**
     * Create with TimeUUID.
     */
    protected AbstractTimeUUIDImpl(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("uuid must not be null");
        }
        _uuid = uuid;
    }
    
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
