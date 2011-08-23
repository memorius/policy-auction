package net.retakethe.policyauction.data.api;

/**
 * Data Access Object abstraction layer - abstracts away details of data storage implementation.
 * <p>
 * This is the entry point from Tapestry pages into the data layer.
 * The DAOManager is accessed by marking a field on the page class with
 * {@link org.apache.tapestry5.ioc.annotations.Inject}.
 *
 * @author Nick Clarke
 */
public interface DAOManager {

    LogManager getLogManager();

    PolicyManager getPolicyManager();

    // UserManager getUserManager();

    UserVoteManager getUserVoteManager();

    SystemInfoManager getSystemInfoManager();

}
