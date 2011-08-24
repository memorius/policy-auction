package net.retakethe.policyauction.data.impl.manager;

import java.util.Date;

import net.retakethe.policyauction.data.api.SystemInfoManager;
import net.retakethe.policyauction.data.impl.schema.Schema;

/**
 * @author Nick Clarke
 */
public class SystemInfoManagerImpl extends AbstractDAOManagerImpl implements SystemInfoManager {

    public SystemInfoManagerImpl(KeyspaceManagerImpl keyspaceManager) {
        super(keyspaceManager);

        // Set the initial startup time the first time any node tries to access Cassandra
        getFirstStartupTime();
    }

    @Override
    public Date getFirstStartupTime() {
        // TODO: cache value in memory for a while
        return readFirstStartupTime();
    }

    private Date readFirstStartupTime() {
        return Schema.SYSTEM_INFO.FIRST_STARTUP.getColumnValueOrSetDefault(getKeyspaceManager());
    }
}
