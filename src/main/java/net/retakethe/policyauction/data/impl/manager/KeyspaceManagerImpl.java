package net.retakethe.policyauction.data.impl.manager;

import java.util.EnumMap;
import java.util.Map;

import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.family.BaseColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;

public class KeyspaceManagerImpl implements KeyspaceManager {

    /**
     * Threadsafe
     */
    private Cluster _cluster;

    /**
     * No synchronization needed - only modified in constructor.
     */
    private Map<SchemaKeyspace, Keyspace> keyspaces;

    public KeyspaceManagerImpl(String hostAndPort) {
        _cluster = HFactory.getOrCreateCluster("policy_auction_cluster",
                new CassandraHostConfigurator(hostAndPort));
        keyspaces = new EnumMap<SchemaKeyspace, Keyspace>(SchemaKeyspace.class);
        for (SchemaKeyspace schemaKS : SchemaKeyspace.values()) {
            Keyspace keyspace = loadKeyspace(schemaKS.getKeyspaceName());

            keyspace.setConsistencyLevelPolicy(schemaKS.getConsistencyLevelPolicy());

            keyspaces.put(schemaKS, keyspace);
        }
    }

    /**
     * Called at startup (and per test cycle after cleaning DB).
     * Allow CFs to do any initialization they need, e.g. creating default rows.
     */
    public void initializeColumnFamilies() {
        for (BaseColumnFamily<?, ? extends Timestamp> cf : Schema.getAllCFs()) {
            cf.initialize(this);
        }
    }

    @Override
    public Keyspace getKeyspace(SchemaKeyspace schemaKS) {
        Keyspace ks = keyspaces.get(schemaKS);
        if (ks == null) {
            throw new IllegalArgumentException("Unknown schema: " + schemaKS);
        }
        return ks;
    }

    protected void destroy() {
        keyspaces = new EnumMap<SchemaKeyspace, Keyspace>(SchemaKeyspace.class);
        HFactory.shutdownCluster(_cluster);
        _cluster = null;
    }

    private Keyspace loadKeyspace(String keyspaceName) {
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
