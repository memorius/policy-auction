package net.retakethe.policyauction.data.impl.schema.timestamp;

import net.retakethe.policyauction.data.impl.util.TimestampUtils;

public class MillisecondsTimestampFactory implements TimestampFactory<MillisecondsTimestamp> {

    private static final MillisecondsTimestampFactory INSTANCE = new MillisecondsTimestampFactory();

    public static MillisecondsTimestampFactory get() {
        return INSTANCE;
    }

    private MillisecondsTimestampFactory() {}

    @Override
    public MillisecondsTimestamp createCurrentTimestamp() {
        return new MillisecondsTimestamp(TimestampUtils.createMillisecondsTimestamp());
    }

    @Override
    public MillisecondsTimestamp fromCassandraTimestamp(long cassandraValue) {
        return new MillisecondsTimestamp(cassandraValue);
    }
}
