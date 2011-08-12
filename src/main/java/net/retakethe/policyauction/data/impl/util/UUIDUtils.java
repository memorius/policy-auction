package net.retakethe.policyauction.data.impl.util;

import java.util.UUID;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;

/**
 * Utilities for creating and manipulating UUIDs.
 *
 * @author Nick Clarke
 */
public final class UUIDUtils {

    private static final UUID ZERO_TIME_UUID;
    static {
        long msb = 0x0000000000001000L; // sets the UUID version field to 1 to indicate that it's a TimeUUID
        long lsb = 0L;
        ZERO_TIME_UUID = new UUID(msb, lsb);
    }

    /**
     * Get the time in milliseconds from a TimeUUID.
     * 
     * @param timeUUID the UUID to extract time from
     * @return time since milliseconds epoch, compatible with {@link System#currentTimeMillis()}.
     * @throws UnsupportedOperationException if UUID is not a TimeUUID (i.e. if UUID version field != 1).
     */
    public static long getTimeMillisFromTimeUUID(UUID timeUUID) {
        return TimeUUIDUtils.getTimeFromUUID(timeUUID);
    }

    /**
     * Create TimeUUID using the current system time, with duplicate prevention for UUIDs generated in this application.
     * <p>
     * The timestamp uses the millisecond-precision system time, converted to microseconds since the UUID epoch,
     * with the value incremented by at least one microsecond since any previously-generated current time UUID
     * made by this method.
     * <p>
     * Note the remaining components of the UUID - based on the host/address/MAC and a random value -
     * will avoid duplicates between instances of this library in separate classloaders or on separate machines.
     *
     * @return
     */
    public static UUID createUniqueTimeUUID() {
        return TimeUUIDUtils.getUniqueTimeUUIDinMillis();
    }

    /**
     * TimeUUID with time and all other variable bytes set to zero: no per-node or randomized components.
     * <p>
     * This is for use as the first record in sequences sorted by TimeUUID:
     * it will sort as the first one if the others are created with the current time,
     * and will intentionally collide if the record is written more than once.
     * <p>
     * Note that it's still possible to retrieve a milliseconds timestamp for this UUID,
     * but it will be a long time ago, in the year 1582!.
     *
     * @return the zero TimeUUID (singleton instance)
     */
    public static UUID getZeroTimeUUID() {
        return ZERO_TIME_UUID;
    }
}
