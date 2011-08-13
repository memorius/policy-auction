package net.retakethe.policyauction.data.impl.serializers;

import java.util.UUID;

import net.retakethe.policyauction.data.impl.types.internal.VoteRecordID;
import net.retakethe.policyauction.data.impl.types.internal.VoteRecordIDImpl;

/**
 * @author Nick Clarke
 */
public class VoteRecordIDSerializer extends AbstractTimeUUIDSerializer<VoteRecordID> {

    private static final VoteRecordIDSerializer INSTANCE = new VoteRecordIDSerializer();

    public static VoteRecordIDSerializer get() {
        return INSTANCE;
    }

    /**
     * @see #get()
     */
    private VoteRecordIDSerializer() {}

    @Override
    protected UUID toUUID(VoteRecordID obj) {
        return ((VoteRecordIDImpl) obj).getUUID();
    }

    @Override
    protected VoteRecordID fromUUID(UUID uuid) {
        return new VoteRecordIDImpl(uuid);
    }
}
