package net.retakethe.policyauction.data.impl.serializers;

import net.retakethe.policyauction.data.api.types.UserRole;

/**
 * Hector serializer for {@link UserRole} type. The enum value is stored as a UTF8 String.
 *
 * @see me.prettyprint.cassandra.serializers.StringSerializer
 */
public class UserRoleSerializer extends AbstractEnumSerializer<UserRole> {

    private static final UserRoleSerializer INSTANCE = new UserRoleSerializer();

    public static UserRoleSerializer get() {
        return INSTANCE;
    }

    /**
     * @see #get()
     */
    private UserRoleSerializer() {}

    @Override
    protected UserRole fromString(String obj) {
        return UserRole.valueOf(obj);
    }
}
