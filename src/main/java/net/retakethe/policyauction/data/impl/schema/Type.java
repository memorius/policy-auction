package net.retakethe.policyauction.data.impl.schema;

import java.util.Date;
import java.util.UUID;

import me.prettyprint.cassandra.serializers.BooleanSerializer;
import me.prettyprint.cassandra.serializers.DateSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;
import me.prettyprint.hector.api.Serializer;

/**
 * Enum-like class representing datatypes for Cassandra keys,
 * column/supercolumn/subcolumn names and column/subcolumn values.
 * <p>
 * Can't use an actual enum due to use of generics.
 *
 * @author Nick Clarke
 */
public final class Type<T> {

    /**
     * UTF8 String, for cassandra UTF8Type.
     */
    public static final Type<String>  UTF8      = new Type<String>(String.class, StringSerializer.get());

    /**
     * java.util.UUID, for cassandra TimeUUIDType.
     *
     * @see me.prettyprint.cassandra.utils.TimeUUIDUtils
     */
    public static final Type<UUID>    TIME_UUID = new Type<UUID>(UUID.class, UUIDSerializer.get());

    /**
     * java.util.Date, stored in cassandra as LongType.
     */
    public static final Type<Date>    DATE      = new Type<Date>(Date.class, DateSerializer.get());

    /**
     * Boolean, stored in cassandra as a 1 or 0 byte (BytesType).
     */
    public static final Type<Boolean> BOOLEAN   = new Type<Boolean>(Boolean.class, BooleanSerializer.get());


    private final Class<T> type;
    private final Serializer<T> serializer;

    private Type(Class<T> type, Serializer<T> serializer) {
        this.type = type;
        this.serializer = serializer;
    }

    public Class<T> getType() {
        return this.type;
    }

    public Serializer<T> getSerializer() {
        return this.serializer;
    }
}
