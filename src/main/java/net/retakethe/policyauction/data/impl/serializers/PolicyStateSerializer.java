package net.retakethe.policyauction.data.impl.serializers;

import net.retakethe.policyauction.data.api.dao.PolicyState;

/**
 * Hector serializer for {@link PolicyState} type. The enum value is stored as a UTF8 String.
 *
 * @see me.prettyprint.cassandra.serializers.StringSerializer
 */
public class PolicyStateSerializer extends AbstractEnumSerializer<PolicyState> {

    private static final PolicyStateSerializer INSTANCE = new PolicyStateSerializer();

    public static PolicyStateSerializer get() {
        return INSTANCE;
    }

    /**
     * @see #get()
     */
    private PolicyStateSerializer() {}

    @Override
    protected PolicyState fromString(String obj) {
        return PolicyState.valueOf(obj);
    }
}
