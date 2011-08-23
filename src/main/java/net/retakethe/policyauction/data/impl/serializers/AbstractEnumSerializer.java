package net.retakethe.policyauction.data.impl.serializers;

/**
 * Hector serializer for arbitrary java enum types. The enum value is stored as a UTF8 String.
 *
 * @see me.prettyprint.cassandra.serializers.StringSerializer
 */
public abstract class AbstractEnumSerializer<T extends Enum<T>> extends AbstractStringSerializer<T> {

    protected AbstractEnumSerializer() {}

    @Override
    protected final String toString(T obj) {
        return obj.name();
    }
}
