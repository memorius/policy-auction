package net.retakethe.policyauction.data.impl.schema;

import java.util.Date;

import net.retakethe.policyauction.data.api.types.DateAndHour;
import net.retakethe.policyauction.data.api.types.DayOfWeek;
import net.retakethe.policyauction.data.api.types.LogMessageID;
import net.retakethe.policyauction.data.api.types.PolicyID;
import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.column.DefaultValuedNamedColumn;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.column.typed.StringNamedColumn;
import net.retakethe.policyauction.data.impl.schema.column.typed.StringStringColumn;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.RangeColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.RangeSupercolumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.SingleRowNamedColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.SingleRowRangeColumnFamily;
import net.retakethe.policyauction.data.impl.schema.subcolumn.SuperRangeNamedSubcolumn;
import net.retakethe.policyauction.data.impl.schema.supercolumn.SupercolumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.MillisTimestamp;
import net.retakethe.policyauction.data.impl.schema.timestamp.MillisTimestampFactory;
import net.retakethe.policyauction.data.impl.schema.timestamp.UniqueTimestamp;
import net.retakethe.policyauction.data.impl.schema.timestamp.UniqueTimestampFactory;
import net.retakethe.policyauction.data.impl.types.internal.VoteRecordID;

import org.apache.tapestry5.json.JSONObject;
import org.joda.time.LocalDate;

/**
 * Cassandra schema elements as in cassandra-schema.txt.
 *
 * @author Nick Clarke
 */
public final class Schema {

    public static final PoliciesCF POLICIES = new PoliciesCF();
    
    public static final UsersCF USERS = new UsersCF();

    public static final UserVotesCF USER_VOTES_PENDING = new UserVotesCF("user_policy_votes_pending");

    public static final UserVotesCF USER_VOTES = new UserVotesCF("user_policy_votes");

    public static final VoteSalaryRow VOTE_SALARY = new VoteSalaryRow();

    public static final VotingConfigRow VOTING_CONFIG = new VotingConfigRow();

    public static final SystemInfoRow SYSTEM_INFO = new SystemInfoRow();

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

    public static final class UserVotesCF extends RangeColumnFamily<UserID, UniqueTimestamp, VoteRecordID, JSONObject> {
        private UserVotesCF(String columnFamilyName) {
            super(SchemaKeyspace.MAIN, columnFamilyName, Type.USER_ID, UniqueTimestampFactory.get(),
                    Type.VOTE_RECORD_ID);
            setColumnRange(new ColumnRange<UserID, UniqueTimestamp, VoteRecordID, JSONObject>(this, Type.JSON));
        }
    }

    public static final class VoteSalaryRow extends SingleRowRangeColumnFamily<String, MillisTimestamp, LocalDate, Long> {
        private VoteSalaryRow() {
            super(SchemaKeyspace.MAIN, "memcache_string", "vote-salary", Type.UTF8, MillisTimestampFactory.get(),
                    Type.DAY);
            setColumnRange(new ColumnRange<String, MillisTimestamp, LocalDate, Long>(this, Type.LONG));
        }
    }

    public static final class VotingConfigRow extends SingleRowNamedColumnFamily<String, MillisTimestamp, String> {
        public final DefaultValuedNamedColumn<String, MillisTimestamp, String, Byte> VOTE_WITHDRAWAL_PENALTY_PERCENTAGE;
        public final DefaultValuedNamedColumn<String, MillisTimestamp, String, Long> VOTE_COST_TO_CREATE_POLICY;
        public final DefaultValuedNamedColumn<String, MillisTimestamp, String, Long> USER_VOTE_SALARY_INCREMENT;
        public final DefaultValuedNamedColumn<String, MillisTimestamp, String, Short> USER_VOTE_SALARY_FREQUENCY_DAYS;
        public final DefaultValuedNamedColumn<String, MillisTimestamp, String, DayOfWeek> USER_VOTE_SALARY_WEEKLY_DAY_OF_WEEK;

        private VotingConfigRow() {
            super(SchemaKeyspace.MAIN, "memcache_string", "voting-config", Type.UTF8, MillisTimestampFactory.get(),
                    Type.UTF8);
            VOTE_WITHDRAWAL_PENALTY_PERCENTAGE = new DefaultValuedNamedColumn<String, MillisTimestamp, String, Byte>(
                    "vote_withdrawal_penalty_percentage", this, Type.BYTE, (byte) 40); 
            VOTE_COST_TO_CREATE_POLICY = new DefaultValuedNamedColumn<String, MillisTimestamp, String, Long>(
                    "vote_cost_to_create_policy", this, Type.LONG, 100L);
            USER_VOTE_SALARY_INCREMENT = new DefaultValuedNamedColumn<String, MillisTimestamp, String, Long>(
                    "user_vote_salary_increment", this, Type.LONG, 100L);
            USER_VOTE_SALARY_FREQUENCY_DAYS = new DefaultValuedNamedColumn<String, MillisTimestamp, String, Short>(
                    "user_vote_salary_frequency_days", this, Type.SHORT, (short) 7);
            USER_VOTE_SALARY_WEEKLY_DAY_OF_WEEK = new DefaultValuedNamedColumn<String, MillisTimestamp, String, DayOfWeek>(
                    "user_vote_salary_weekly_day_of_week", this, Type.DAY_OF_WEEK, DayOfWeek.MONDAY);
        }
    }

    public static final class SystemInfoRow extends SingleRowNamedColumnFamily<String, MillisTimestamp, String> {
        public final DefaultValuedNamedColumn<String, MillisTimestamp, String, Date> FIRST_STARTUP;
        public final NamedColumn<String, MillisTimestamp, String, LocalDate> VOTE_SALARY_LAST_PAID;

        private SystemInfoRow() {
            super(SchemaKeyspace.MAIN, "memcache_string", "system-info", Type.UTF8, MillisTimestampFactory.get(),
                    Type.UTF8);
            FIRST_STARTUP = new DefaultValuedNamedColumn<String, MillisTimestamp, String, Date>(
                    "first_startup_time", this, Type.DATE) {
                @Override
                protected Date getDefaultValue() {
                    // Set current date when it's first read
                    return new Date();
                }
            };
            VOTE_SALARY_LAST_PAID = new NamedColumn<String, MillisTimestamp, String, LocalDate>(
                    "vote_salary_last_paid", this, Type.DAY);
        }
    }

    public static final class LogHoursRow extends SingleRowRangeColumnFamily<String, MillisTimestamp, DateAndHour, Object> {
        private LogHoursRow() {
            super(SchemaKeyspace.MAIN, "misc_string", "log-hours", Type.UTF8, MillisTimestampFactory.get(),
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
