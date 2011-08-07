package net.retakethe.policyauction.data.impl;

import me.prettyprint.cassandra.model.QuorumAllConsistencyLevelPolicy;
import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.services.AppModule;

import org.apache.tapestry5.ioc.annotations.Inject;

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
public class HectorDAOManagerImpl implements DAOManager {

    private final HectorPolicyManagerImpl _policyManager;

    private final HectorKeyspaceManager _keyspaceManager;

    /**
     * Default constructor used by {@link AppModule#bind(org.apache.tapestry5.ioc.ServiceBinder)}
     *
     * @throws InitializationException
     */
    @Inject // This in the one to call from AppModule to register this as a service
    public HectorDAOManagerImpl() {
        this("localhost", 9160);
    }

    /**
     * Constructor used in testing
     *
     * @throws InitializationException
     */
    public HectorDAOManagerImpl(String address, int port) {
        if (address == null) {
            throw new IllegalArgumentException("address must not be null");
        }
        _keyspaceManager = new HectorKeyspaceManager(address + ':' + String.valueOf(port));

        // TODO: is there a better place to set up keyspace consistency levels etc?
        _keyspaceManager.getKeyspace(Schema.SchemaKeyspace.MAIN).setConsistencyLevelPolicy(new QuorumAllConsistencyLevelPolicy());

        _policyManager = new HectorPolicyManagerImpl(_keyspaceManager);
    }

    public KeyspaceManager getKeyspaceManager() {
        return _keyspaceManager;
    }
    
    public void destroy() {
        _keyspaceManager.destroy();
    }

    @Override
    public HectorPolicyManagerImpl getPolicyManager() {
        return _policyManager;
    }

}
