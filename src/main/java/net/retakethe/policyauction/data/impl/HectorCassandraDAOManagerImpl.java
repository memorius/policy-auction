package net.retakethe.policyauction.data.impl;

import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import net.retakethe.policyauction.data.api.DAOManager;
import net.retakethe.policyauction.data.api.PolicyManager;

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
public class HectorCassandraDAOManagerImpl implements DAOManager {

    private static final String KEYSPACE_NAME = "policy_auction";

    /**
     * Threadsafe
     */
    private final Cluster _cluster;

    /**
     * I <i>think</i> this is threadsafe
     */
    private final Keyspace _keyspace;

    private final PolicyManager _policyManager;

    /**
     * @throws InitializationException
     */
    public HectorCassandraDAOManagerImpl() {
        _cluster = HFactory.getOrCreateCluster("policy_auction_cluster",
                new CassandraHostConfigurator("localhost:9160"));

        KeyspaceDefinition keyspaceDef = _cluster.describeKeyspace(KEYSPACE_NAME);

        if (keyspaceDef == null) {
            throw new InitializationException("Cassandra keyspace '" + KEYSPACE_NAME + "' not found."
                    + " Cluster must be manually initialized before webapp startup - see dev docs.");
        }

        _keyspace = HFactory.createKeyspace(KEYSPACE_NAME, _cluster);

        _policyManager = new HectorPolicyManagerImpl(this);
    }

    public Keyspace getKeyspace() {
        return _keyspace;
    }

    @Override
    public PolicyManager getPolicyManager() {
        return _policyManager;
    }


}
