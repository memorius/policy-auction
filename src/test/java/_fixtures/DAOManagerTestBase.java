package _fixtures;

import java.util.List;

import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import net.retakethe.policyauction.data.impl.manager.DAOManagerImpl;
import net.retakethe.policyauction.data.impl.query.QueryFactory;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.Mutator;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.family.SingleRowColumnFamily;
import net.retakethe.policyauction.data.impl.schema.timestamp.Timestamp;
import net.retakethe.policyauction.data.impl.serializers.DummySerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Base class for all cassandra DAOManager tests.
 *
 * @author Nick Clarke
 */
public abstract class DAOManagerTestBase {

    private static final Logger logger = LoggerFactory.getLogger(DAOManagerTestBase.class);

    private boolean runningInTestSuite = false;
    private DAOManagerImpl daoManager;

    @BeforeClass(groups = {"dao"})
    public void setupCassandraAndDAOManager() {
        logger.info("DAOManagerTestBase.setupCassandraAndDAOManager starting");
        runningInTestSuite = TestCassandraManager.isInitialized();
        if (!runningInTestSuite) {
            // Standalone execution of only this test class, e.g. from Eclipse plugin.
            // We must trigger TestCassandra init ourselves.
            TestCassandraManager.setupCassandra();
        }
        // else we're running ALL the tests, and TestCassandraManager is run as a test class,
        // so its @BeforeSuite method is called before the first test.

        daoManager = new DAOManagerImpl("localhost", TestCassandraManager.getCassandraRpcPort());
        logger.info("DAOManagerTestBase.setupCassandraAndDAOManager finished");
    }

    @AfterClass(groups = {"dao"})
    public void teardownDAOManagerAndCassandra() {
        logger.info("DAOManagerTestBase.teardownDAOManagerAndCassandra starting");
        daoManager.destroy();
        daoManager = null;

        if (!runningInTestSuite) {
            // Standalone execution of only this test class, e.g. from Eclipse plugin.
            // We must trigger TestCassandra teardown ourselves.
            TestCassandraManager.teardownCassandra();
        }
        // else we're running ALL the tests, and TestCassandraManager is run as a test class,
        // so its @AfterSuite method is called after the last test.

        logger.info("DAOManagerTestBase.teardownDAOManagerAndCassandra finished");
    }

    protected DAOManagerImpl getDAOManager() {
        return daoManager;
    }

    /**
     * Delete contents of all column families.
     * <p>
     * No annotation - BeforeMethod/BeforeClass scope is controlled by subclasses explicitly calling this method
     */
    protected void cleanCassandraDB() {
        logger.info("DAOManagerTestBase.cleanCassandraDB starting");
        KeyspaceManager keyspaceManager = daoManager.getKeyspaceManager();

        cleanColumnFamily(keyspaceManager, Schema.POLICIES);
        cleanColumnFamily(keyspaceManager, Schema.USERS);
        cleanColumnFamily(keyspaceManager, Schema.USER_VOTES);
        cleanColumnFamily(keyspaceManager, Schema.USER_VOTES_PENDING);
        cleanColumnFamily(keyspaceManager, Schema.VOTE_SALARY);
        cleanColumnFamily(keyspaceManager, Schema.VOTING_CONFIG);
        cleanColumnFamily(keyspaceManager, Schema.SYSTEM_INFO);

        logger.info("DAOManagerTestBase.cleanCassandraDB finished");
    }

    private <K, T extends Timestamp, N> void cleanColumnFamily(KeyspaceManager keyspaceManager,
            ColumnFamily<K, T, N> cf) {

        // If our schema uses single rows in this CF, the other rows in the 'real' CF may have different data types,
        // so must be handled separately - if we retrieve them all we'll get errors on column name deserialization,
        // and also we'll duplicate a lot of work. Just explicitly delete the single row.
        if (cf instanceof SingleRowColumnFamily<?>) {
            @SuppressWarnings("unchecked")
            SingleRowColumnFamily<K> srcf = (SingleRowColumnFamily<K>) cf;
            K key = srcf.getKey();
            Mutator<K, T> m = cf.createMutator(keyspaceManager);
            cf.addRowDeletion(m, key);
            m.execute();
            return;
        }

        // Multiple rows - query to get the keys for any existing rows, then delete them.
        while (true) {
            logger.info("DAOManagerTestBase.cleanCassandraDB starting cycle for " + cf.getName());
            // Query a batch of keys in this column family.

            RangeSlicesQuery<K, N, Object> query = QueryFactory.createHectorRangeSlicesQuery(
                    keyspaceManager, cf, DummySerializer.get(), null, null, false, 1);

            query.setRowCount(100000);
            QueryResult<OrderedRows<K, N, Object>> result = query.execute();
            List<Row<K, N, Object>> rows = result.get().getList();
            logger.info("DAOManagerTestBase.cleanCassandraDB: rows: " + rows.size());
            if (rows.size() == 0) {
                break;
            }

            // Delete the rows for these keys
            Mutator<K, T> m = cf.createMutator(keyspaceManager);
            boolean rowsExist = false;
            for (Row<K, N, Object> row : rows) {
                logger.info("DAOManagerTestBase.cleanCassandraDB: key: " + row.getKey());
                if (row.getColumnSlice().getColumns().isEmpty()) {
                    // This is a previously-deleted row.
                    // They reappear (with empty contents) in range slices query results until garbage-collected.
                    // See Cassandra FAQ: http://wiki.apache.org/cassandra/FAQ#range_ghosts
                    logger.info("DAOManagerTestBase.cleanCassandraDB: tombstone row");
                    continue;
                }
                logger.info("DAOManagerTestBase.cleanCassandraDB: row exists");
                cf.addRowDeletion(m, row.getKey());
                rowsExist = true;
            }
            if (!rowsExist) { 
                logger.info("DAOManagerTestBase.cleanCassandraDB: no more rows");
                break;
            }
            m.execute();
            logger.info("DAOManagerTestBase.cleanCassandraDB ended cycle");
        }
        logger.info("DAOManagerTestBase.cleanCassandraDB finished for " + cf.getName());
    }
}
