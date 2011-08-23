package net.retakethe.policyauction.data.api;

import java.util.Date;

import org.joda.time.LocalDate;

/**
 * Various runtime values for the whole system.
 *
 * @author Nick Clarke
 */
public interface SystemInfoManager {

    /**
     * Get date+time when the application database was first accessed
     *
     * @return non-null value
     */
    Date getFirstStartupTime();

    /**
     * 
     * @return null if no pay run yet
     */
    LocalDate getVoteSalaryLastPaid();

    /**
     * @param lastPaid must not be null
     */
    void setVoteSalaryLastPaid(LocalDate lastPaid);

}
