package net.retakethe.policyauction.data.impl;

import java.util.UUID;

import net.retakethe.policyauction.data.api.PolicyID;

public final class HectorPolicyIDImpl implements PolicyID {

    private final UUID _uuid;

    public HectorPolicyIDImpl(String idString) {
        if (idString == null) {
            throw new IllegalArgumentException("idString must not be null");
        }
        _uuid = UUID.fromString(idString);
    }

    public HectorPolicyIDImpl(UUID uuid) {
        _uuid = uuid;
    }

    @Override
    public String asString() {
        return _uuid.toString();
    }

    @Override
    public String toString() {
        return asString();
    }

    public UUID getUUID() {
        return _uuid;
    }
}