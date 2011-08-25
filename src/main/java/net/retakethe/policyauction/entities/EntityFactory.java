package net.retakethe.policyauction.entities;

import java.util.List;

import net.retakethe.policyauction.data.api.dao.PolicyDAO;
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

    public static List<Policy> makePoliciesFromDAO(List<PolicyDAO> allPolicies) {
        return Functional.map(allPolicies, new Converter<PolicyDAO, Policy>() {
            @Override
            public Policy convert(PolicyDAO a) {
                return makePolicyFromDAO(a);
            }
        });
    }

    public static Policy makePolicyFromDAO(PolicyDAO policyDAO) {
        return new Policy(policyDAO);
    }

    public static PolicyDAO getPolicyDAO(Policy policy) {
        return policy.getPolicyDAO();
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
