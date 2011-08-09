package net.retakethe.policyauction.data.impl.schema;

import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.cassandra.model.QuorumAllConsistencyLevelPolicy;
import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.HConsistencyLevel;

/**
 * @author Nick Clarke
 */
public final class ConsistencyLevel {

    /**
     * Quorum for both reads and writes - strongly consistent, moderate speed.
     * <p>
     * A successful write followed by a successful read will always see the newly-written data.
     * Successfully-written data will not be lost unless a majority of nodes fail simultaneously.
     * <p>
     * Note that a failed write followed by a successful read may still return stale data:
     * the write which returned a failure to the client may still have made it to a node which will recover later.
     * <p>
     * This is used for all important data in the system.
     */
    public static final ConsistencyLevelPolicy WRITE_QUORUM_READ_QUORUM = new QuorumAllConsistencyLevelPolicy();

    /**
     * Fast but unreliable writes. Readers may not see their written data.
     * <p>
     * A successful write followed by a successful read will return stale data most of the time.
     * In many cases the up-to-date data will be returned by the next read after the stale one,
     * since it propagates by read repair, but it could take longer.
     * Successfully-written data may be lost if a single node dies.
     * <p>
     * This is for low-importance data that we don't mind losing or getting stale reads for:
     * e.g. debugging and performance logs.
     */
    public static final ConsistencyLevelPolicy WRITE_ANY_READ_ONE;
    static {
        ConfigurableConsistencyLevel configurable = new ConfigurableConsistencyLevel();
        configurable.setDefaultWriteConsistencyLevel(HConsistencyLevel.ANY);
        configurable.setDefaultReadConsistencyLevel(HConsistencyLevel.ONE);
        WRITE_ANY_READ_ONE = configurable;
    }
}
