package net.retakethe.policyauction.data.api;

import java.util.Date;

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

}
