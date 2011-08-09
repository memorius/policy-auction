package net.retakethe.policyauction.data.impl.schema;

import java.util.Date;
import java.util.UUID;

import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.cassandra.model.QuorumAllConsistencyLevelPolicy;
import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.HConsistencyLevel;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.column.typed.StringNamedColumn;
import net.retakethe.policyauction.data.impl.schema.column.typed.StringStringColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.SupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SuperRangeNamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.types.DateAndHour;
import net.retakethe.policyauction.data.impl.schema.types.Type;

/**
 * Cassandra schema elements as in cassandra-schema.txt.
 *
 * @author Nick Clarke
 */
public final class Schema {

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
    private static final ConsistencyLevelPolicy WRITE_QUORUM_READ_QUORUM = new QuorumAllConsistencyLevelPolicy();

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
    private static final ConsistencyLevelPolicy WRITE_ANY_READ_ONE;
    static {
        ConfigurableConsistencyLevel configurable = new ConfigurableConsistencyLevel();
        configurable.setDefaultWriteConsistencyLevel(HConsistencyLevel.ANY);
        configurable.setDefaultReadConsistencyLevel(HConsistencyLevel.ONE);
        WRITE_ANY_READ_ONE = configurable;
    }

    public static enum SchemaKeyspace {
        MAIN("policy_auction", WRITE_QUORUM_READ_QUORUM),
        LOGS("policy_auction_logs", WRITE_ANY_READ_ONE);

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


    public static final PoliciesCF POLICIES = new PoliciesCF();
    public static final LogSCF LOG = new LogSCF();


    public static final class PoliciesCF extends ColumnFamily<UUID, String> {

        public final NamedColumn<UUID, String, String> SHORT_NAME;
        public final NamedColumn<UUID, String, String> DESCRIPTION;
        public final NamedColumn<UUID, String, Date> LAST_EDITED;

        private PoliciesCF() {
            super(SchemaKeyspace.MAIN, "policies", Type.TIME_UUID, Type.UTF8);
            SHORT_NAME = new StringStringColumn<UUID>("short_name", this);
            DESCRIPTION = new StringStringColumn<UUID>("description", this);
            LAST_EDITED = new StringNamedColumn<UUID, Date>("last_edited", this, Type.DATE);
        }
    }

    public static final class LogSCF extends SupercolumnFamily<DateAndHour, UUID, String> {

        public final class LogMessage extends SupercolumnRange<DateAndHour, UUID, String> {

            public final SuperRangeNamedSubcolumn<DateAndHour, UUID, String, String> LOCAL_TIME;
            public final SuperRangeNamedSubcolumn<DateAndHour, UUID, String, String> SERVER_IP;
            public final SuperRangeNamedSubcolumn<DateAndHour, UUID, String, String> LEVEL;
            public final SuperRangeNamedSubcolumn<DateAndHour, UUID, String, String> MESSAGE;

            private LogMessage() {
                super(LogSCF.this);
                LOCAL_TIME = new SuperRangeNamedSubcolumn<DateAndHour, UUID, String, String>("local_time",
                        this, Type.UTF8);
                SERVER_IP = new SuperRangeNamedSubcolumn<DateAndHour, UUID, String, String>("server_ip",
                        this, Type.UTF8);
                LEVEL = new SuperRangeNamedSubcolumn<DateAndHour, UUID, String, String>("level",
                        this, Type.UTF8);
                MESSAGE = new SuperRangeNamedSubcolumn<DateAndHour, UUID, String, String>("message",
                        this, Type.UTF8);
            }
        }

        public final LogMessage LOG_MESSAGE;

        private LogSCF() {
            super(SchemaKeyspace.LOGS, "log", Type.DATE_AND_HOUR, Type.TIME_UUID, Type.UTF8);
            LOG_MESSAGE = new LogMessage();
        }

    }
}
