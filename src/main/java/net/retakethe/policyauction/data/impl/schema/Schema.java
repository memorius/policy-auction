package net.retakethe.policyauction.data.impl.schema;

import java.util.Date;

import net.retakethe.policyauction.data.api.types.DateAndHour;
import net.retakethe.policyauction.data.api.types.LogMessageID;
import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.column.typed.StringNamedColumn;
import net.retakethe.policyauction.data.impl.schema.column.typed.StringStringColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.RangeSupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.SingleRowRangeColumnFamily;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SuperRangeNamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.MillisTimestamp;
import net.retakethe.policyauction.data.impl.schema.timestamp.MillisTimestampFactory;

/**
 * Cassandra schema elements as in cassandra-schema.txt.
 *
 * @author Nick Clarke
 */
public final class Schema {

    public static final PoliciesCF POLICIES = new PoliciesCF();
    
    public static final UsersCF USERS = new UsersCF();

    public static final LogHoursRow LOG_HOURS = new LogHoursRow();

    public static final LogSCF LOG = new LogSCF();


    public static final class PoliciesCF extends ColumnFamily<PolicyID, MillisTimestamp, String> {
        public final NamedColumn<PolicyID, MillisTimestamp, String, String> SHORT_NAME;
        public final NamedColumn<PolicyID, MillisTimestamp, String, String> DESCRIPTION;
        public final NamedColumn<PolicyID, MillisTimestamp, String, Date> LAST_EDITED;

        private PoliciesCF() {
            super(SchemaKeyspace.MAIN, "policies", Type.POLICY_ID, MillisTimestampFactory.get(), Type.UTF8);
            SHORT_NAME = new StringStringColumn<PolicyID, MillisTimestamp>("short_name", this);
            DESCRIPTION = new StringStringColumn<PolicyID, MillisTimestamp>("description", this);
            LAST_EDITED = new StringNamedColumn<PolicyID, MillisTimestamp, Date>("last_edited", this, Type.DATE);
        }
    }
    
    public static final class UsersCF extends ColumnFamily<UserID, MillisTimestamp, String> {
    	public final NamedColumn<UserID, MillisTimestamp, String, String> USERNAME;
    	public final NamedColumn<UserID, MillisTimestamp, String, String> EMAIL;
    	public final NamedColumn<UserID, MillisTimestamp, String, String> PASSWORD_HASH;
    	
    	public final NamedColumn<UserID, MillisTimestamp, String, String> FIRST_NAME;
    	public final NamedColumn<UserID, MillisTimestamp, String, String> LAST_NAME;
    	
    	public final NamedColumn<UserID, MillisTimestamp, String, Boolean> SHOW_REAL_NAME;
    	
    	public final NamedColumn<UserID, MillisTimestamp, String, Date> CREATED_TIMESTAMP;
    	public final NamedColumn<UserID, MillisTimestamp, String, Date> PASSWORD_EXPIRY_TIMESTAMP;
    	public final NamedColumn<UserID, MillisTimestamp, String, Date> VOTE_SALARY_LAST_PAID_TIMESTAMP;
    	public final NamedColumn<UserID, MillisTimestamp, String, Date> VOTE_SALARY_DATE;
    	
    	public final NamedColumn<UserID, MillisTimestamp, String, String> USER_ROLE;
    	
    	private UsersCF() {
			super(SchemaKeyspace.MAIN, "users", Type.USER_ID, MillisTimestampFactory.get(), Type.UTF8);
			USERNAME = new StringStringColumn<UserID, MillisTimestamp>("username", this);
			EMAIL = new StringStringColumn<UserID, MillisTimestamp>("email", this);
			PASSWORD_HASH = new StringStringColumn<UserID, MillisTimestamp>("password_hash", this);
			
			FIRST_NAME = new StringStringColumn<UserID, MillisTimestamp>("first_name", this);
			LAST_NAME = new StringStringColumn<UserID, MillisTimestamp>("last_name", this);
			
			SHOW_REAL_NAME = new StringNamedColumn<UserID, MillisTimestamp, Boolean>("show_real_name", this, Type.BOOLEAN);
			
			CREATED_TIMESTAMP = new StringNamedColumn<UserID, MillisTimestamp, Date>("created_timestamp", this, Type.DATE);
			PASSWORD_EXPIRY_TIMESTAMP = new StringNamedColumn<UserID, MillisTimestamp, Date>("password_expiry_timestamp", this, Type.DATE);
			VOTE_SALARY_LAST_PAID_TIMESTAMP = new StringNamedColumn<UserID, MillisTimestamp, Date>("vote_salary_last_paid_timestamp", this, Type.DATE);
			VOTE_SALARY_DATE = new StringNamedColumn<UserID, MillisTimestamp, Date>("vote_salary_date", this, Type.DATE);
			
			USER_ROLE = new StringStringColumn<UserID, MillisTimestamp>("user_role", this);
		}

    }

    public static final class LogHoursRow extends SingleRowRangeColumnFamily<String, MillisTimestamp, DateAndHour, Object> {
        private LogHoursRow() {
            super(SchemaKeyspace.MAIN, "misc_string", "log hours", Type.UTF8, MillisTimestampFactory.get(),
                    Type.DATE_AND_HOUR);
            setColumnRange(new ColumnRange<String, MillisTimestamp, DateAndHour, Object>(this, Type.NULL));
        }
    }

    public static final class LogSCF
            extends RangeSupercolumnFamily<DateAndHour, MillisTimestamp, LogMessageID, String, LogSCF.LogMessageRange> {

        public final class LogMessageRange extends SupercolumnRange<DateAndHour, MillisTimestamp, LogMessageID, String> {
            public final SuperRangeNamedSubcolumn<DateAndHour, MillisTimestamp, LogMessageID, String, String> LOCAL_TIME;
            public final SuperRangeNamedSubcolumn<DateAndHour, MillisTimestamp, LogMessageID, String, String> SERVER;
            public final SuperRangeNamedSubcolumn<DateAndHour, MillisTimestamp, LogMessageID, String, String> LEVEL;
            public final SuperRangeNamedSubcolumn<DateAndHour, MillisTimestamp, LogMessageID, String, String> LOGGER;
            public final SuperRangeNamedSubcolumn<DateAndHour, MillisTimestamp, LogMessageID, String, String> MESSAGE;

            private LogMessageRange() {
                super(LogSCF.this);
                LOCAL_TIME = new SuperRangeNamedSubcolumn<DateAndHour, MillisTimestamp, LogMessageID, String, String>(
                        "local_time", this, Type.UTF8);
                SERVER = new SuperRangeNamedSubcolumn<DateAndHour, MillisTimestamp, LogMessageID, String, String>(
                        "server", this, Type.UTF8);
                LEVEL = new SuperRangeNamedSubcolumn<DateAndHour, MillisTimestamp, LogMessageID, String, String>(
                        "level", this, Type.UTF8);
                LOGGER = new SuperRangeNamedSubcolumn<DateAndHour, MillisTimestamp, LogMessageID, String, String>(
                        "logger", this, Type.UTF8);
                MESSAGE = new SuperRangeNamedSubcolumn<DateAndHour, MillisTimestamp, LogMessageID, String, String>(
                        "message", this, Type.UTF8);
            }
        }

        private LogSCF() {
            super(SchemaKeyspace.LOGS, "log", Type.DATE_AND_HOUR, MillisTimestampFactory.get(),
                    Type.LOG_MESSAGE_ID, Type.UTF8);
            setSupercolumnRange(new LogMessageRange());
        }
    }
}
