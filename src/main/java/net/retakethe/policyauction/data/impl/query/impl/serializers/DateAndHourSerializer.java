package net.retakethe.policyauction.data.impl.query.impl.serializers;

import java.nio.ByteBuffer;

import me.prettyprint.cassandra.serializers.AbstractSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.ddl.ComparatorType;
import net.retakethe.policyauction.data.api.types.DateAndHour;

/**
 * Hector serializer for {@link DateAndHour} type. The GMT time is stored as a UTF8 String.
 *
 * @see me.prettyprint.cassandra.serializers.StringSerializer
 */
public class DateAndHourSerializer extends AbstractSerializer<DateAndHour>
        implements Serializer<DateAndHour> {

    private static final DateAndHourSerializer INSTANCE = new DateAndHourSerializer();

    public static DateAndHourSerializer get() {
        return INSTANCE;
    }

    @Override
    public ByteBuffer toByteBuffer(DateAndHour obj) {
        if (obj == null) {
            return null;
        }
        return StringSerializer.get().toByteBuffer(obj.getGMTDateAndHourString());
    }

    @Override
    public DateAndHour fromByteBuffer(ByteBuffer byteBuffer) {
        String s = StringSerializer.get().fromByteBuffer(byteBuffer);
        if (s == null) {
            return null;
        }
        return DateAndHour.fromGMTString(s);
    }

    @Override
    public ComparatorType getComparatorType() {
        return StringSerializer.get().getComparatorType();
    }
}