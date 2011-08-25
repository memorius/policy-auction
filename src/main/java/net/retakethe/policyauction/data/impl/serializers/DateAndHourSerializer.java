package net.retakethe.policyauction.data.impl.serializers;

import net.retakethe.policyauction.data.api.types.DateAndHour;

/**
 * Hector serializer for {@link DateAndHour} type. The GMT time is stored as a UTF8 String.
 *
 * @see me.prettyprint.cassandra.serializers.StringSerializer
 */
public class DateAndHourSerializer extends AbstractStringSerializer<DateAndHour> {

    private static final DateAndHourSerializer INSTANCE = new DateAndHourSerializer();

    public static DateAndHourSerializer get() {
        return INSTANCE;
    }

    /**
     * @see #get()
     */
    private DateAndHourSerializer() {}

    @Override
    protected String toString(DateAndHour obj) {
        return obj.getGMTDateAndHourString();
    }

    @Override
    protected DateAndHour fromString(String obj) {
        return DateAndHour.fromGMTString(obj);
    }
}
