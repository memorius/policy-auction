package net.retakethe.policyauction.data.impl.schema;

import java.util.Date;

import net.retakethe.policyauction.data.api.types.DateAndHour;
import net.retakethe.policyauction.data.api.types.LogMessageID;
import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.column.typed.StringNamedColumn;
import net.retakethe.policyauction.data.impl.schema.column.typed.StringStringColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.RangeSupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.SingleRowRangeColumnFamily;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SuperRangeNamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.MillisecondsTimestamp;
import net.retakethe.policyauction.data.impl.schema.timestamp.MillisecondsTimestampFactory;

/**
 * Cassandra schema elements as in cassandra-schema.txt.
 *
 * @author Nick Clarke
 */
public final class Schema {

    public static final PoliciesCF POLICIES = new PoliciesCF();

    public static final LogHoursRow LOG_HOURS = new LogHoursRow();

    public static final LogSCF LOG = new LogSCF();


    public static final class PoliciesCF extends ColumnFamily<PolicyID, MillisecondsTimestamp, String> {
        public final NamedColumn<PolicyID, MillisecondsTimestamp, String, String> SHORT_NAME;
        public final NamedColumn<PolicyID, MillisecondsTimestamp, String, String> DESCRIPTION;
        public final NamedColumn<PolicyID, MillisecondsTimestamp, String, Date> LAST_EDITED;

        private PoliciesCF() {
            super(SchemaKeyspace.MAIN, "policies", Type.POLICY_ID, MillisecondsTimestampFactory.get(), Type.UTF8);
            SHORT_NAME = new StringStringColumn<PolicyID, MillisecondsTimestamp>("short_name", this);
            DESCRIPTION = new StringStringColumn<PolicyID, MillisecondsTimestamp>("description", this);
            LAST_EDITED = new StringNamedColumn<PolicyID, MillisecondsTimestamp, Date>("last_edited", this, Type.DATE);
        }
    }

    public static final class LogHoursRow extends SingleRowRangeColumnFamily<String, MillisecondsTimestamp, DateAndHour, Object> {
        private LogHoursRow() {
            super(SchemaKeyspace.MAIN, "misc_string", "log hours", Type.UTF8, MillisecondsTimestampFactory.get(),
                    Type.DATE_AND_HOUR);
            setColumnRange(new ColumnRange<String, MillisecondsTimestamp, DateAndHour, Object>(this, Type.NULL));
        }
    }

    public static final class LogSCF
            extends RangeSupercolumnFamily<DateAndHour, MillisecondsTimestamp, LogMessageID, String, LogSCF.LogMessageRange> {

        public final class LogMessageRange extends SupercolumnRange<DateAndHour, MillisecondsTimestamp, LogMessageID, String> {
            public final SuperRangeNamedSubcolumn<DateAndHour, MillisecondsTimestamp, LogMessageID, String, String> LOCAL_TIME;
            public final SuperRangeNamedSubcolumn<DateAndHour, MillisecondsTimestamp, LogMessageID, String, String> SERVER;
            public final SuperRangeNamedSubcolumn<DateAndHour, MillisecondsTimestamp, LogMessageID, String, String> LEVEL;
            public final SuperRangeNamedSubcolumn<DateAndHour, MillisecondsTimestamp, LogMessageID, String, String> LOGGER;
            public final SuperRangeNamedSubcolumn<DateAndHour, MillisecondsTimestamp, LogMessageID, String, String> MESSAGE;

            private LogMessageRange() {
                super(LogSCF.this);
                LOCAL_TIME = new SuperRangeNamedSubcolumn<DateAndHour, MillisecondsTimestamp, LogMessageID, String, String>(
                        "local_time", this, Type.UTF8);
                SERVER = new SuperRangeNamedSubcolumn<DateAndHour, MillisecondsTimestamp, LogMessageID, String, String>(
                        "server", this, Type.UTF8);
                LEVEL = new SuperRangeNamedSubcolumn<DateAndHour, MillisecondsTimestamp, LogMessageID, String, String>(
                        "level", this, Type.UTF8);
                LOGGER = new SuperRangeNamedSubcolumn<DateAndHour, MillisecondsTimestamp, LogMessageID, String, String>(
                        "logger", this, Type.UTF8);
                MESSAGE = new SuperRangeNamedSubcolumn<DateAndHour, MillisecondsTimestamp, LogMessageID, String, String>(
                        "message", this, Type.UTF8);
            }
        }

        private LogSCF() {
            super(SchemaKeyspace.LOGS, "log", Type.DATE_AND_HOUR, MillisecondsTimestampFactory.get(),
                    Type.LOG_MESSAGE_ID, Type.UTF8);
            setSupercolumnRange(new LogMessageRange());
        }
    }
}
