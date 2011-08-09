package net.retakethe.policyauction.data.impl.serializers;

import java.util.UUID;

import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.data.impl.types.HectorPolicyIDImpl;

/**
 * @author Nick Clarke
 */
public class PolicyIDSerializer extends AbstractUUIDSerializer<PolicyID> {

    private static final PolicyIDSerializer INSTANCE = new PolicyIDSerializer();

    public static PolicyIDSerializer get() {
        return INSTANCE;
    }

    @Override
    protected UUID toUUID(PolicyID obj) {
        return ((HectorPolicyIDImpl) obj).getUUID();
    }

    @Override
    protected PolicyID fromUUID(UUID uuid) {
        return new HectorPolicyIDImpl(uuid);
    }
}
