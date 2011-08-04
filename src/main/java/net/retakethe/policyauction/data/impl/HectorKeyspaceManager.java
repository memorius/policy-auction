package net.retakethe.policyauction.data.impl;

import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;

/**
 * Holder for keyspace access, used in production and for unit tests of individual manager impls
 *
 * @author Nick Clarke
 */
public class HectorKeyspaceManager {

    /**
     * Threadsafe
     */
    private Cluster _cluster;

    public HectorKeyspaceManager(String hostAndPort) {
        _cluster = HFactory.getOrCreateCluster("policy_auction_cluster",
                new CassandraHostConfigurator(hostAndPort));
    }

    protected void destroy() {
        HFactory.shutdownCluster(_cluster);
        _cluster = null;
    }

    public Keyspace getKeyspace(String keyspaceName) {
        KeyspaceDefinition keyspaceDef;
        try {
            keyspaceDef = _cluster.describeKeyspace(keyspaceName);
        } catch (HectorException e) {
            throw new InitializationException("Cannot retrieve cassandra keyspace '" + keyspaceName + "':"
                    + " check the cluster has been started before webapp startup - see dev docs.");
        }

        if (keyspaceDef == null) {
            throw new InitializationException("Cassandra keyspace '" + keyspaceName + "' not found."
                    + " Cluster must be manually initialized before webapp startup - see dev docs.");
        }

        return HFactory.createKeyspace(keyspaceName, _cluster);
    }
}
