package net.retakethe.policyauction.data.impl.schema.timestamp;

import net.retakethe.policyauction.data.impl.util.TimestampUtils;

public class UniqueTimestampFactory implements TimestampFactory<UniqueTimestamp> {

    private static final UniqueTimestampFactory INSTANCE = new UniqueTimestampFactory();

    public static UniqueTimestampFactory get() {
        return INSTANCE;
    }

    private UniqueTimestampFactory() {}

    @Override
    public UniqueTimestamp createCurrentTimestamp() {
        return new UniqueTimestamp(TimestampUtils.createUniqueTimePlusCounterPlusRandomTimestamp());
    }

    @Override
    public UniqueTimestamp fromCassandraTimestamp(long cassandraValue) {
        return new UniqueTimestamp(cassandraValue);
    }
}
