package net.retakethe.policyauction.data.impl.util;

import java.util.Random;

import me.prettyprint.cassandra.service.clock.AbstractClockResolution;
import me.prettyprint.hector.api.ClockResolution;

/**
 * Variant of MicrosecondsSyncClockResolution which uses all the space in 'long' plus a counter and random component.
 * <p>
 * Values are based on the current time in milliseconds, with a sync counter to guarantee locally-created values are
 * unique and monotonic, and a random component to minimize the chances of collisions between timestamps from
 * different machines.
 *
 * @author Nick Clarke
 */
public class MonotonicMillisPlusRandomClockResolution extends AbstractClockResolution implements ClockResolution {
    private static final long serialVersionUID = 0L;

    // Note this will overflow somewhere around the year 2262
    // (when System.currentTimeMillis() x multiplier exceeds Long.MAX_VALUE),
    // at which point new Cassandra writes will lose over the old values and be discarded.
    // Hopefully this app will still be running then, so I'll be remembered.
    private static final int TIME_MULTIPLIER = 1000000;

    public static final int MAX_VALUES_PER_MILLISECOND = 100;

    private static final int COUNTER_MULTIPLIER = TIME_MULTIPLIER / MAX_VALUES_PER_MILLISECOND;
    private static final int RANDOM_RANGE = TIME_MULTIPLIER / COUNTER_MULTIPLIER;

    private static final Random RANDOM = new Random();

    private static long lastMillis = -1;
    private static int counter = 0;

    @Override
    public long createClock() {
        long time = getSystemMilliseconds();
        long base;
        synchronized (MonotonicMillisPlusRandomClockResolution.class) {
            if (time > lastMillis) {
                lastMillis = time;
                counter = 0;
            } else {
                ++counter;
                if (counter >= MAX_VALUES_PER_MILLISECOND) {
                    counter = 0;
                    ++lastMillis;
                }
                time = lastMillis;
            }
            base = (time * TIME_MULTIPLIER) + (counter * COUNTER_MULTIPLIER);
        }
        return base + RANDOM.nextInt(RANDOM_RANGE);
    }
}
