package net.retakethe.policyauction.data.impl.schema.timestamp;

/**
 * Timestamp in milliseconds since the unix epoch, as per {@link System#currentTimeMillis()}.
 *
 * @author Nick Clarke
 */
public class MillisTimestamp extends AbstractTimestamp {
    private static final long serialVersionUID = 0L;

    protected MillisTimestamp(long millis) {
        super(millis);
    }
}
