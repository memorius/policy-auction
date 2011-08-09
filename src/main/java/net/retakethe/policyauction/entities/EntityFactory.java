package net.retakethe.policyauction.entities;

import java.util.List;

import net.retakethe.policyauction.data.api.dao.PolicyDAO;
import net.retakethe.policyauction.util.Functional;
import net.retakethe.policyauction.util.Functional.Converter;

/**
 * Conversion between data access layer and Tapestry entities layer
 *
 * @author Nick Clarke
 */
public final class EntityFactory {

    public static List<Policy> makePolicies(List<PolicyDAO> allPolicies) {
        return Functional.map(allPolicies, new Converter<PolicyDAO, Policy>() {
            @Override
            public Policy convert(PolicyDAO a) {
                return makePolicy(a);
            }
        });
    }

    public static Policy makePolicy(PolicyDAO policyDAO) {
        return new Policy(policyDAO);
    }

    public static PolicyDAO getPolicyDAO(Policy policy) {
        return policy.getPolicyDAO();
    }
}
