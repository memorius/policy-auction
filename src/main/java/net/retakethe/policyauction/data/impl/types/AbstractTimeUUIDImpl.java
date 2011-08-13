package net.retakethe.policyauction.data.impl.types;

import java.io.Serializable;
import java.util.UUID;

import net.retakethe.policyauction.data.impl.serializers.AbstractTimeUUIDSerializer;
import net.retakethe.policyauction.data.impl.util.UUIDUtils;

import org.apache.cassandra.db.marshal.TimeUUIDType;

/**
 * Base class for ID types represented as a UUID.
 *
 * @author Nick Clarke
 */
public abstract class AbstractTimeUUIDImpl implements Serializable, Comparable<AbstractTimeUUIDImpl> {
    private static final long serialVersionUID = 0L;

    private final UUID uuid;

    /**
     * Create with new TimeUUID - current time.
     */
    protected AbstractTimeUUIDImpl() {
        uuid = UUIDUtils.createUniqueTimeUUID();
    }

    /**
     * Create with String representation of a TimeUUID.
     */
    protected AbstractTimeUUIDImpl(String idString) {
        if (idString == null) {
            throw new IllegalArgumentException("idString must not be null");
        }
        uuid = UUID.fromString(idString);
    }

    /**
     * Create with TimeUUID.
     */
    protected AbstractTimeUUIDImpl(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("uuid must not be null");
        }
        this.uuid = uuid;
    }
    
    public String asString() {
        return uuid.toString();
    }

    @Override
    public String toString() {
        return asString();
    }

    public UUID getUUID() {
        return uuid;
    }

    /**
     * Get hashCode considering only the uuid field.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
        return result;
    }

    /**
     * Check whether the specified object is an instance of the exact same subclass with the same UUID.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        // Different subclasses do NOT match
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractTimeUUIDImpl other = (AbstractTimeUUIDImpl) obj;
        if (this.uuid == null) {
            if (other.uuid != null) {
                return false;
            }
        } else if (!this.uuid.equals(other.uuid)) {
            return false;
        }
        return true;
    }
    
    private static final AbstractTimeUUIDSerializer<AbstractTimeUUIDImpl> SERIALIZER =
            new AbstractTimeUUIDSerializer<AbstractTimeUUIDImpl>() {
                @Override
                protected UUID toUUID(AbstractTimeUUIDImpl obj) {
                    return obj.getUUID();
                }

                @Override
                protected AbstractTimeUUIDImpl fromUUID(UUID obj) {
                    throw new UnsupportedOperationException();
                }
            };

    /**
     * Same as Cassandra TimeUUIDType comparison
     */
    @Override
    public int compareTo(AbstractTimeUUIDImpl o) {
        return TimeUUIDType.instance.compare(SERIALIZER.toByteBuffer(this), SERIALIZER.toByteBuffer(o));
    }
}
