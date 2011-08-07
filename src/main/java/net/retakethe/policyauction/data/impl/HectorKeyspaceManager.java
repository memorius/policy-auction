package net.retakethe.policyauction.data.impl;

import java.util.EnumMap;
import java.util.Map;

import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import net.retakethe.policyauction.data.impl.schema.Schema;

/**
 * Holder for keyspace access, used in production and for unit tests of individual manager impls
 *
 * @author Nick Clarke
 */
public class HectorKeyspaceManager implements KeyspaceManager {

    /**
     * Threadsafe
     */
    private Cluster _cluster;

    /**
     * No synchronization needed - only modified in constructor.
     */
    private Map<Schema.SchemaKeyspace, Keyspace> keyspaces;

    public HectorKeyspaceManager(String hostAndPort) {
        _cluster = HFactory.getOrCreateCluster("policy_auction_cluster",
                new CassandraHostConfigurator(hostAndPort));
        keyspaces = new EnumMap<Schema.SchemaKeyspace, Keyspace>(Schema.SchemaKeyspace.class);
        for (Schema.SchemaKeyspace schemaKS : Schema.SchemaKeyspace.values()) {
            keyspaces.put(schemaKS, findKeyspace(schemaKS.getKeyspaceName()));
        }
    }

    @Override
    public Keyspace getKeyspace(Schema.SchemaKeyspace schemaKS) {
        Keyspace ks = keyspaces.get(schemaKS);
        if (ks == null) {
            throw new IllegalArgumentException("Unknown schema: " + schemaKS);
        }
        return ks;
    }

    protected void destroy() {
        keyspaces = new EnumMap<Schema.SchemaKeyspace, Keyspace>(Schema.SchemaKeyspace.class);
        HFactory.shutdownCluster(_cluster);
        _cluster = null;
    }

    private Keyspace findKeyspace(String keyspaceName) {
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
