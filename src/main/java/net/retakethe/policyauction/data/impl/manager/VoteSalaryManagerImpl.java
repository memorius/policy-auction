package net.retakethe.policyauction.data.impl.manager;

import java.util.List;

import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.api.SystemInfoManager;
import net.retakethe.policyauction.data.api.VoteSalaryManager;
import net.retakethe.policyauction.data.api.VotingConfigManager;
import net.retakethe.policyauction.data.api.dao.VoteSalaryPayment;
import net.retakethe.policyauction.data.api.exceptions.NoSuchUserException;
import net.retakethe.policyauction.data.api.types.DayOfWeek;
import net.retakethe.policyauction.data.api.types.UserID;
import net.retakethe.policyauction.data.impl.dao.VoteSalaryPaymentImpl;
import net.retakethe.policyauction.data.impl.query.api.ColumnResult;
import net.retakethe.policyauction.data.impl.query.api.ColumnSlice;
import net.retakethe.policyauction.data.impl.query.api.Mutator;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.data.impl.schema.Schema.SystemInfoRow;
import net.retakethe.policyauction.data.impl.schema.Schema.VoteSalaryRow;
import net.retakethe.policyauction.data.impl.schema.column.ColumnRange;
import net.retakethe.policyauction.data.impl.schema.timestamp.MillisTimestamp;
import net.retakethe.policyauction.util.Functional;

import org.joda.time.LocalDate;

/**
 * @author Nick Clarke
 */
public class VoteSalaryManagerImpl extends AbstractDAOManagerImpl implements VoteSalaryManager {

    private final SystemInfoManager systemInfoManager;
    private final VotingConfigManager votingConfigManager;

    public VoteSalaryManagerImpl(KeyspaceManagerImpl keyspaceManager,
            SystemInfoManager systemInfoManager,
            VotingConfigManager votingConfigManager) {
        super(keyspaceManager);
        this.systemInfoManager = systemInfoManager;
        this.votingConfigManager = votingConfigManager;
    }

    @Override
    public LocalDate getVoteSalaryLastPaid() {
        SystemInfoRow cf = Schema.SYSTEM_INFO;
        ColumnResult<MillisTimestamp, String, LocalDate> result =
            cf.createColumnQuery(getKeyspaceManager(), cf.VOTE_SALARY_LAST_PAID).execute().get();
        if (result == null) {
            return null;
        }
        return result.getValue().getValue();
    }

    /**
     * @param lastPaid must not be null
     */
    private void setVoteSalaryLastPaid(LocalDate lastPaid) {
        SystemInfoRow cf = Schema.SYSTEM_INFO;
        Mutator<String, MillisTimestamp> m = cf.createMutator(getKeyspaceManager());
        cf.addColumnInsertion(m, cf.VOTE_SALARY_LAST_PAID, cf.createValue(lastPaid));
        m.execute();
    }

    @Override
    public List<VoteSalaryPayment> getUserVoteSalaryHistory(UserID userID) throws NoSuchUserException {
        return getSalaryRecordsSince(getUserRegistrationDate(userID));
    }

    @Override
    public List<VoteSalaryPayment> getSystemWideVoteSalaryHistory() {
        return getSalaryRecordsSince(new LocalDate(systemInfoManager.getFirstStartupTime()));
    }

    private LocalDate getUserRegistrationDate(UserID userID) throws NoSuchUserException {
        // FIXME: temporary hack until I have Matt's user reg manager: all users get all votes regardless of reg date.
        //        Need to read real reg date from user CF, throw NoSuchUserException if not found. 
        return new LocalDate(systemInfoManager.getFirstStartupTime());
    }

    private List<VoteSalaryPayment> getSalaryRecordsSince(LocalDate startDate) {
        LocalDate today = LocalDate.now();

        createSalaryRecordsIfNeeded(today);

        return readVoteSalary(startDate, today);
    }

    private void createSalaryRecordsIfNeeded(LocalDate today) {
        LocalDate lastPay = getVoteSalaryLastPaid();
        if (lastPay != null && !lastPay.isBefore(today)) {
            // Already updated today (or, well, in the future, like)
            return;
        }

        LocalDate nextPay = getNextPay(lastPay, today);
        if (nextPay == null) {
            // Not due yet
            return;
        }

        // Write any missing records
        do {
            writeVoteSalaryColumn(nextPay, votingConfigManager.getUserVoteSalaryIncrement());
            setVoteSalaryLastPaid(nextPay);
            lastPay = nextPay;
            nextPay = getNextPay(lastPay, today);
        } while (nextPay != null);
    }

    private LocalDate getNextPay(LocalDate lastPay, LocalDate today) {
        if (lastPay == null) {
            // First payment is always on first startup day, even if weekly/specific-day frequency is set and it's
            // out of phase, because it simplifies range retrievals. The next payment will be on the specified day.
            return new LocalDate(systemInfoManager.getFirstStartupTime());
        }

        LocalDate nextPay;
        final short frequencyDays = votingConfigManager.getUserVoteSalaryFrequencyDays();
        if (7 == frequencyDays) {
            // Weekly frequency is handled differently so that all payments except the first-startup one occur on
            // the specified day of the week, regardless of first-startup day-of-week or day of frequency change.
            // If we're out of phase (e.g. changed the pay-day-of-week since last pay, or changed to weekly from 
            // some other frequency), step back so we have a short week this week, rather than a long week.
            DayOfWeek dayOfWeek = votingConfigManager.getUserVoteSalaryWeeklyDayOfWeek();
            while (DayOfWeek.fromLocalDate(lastPay) != dayOfWeek) {
                lastPay = lastPay.minusDays(1);
            }
            nextPay = lastPay.plusDays(frequencyDays);
        } else {
            // Non-weekly frequency
            nextPay = lastPay.plusDays(frequencyDays);
        }

        // Return only if it's actually due yet
        return nextPay.isAfter(today) ? null : nextPay;
    }

    /**
     * @param startDate inclusive
     * @param endDate inclusive
     */
    private List<VoteSalaryPayment> readVoteSalary(LocalDate startDate, LocalDate endDate) {
        VoteSalaryRow cf = Schema.VOTE_SALARY;
        ColumnRange<String, MillisTimestamp, LocalDate, Long> columnRange = cf.getColumnRange();

        QueryResult<ColumnSlice<MillisTimestamp, LocalDate>> result =
                cf.createSliceQuery(getKeyspaceManager(), startDate, endDate, false, Integer.MAX_VALUE).execute();
        List<ColumnResult<MillisTimestamp, LocalDate, Long>> columns = result.get().getColumns(columnRange);

        List<VoteSalaryPayment> salary = toSalaryRecords(columns);

        // Find one more record backwards from the start date, if there are any.
        // This is so that if you start ON a salary day, you receive that day's salary;
        // if you start between days, you receive salary from the previous cycle.
        // To avoid reading ALL records every time, we have to do a second read (or in the worst case, several)
        // since we don't know what the salary frequency or phase was at the startDate.

        endDate = startDate.minusDays(1);
        startDate = startDate.minusMonths(1);

        // Oldest possible salary payment date.
        final LocalDate earliest = new LocalDate(systemInfoManager.getFirstStartupTime());

        while (earliest.isBefore(endDate)) {
            result = cf.createSliceQuery(getKeyspaceManager(), startDate, endDate, true, Integer.MAX_VALUE).execute();
            columns = result.get().getColumns(columnRange);

            if (columns.isEmpty()) {
                // No records in this interval - fetch another chunk
                endDate = startDate.minusDays(1);
                startDate = startDate.minusMonths(1);
                continue;
            }

            // Results in reversed order - this is the most recent
            salary.add(0, makeSalaryRecord(columns.get(0)));
            break;
        }

        return salary;
    }

    private VoteSalaryPayment makeSalaryRecord(ColumnResult<MillisTimestamp, LocalDate, Long> columnResult) {
        return new VoteSalaryPaymentImpl(columnResult.getName(), columnResult.getValue().getValue());
    }

    private List<VoteSalaryPayment> toSalaryRecords(List<ColumnResult<MillisTimestamp, LocalDate, Long>> columns) {
        return Functional.map(columns,
                new Functional.Converter<ColumnResult<MillisTimestamp, LocalDate, Long>, VoteSalaryPayment>() {
                    @Override
                    public VoteSalaryPayment convert(ColumnResult<MillisTimestamp, LocalDate, Long> columnResult) {
                        return makeSalaryRecord(columnResult);
                    }
                });
    }

    private void writeVoteSalaryColumn(LocalDate date, long salaryIncrement) {
        VoteSalaryRow cf = Schema.VOTE_SALARY;
        Mutator<String, MillisTimestamp> m = cf.createMutator(getKeyspaceManager());
        cf.addColumnInsertion(m, date, cf.createValue(salaryIncrement));
        m.execute();
    }
}
