package net.retakethe.policyauction.data.impl.serializers;

import net.retakethe.policyauction.data.api.types.DayOfWeek;

/**
 * Hector serializer for {@link DayOfWeek} type. The enum value is stored as a UTF8 String.
 *
 * @see me.prettyprint.cassandra.serializers.StringSerializer
 */
public class DayOfWeekSerializer extends AbstractEnumSerializer<DayOfWeek> {

    private static final DayOfWeekSerializer INSTANCE = new DayOfWeekSerializer();

    public static DayOfWeekSerializer get() {
        return INSTANCE;
    }

    /**
     * @see #get()
     */
    private DayOfWeekSerializer() {}

    @Override
    protected DayOfWeek fromString(String obj) {
        return DayOfWeek.valueOf(obj);
    }
}
