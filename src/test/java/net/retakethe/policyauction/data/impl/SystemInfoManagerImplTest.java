package net.retakethe.policyauction.data.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.util.Date;

import net.retakethe.policyauction.data.impl.manager.SystemInfoManagerImpl;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import _fixtures.CleanDbEveryMethodDAOManagerTestBase;

/**
 * @author Nick Clarke
 */
public class SystemInfoManagerImplTest extends CleanDbEveryMethodDAOManagerTestBase {

    private SystemInfoManagerImpl manager;

    @BeforeMethod(groups = {"dao"})
    public void setupManager() {
        manager = getDAOManager().getSystemInfoManager();
    }

    @Test(groups = {"dao"})
    public void testFirstStartupTime() throws InterruptedException {
        Date firstStartupTime = manager.getFirstStartupTime();
        Date now = new Date();
        assertNotNull(firstStartupTime);
        assertFalse(now.before(firstStartupTime));
        // Wait until millis value changes
        while (!now.before(new Date())) {
            Thread.sleep(1);
        }
        // Make sure we still get the original value
        assertEquals(manager.getFirstStartupTime(), firstStartupTime);
    }
}
