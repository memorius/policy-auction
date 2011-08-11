package net.retakethe.policyauction.data.impl.serializers;

import java.util.UUID;

import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.impl.types.UserIDImpl;

/**
 * @author Nick Clarke
 */
public class UserIDSerializer extends AbstractUUIDSerializer<UserID> {

    private static final UserIDSerializer INSTANCE = new UserIDSerializer();

    public static UserIDSerializer get() {
        return INSTANCE;
    }

    /**
     * @see #get()
     */
    private UserIDSerializer() {}

    @Override
    protected UUID toUUID(UserID obj) {
        return ((UserIDImpl) obj).getUUID();
    }

    @Override
    protected UserID fromUUID(UUID uuid) {
        return new UserIDImpl(uuid);
    }
}
