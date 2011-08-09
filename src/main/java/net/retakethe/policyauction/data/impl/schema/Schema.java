package net.retakethe.policyauction.data.impl.schema;

import java.util.Date;
import java.util.UUID;

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
