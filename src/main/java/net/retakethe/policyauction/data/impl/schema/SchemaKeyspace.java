package net.retakethe.policyauction.data.impl.schema;

import me.prettyprint.hector.api.ConsistencyLevelPolicy;

/**
 * Cassandra schema elements as in cassandra-schema.txt.
 *
 * @author Nick Clarke
 */
public enum SchemaKeyspace {

    MAIN("policy_auction", ConsistencyLevel.WRITE_QUORUM_READ_QUORUM),
    LOGS("policy_auction_logs", ConsistencyLevel.WRITE_ANY_READ_ONE);


    private final String keyspaceName;
    private final ConsistencyLevelPolicy consistencyLevelPolicy;

    private SchemaKeyspace(String keyspaceName, ConsistencyLevelPolicy consistencyLevelPolicy) {
        this.keyspaceName = keyspaceName;
        this.consistencyLevelPolicy = consistencyLevelPolicy;
    }

    public String getKeyspaceName() {
        return keyspaceName;
    }

    public ConsistencyLevelPolicy getConsistencyLevelPolicy() {
        return consistencyLevelPolicy;
    }
}
