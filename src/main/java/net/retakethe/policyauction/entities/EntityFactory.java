package net.retakethe.policyauction.entities;

import java.util.List;

import net.retakethe.policyauction.data.api.dao.PolicyDetailsDAO;
import net.retakethe.policyauction.data.api.dao.UserDAO;
import net.retakethe.policyauction.util.Functional;
import net.retakethe.policyauction.util.Functional.Converter;

/**
 * Conversion between data access layer and Tapestry entities layer.
 * <p>
 * This exists mostly so that access to the entities' embedded DAO objects can be hidden (protected methods) from the
 * Tapestry page classes, so the separation of layers is maintained.
 *
 * @author Nick Clarke
 */
public final class EntityFactory {

    public static List<PolicyDetails> makePolicyDetailsFromDAO(List<PolicyDetailsDAO> allPolicies) {
        return Functional.map(allPolicies, new Converter<PolicyDetailsDAO, PolicyDetails>() {
            @Override
            public PolicyDetails convert(PolicyDetailsDAO a) {
                return makePolicyDetailsFromDAO(a);
            }
        });
    }

    public static PolicyDetails makePolicyDetailsFromDAO(PolicyDetailsDAO policyDAO) {
        return new PolicyDetails(policyDAO);
    }

    public static PolicyDetailsDAO getPolicyDAO(PolicyDetails policy) {
        return policy.getPolicyDetailsDAO();
    }
    
    public static List<User> makeUsers(List<UserDAO> allUsers) {
        return Functional.map(allUsers, new Converter<UserDAO, User>() {
            @Override
            public User convert(UserDAO a) {
                return makeUser(a);
            }
        });
    }

    public static User makeUser(UserDAO userDAO) {
        return new User(userDAO);
    }

    public static UserDAO getUserDAO(User user) {
        return user.getUserDAO();
    }
}
