package net.retakethe.policyauction.data.impl.manager;

import java.util.Date;

import net.retakethe.policyauction.data.api.SystemInfoManager;
import net.retakethe.policyauction.data.impl.query.api.ColumnResult;
import net.retakethe.policyauction.data.impl.query.api.Mutator;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.data.impl.schema.Schema.SystemInfoRow;
import net.retakethe.policyauction.data.impl.schema.timestamp.MillisTimestamp;

import org.joda.time.LocalDate;

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

    @Override
    public void setVoteSalaryLastPaid(LocalDate lastPaid) {
        SystemInfoRow cf = Schema.SYSTEM_INFO;
        Mutator<String, MillisTimestamp> m = cf.createMutator(getKeyspaceManager());
        cf.addColumnInsertion(m, cf.VOTE_SALARY_LAST_PAID, cf.createValue(lastPaid));
        m.execute();
    }
}
