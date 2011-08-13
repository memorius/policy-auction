package net.retakethe.policyauction.data.impl.schema.timestamp;

/**
 * Timestamp in milliseconds since the unix epoch, as per {@link System#currentTimeMillis()}.
 *
 * @author Nick Clarke
 */
public class MillisecondsTimestamp extends AbstractTimestamp {
    private static final long serialVersionUID = 0L;

    public MillisecondsTimestamp(long millis) {
        super(millis);
    }
}
