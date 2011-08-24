package net.retakethe.policyauction.data.api;

/**
 * Data Access Object abstraction layer - abstracts away details of data storage implementation.
 *
 * @author Nick Clarke
 */
public interface DAOManager {

    PolicyManager getPolicyManager();

    UserManager getUserManager();

}
