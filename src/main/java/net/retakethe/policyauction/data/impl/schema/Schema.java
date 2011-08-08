package net.retakethe.policyauction.data.impl.schema;

import java.util.Date;
import java.util.UUID;

import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.column.typed.StringNamedColumn;
import net.retakethe.policyauction.data.impl.schema.column.typed.StringStringColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;

import me.prettyprint.cassandra.serializers.DateSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;

/**
 * Cassandra schema elements as in cassandra-schema.txt.
 *
 * @author Nick Clarke
 */
public final class Schema {

    public static enum SchemaKeyspace {
        MAIN("policy_auction");

        private final String keyspaceName;

        private SchemaKeyspace(String keyspaceName) {
            this.keyspaceName = keyspaceName;
        }

        public String getKeyspaceName() {
            return keyspaceName;
        }
    }


    public static final PoliciesCF POLICIES = new PoliciesCF();


    public static final class PoliciesCF extends ColumnFamily<UUID, String> {

        public final NamedColumn<UUID, String, String> SHORT_NAME;
        public final NamedColumn<UUID, String, String> DESCRIPTION;
        public final NamedColumn<UUID, String, Date> LAST_EDITED;

        private PoliciesCF() {
            super(SchemaKeyspace.MAIN, "policies", UUID.class, UUIDSerializer.get(), String.class, StringSerializer.get());
            SHORT_NAME = new StringStringColumn<UUID>("short_name", this);
            DESCRIPTION = new StringStringColumn<UUID>("description", this);
            LAST_EDITED = new StringNamedColumn<UUID, Date>("last_edited", this, Date.class,
                    DateSerializer.get());
        }
    }
}
