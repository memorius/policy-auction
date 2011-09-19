package net.retakethe.policyauction.data.impl.manager;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import me.prettyprint.cassandra.connection.LeastActiveBalancingPolicy;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.ExhaustedPolicy;
import me.prettyprint.cassandra.service.FailoverPolicy;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.data.impl.schema.SchemaKeyspace;
import net.retakethe.policyauction.data.impl.schema.family.BaseColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.util.AssertArgument;

public class KeyspaceManagerImpl implements KeyspaceManager {

    /**
     * Threadsafe
     */
    private Cluster cluster;

    /**
     * No synchronization needed - only modified in constructor.
     */
    private Map<SchemaKeyspace, Keyspace> keyspaces;

    /**
     * @param hosts Comma-separated <code>host:port,host:port</code> or just <code>host,host</code>
     */
    public KeyspaceManagerImpl(String hosts) {
        this(hosts, Collections.<String, String>emptyMap());
    }

    /**
     * @param hosts Comma-separated <code>host:port,host:port</code> or just <code>host,host</code>
     */
    public KeyspaceManagerImpl(String hosts, String username, String password) {
        this(hosts, makeCredentials(username, password));
    }

    private static Map<String, String> makeCredentials(String username, String password) {
        AssertArgument.notNull(username, "username");
        AssertArgument.notNull(password, "password");
        Map<String, String> credentials = new HashMap<String, String>(2);
        credentials.put("username", username);
        credentials.put("password", password);
        return credentials;
    }

    private KeyspaceManagerImpl(String hosts, Map<String, String> credentials) {
        AssertArgument.notNull(hosts, "hosts");
        AssertArgument.notNull(credentials, "credentials");

        CassandraHostConfigurator configurator = new CassandraHostConfigurator(hosts);
        configurator.setLifo(true);
        configurator.setAutoDiscoverHosts(false);
        configurator.setRetryDownedHosts(true);
        configurator.setLoadBalancingPolicy(new LeastActiveBalancingPolicy());
        configurator.setExhaustedPolicy(ExhaustedPolicy.WHEN_EXHAUSTED_BLOCK);
        configurator.setMaxWaitTimeWhenExhausted(15 * 1000L); // Milliseconds

        cluster = HFactory.createCluster("policy_auction_cluster", configurator, credentials);

        keyspaces = new EnumMap<SchemaKeyspace, Keyspace>(SchemaKeyspace.class);
        for (SchemaKeyspace schemaKS : SchemaKeyspace.values()) {
            Keyspace keyspace = loadKeyspace(schemaKS.getKeyspaceName(), schemaKS.getConsistencyLevelPolicy());

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
        HFactory.shutdownCluster(cluster);
        cluster = null;
    }

    private Keyspace loadKeyspace(String keyspaceName, ConsistencyLevelPolicy consistencyLevelPolicy) {
        KeyspaceDefinition keyspaceDef;
        try {
            keyspaceDef = cluster.describeKeyspace(keyspaceName);
        } catch (HectorException e) {
            throw new InitializationException("Cannot retrieve cassandra keyspace '" + keyspaceName + "':"
                    + " check the cluster has been started before webapp startup - see dev docs.", e);
        }

        if (keyspaceDef == null) {
            throw new InitializationException("Cassandra keyspace '" + keyspaceName + "' not found."
                    + " Cluster must be manually initialized before webapp startup - see dev docs.");
        }

        return HFactory.createKeyspace(keyspaceName, cluster, consistencyLevelPolicy,
                FailoverPolicy.ON_FAIL_TRY_ALL_AVAILABLE);
    }
}
