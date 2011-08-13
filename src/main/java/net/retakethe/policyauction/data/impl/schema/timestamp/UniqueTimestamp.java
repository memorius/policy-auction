package net.retakethe.policyauction.data.impl.schema.timestamp;

/**
 * Timestamp in unspecified units which is locally unique and likely to be unique between different machines.
 *
 * @author Nick Clarke
 */
public class UniqueTimestamp extends AbstractTimestamp {
    private static final long serialVersionUID = 0L;

    protected UniqueTimestamp(long value) {
        super(value);
    }
}
