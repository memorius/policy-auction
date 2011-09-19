package net.retakethe.policyauction.data.impl.manager;

import java.util.Enumeration;

import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.data.impl.logging.CassandraLog4jAppender;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.services.AppModule;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;

/**
 * Data access using Apache Cassandra via Hector library.
 * <p>
 * TODO: hector config for connection pooling, multi-node cassandra cluster, load balancing and failover.
 *   - see https://github.com/rantav/hector/wiki/User-Guide
 * TODO: config (at least the cassandra addresses and port) should be via runtime properties.
 *       OR: could use spring-based config.
 *
 * @author Nick Clarke
 */
public class DAOManagerImpl implements DAOManager, RegistryShutdownListener {

    private final KeyspaceManagerImpl keyspaceManager;

    private final LogManagerImpl logManager;
    private final PolicyManagerImpl policyManager;
    private final PortfolioManagerImpl portfolioManager;
    private final SystemInfoManagerImpl systemInfoManager;
    private final UserManagerImpl userManager;
    private final UserVoteAllocationManagerImpl userVoteManager;
    private final VoteSalaryManagerImpl voteSalaryManager;
    private final VotingConfigManagerImpl votingConfigManager;

    /**
     * Default constructor used by {@link AppModule#bind(org.apache.tapestry5.ioc.ServiceBinder)}
     *
     * @throws InitializationException
     */
    @Inject // This is the one to call from AppModule to register this as a service
    public DAOManagerImpl() {
        this("localhost", 9160);
    }

    /**
     * Constructor used in testing
     *
     * @throws InitializationException
     */
    public DAOManagerImpl(String address, int port) {
        if (address == null) {
            throw new IllegalArgumentException("address must not be null");
        }
        keyspaceManager = new KeyspaceManagerImpl(address + ':' + String.valueOf(port));
        keyspaceManager.initializeColumnFamilies();

        systemInfoManager = new SystemInfoManagerImpl(keyspaceManager);

        logManager = new LogManagerImpl(keyspaceManager);
        initializeCassandraLogAppender();

        portfolioManager = new PortfolioManagerImpl(keyspaceManager);

        policyManager = new PolicyManagerImpl(keyspaceManager, portfolioManager);

        userManager = new UserManagerImpl(keyspaceManager);

        votingConfigManager = new VotingConfigManagerImpl(keyspaceManager);

        voteSalaryManager = new VoteSalaryManagerImpl(keyspaceManager, systemInfoManager, votingConfigManager);

        userVoteManager = new UserVoteAllocationManagerImpl(keyspaceManager, votingConfigManager,
                voteSalaryManager);
    }

    private void initializeCassandraLogAppender() {
        @SuppressWarnings("unchecked")
        Enumeration<Appender> appenders = LogManager.getRootLogger().getAllAppenders();

        while (appenders.hasMoreElements()) {
            Appender appender = appenders.nextElement();

            // This appender is configured in log4j.properties
            // If we don't find one, that's OK - we're probably running in a unit test.
            if (appender instanceof CassandraLog4jAppender) {
                ((CassandraLog4jAppender) appender).setLogWriter(this.logManager);
            }
        }
    }

    public KeyspaceManager getKeyspaceManager() {
        return keyspaceManager;
    }

    /**
     * Called by Tapestry once service is created and configured.
     */
    @PostInjection
    public void startupService(RegistryShutdownHub shutdownHub) {
        // Register so that registryDidShutdown() will be called at Tapestry shutdown.
        shutdownHub.addRegistryShutdownListener(this);
    }

    /**
     * Listener method called by Tapestry when IOC container shuts down.
     *
     * @see #startupService(RegistryShutdownHub)
     */
    @Override
    public void registryDidShutdown() {
        destroy();
    }

    public void destroy() {
        keyspaceManager.destroy();
    }

    @Override
    public LogManagerImpl getLogManager() {
        return logManager;
    }

    @Override
    public PolicyManagerImpl getPolicyManager() {
        return policyManager;
    }
    
    @Override
    public PortfolioManagerImpl getPortfolioManager() {
        return portfolioManager;
    }
    
    @Override
    public UserManagerImpl getUserManager() {
    	return userManager;
    }

    @Override
    public UserVoteAllocationManagerImpl getUserVoteAllocationManager() {
        return userVoteManager;
    }

    @Override
    public VotingConfigManagerImpl getVotingConfigManager() {
        return this.votingConfigManager;
    }

    @Override
    public VoteSalaryManagerImpl getVoteSalaryManager() {
        return voteSalaryManager;
    }

    @Override
    public SystemInfoManagerImpl getSystemInfoManager() {
        return systemInfoManager;
    }
}
