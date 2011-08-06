package net.retakethe.policyauction.data.impl.schema;

import java.util.Date;
import java.util.UUID;

import me.prettyprint.cassandra.serializers.DateSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;

/**
 * Cassandra schema elements as in cassandra-schema.txt.
 *
 * @author Nick Clarke
 */
public final class Schema {

    public static final PoliciesCF POLICIES = new PoliciesCF();


    public static final class PoliciesCF extends ColumnFamily<UUID> {

        public final Column<UUID, String, String> SHORT_NAME;
        public final Column<UUID, String, String> DESCRIPTION;
        public final Column<UUID, String, Date> LAST_EDITED;

        private PoliciesCF() {
            super("policies", UUID.class, UUIDSerializer.get());
            SHORT_NAME = new StringStringColumn<UUID>("short_name", getKeyType(), this);
            DESCRIPTION = new StringStringColumn<UUID>("description", getKeyType(), this);
            LAST_EDITED = new StringNamedColumn<UUID, Date>("last_edited", getKeyType(), this,
                    Date.class, DateSerializer.get());
        }
    }
}
