package net.retakethe.policyauction.data.impl.schema.timestamp;

import net.retakethe.policyauction.data.impl.util.TimestampUtils;

public class MillisTimestampFactory implements TimestampFactory<MillisTimestamp> {

    private static final MillisTimestampFactory INSTANCE = new MillisTimestampFactory();

    public static MillisTimestampFactory get() {
        return INSTANCE;
    }

    private MillisTimestampFactory() {}

    @Override
    public MillisTimestamp createCurrentTimestamp() {
        return new MillisTimestamp(TimestampUtils.createMillisecondsTimestamp());
    }

    @Override
    public MillisTimestamp fromCassandraTimestamp(long cassandraValue) {
        return new MillisTimestamp(cassandraValue);
    }
}
