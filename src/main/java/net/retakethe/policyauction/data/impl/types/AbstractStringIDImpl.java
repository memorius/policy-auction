package net.retakethe.policyauction.data.impl.types;

import java.io.Serializable;

/**
 * Base class for ID types represented as a string.
 *
 * @author Nick Clarke
 */
public abstract class AbstractStringIDImpl implements Serializable, Comparable<AbstractStringIDImpl> {
    private static final long serialVersionUID = 0L;

    private final String id;

    /**
     * Create with String ID value.
     */
    protected AbstractStringIDImpl(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        this.id = id;
    }

    public String asString() {
        return id;
    }

    @Override
    public String toString() {
        return asString();
    }

    /**
     * Get hashCode considering only the id field.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }

    /**
     * Check whether the specified object is an instance of the exact same subclass with the same id.
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
        AbstractStringIDImpl other = (AbstractStringIDImpl) obj;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(AbstractStringIDImpl o) {
        return id.compareTo(o.id);
    }
}
