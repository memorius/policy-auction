package net.retakethe.policyauction.data.impl.serializers;

import org.joda.time.LocalDate;

/**
 * Hector serializer for {@link LocalDate} type. The date is stored as a UTF8 String.
 *
 * @see me.prettyprint.cassandra.serializers.StringSerializer
 */
public class LocalDateSerializer extends AbstractStringSerializer<LocalDate> {

    private static final LocalDateSerializer INSTANCE = new LocalDateSerializer();

    public static LocalDateSerializer get() {
        return INSTANCE;
    }

    /**
     * @see #get()
     */
    private LocalDateSerializer() {}

    @Override
    protected String toString(LocalDate obj) {
        return obj.toString();
    }

    @Override
    protected LocalDate fromString(String obj) {
        return LocalDate.parse(obj);
    }
}
