package net.retakethe.policyauction.data.impl.util;

import me.prettyprint.hector.api.ClockResolution;
import me.prettyprint.hector.api.factory.HFactory;

/**
 * @author Nick Clarke
 */
public final class TimestampUtils {

    private static final ClockResolution MILLISECONDS_CLOCK =
            HFactory.createClockResolution(ClockResolution.MILLISECONDS);
    private static final ClockResolution MICROSECONDS_CLOCK =
            HFactory.createClockResolution(ClockResolution.MICROSECONDS);
    private static final ClockResolution MICROSECONDS_SYNC_CLOCK =
            HFactory.createClockResolution(ClockResolution.MICROSECONDS_SYNC);

    private TimestampUtils() {}

    /**
     * Get current timestamp in microsecond precision (but still only millisecond accuracy),
     * synchronizing to avoid duplicates.
     * <p>
     * The timestamp is incremented by at least one microsecond for each call to avoid duplicates.
     * Note this is guaranteed to be unique on the current JVM, but not between multiple machines.
     *
     * @return timestamp in microsecond units
     */
    public static long createLocallyUniqueMicrosecondsTimestamp() {
        return MICROSECONDS_SYNC_CLOCK.createClock();
    }

    /**
     * Get current timestamp in microsecond precision (but still only millisecond accuracy).
     * <p>
     * This method does not synchronize and will produce colliding timestamps if called concurrently.
     *
     * @return timestamp in microsecond units
     */
    public static long createMicrosecondsTimestamp() {
        return MICROSECONDS_CLOCK.createClock();
    }
    
    /**
     * Get current timestamp in milliseconds.
     * <p>
     * This method does not synchronize and will produce colliding timestamps if called concurrently.
     *
     * @return timestamp in millisecond units
     */
    public static long createMillisecondsTimestamp() {
        return MILLISECONDS_CLOCK.createClock();
    }
}
