package _fixtures;

import java.util.List;

import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import net.retakethe.policyauction.data.impl.HectorDAOManagerImpl;
import net.retakethe.policyauction.data.impl.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.MutatorWrapper;
import net.retakethe.policyauction.data.impl.query.impl.DummySerializer;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.data.impl.schema.family.ColumnFamily;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Base class for all cassandra DAOManager tests.
 *
 * @author Nick Clarke
 */
public abstract class HectorDAOTestBase {

    private static final Logger logger = LoggerFactory.getLogger(HectorDAOTestBase.class);

    private boolean runningInTestSuite = false;
    private HectorDAOManagerImpl daoManager;

    @BeforeClass(groups = {"dao"})
    public void setupCassandraAndDAOManager() {
        logger.info("HectorDAOTestBase.setupCassandraAndDAOManager starting");
        runningInTestSuite = TestCassandraManager.isInitialized();
        if (!runningInTestSuite) {
            // Standalone execution of only this test class, e.g. from Eclipse plugin.
            // We must trigger TestCassandra init ourselves.
            TestCassandraManager.setupCassandra();
        }
        // else we're running ALL the tests, and TestCassandraManager is run as a test class,
        // so its @BeforeSuite method is called before the first test.

        daoManager = new HectorDAOManagerImpl("localhost", TestCassandraManager.getCassandraRpcPort());
        logger.info("HectorDAOTestBase.setupCassandraAndDAOManager finished");
    }

    @AfterClass(groups = {"dao"})
    public void teardownDAOManagerAndCassandra() {
        logger.info("HectorDAOTestBase.teardownDAOManagerAndCassandra starting");
        daoManager.destroy();
        daoManager = null;

        if (!runningInTestSuite) {
            // Standalone execution of only this test class, e.g. from Eclipse plugin.
            // We must trigger TestCassandra teardown ourselves.
            TestCassandraManager.teardownCassandra();
        }
        // else we're running ALL the tests, and TestCassandraManager is run as a test class,
        // so its @AfterSuite method is called after the last test.

        logger.info("HectorDAOTestBase.teardownDAOManagerAndCassandra finished");
    }

    protected HectorDAOManagerImpl getDAOManager() {
        return daoManager;
    }

    /**
     * Delete contents of all column families.
     * <p>
     * No annotation - BeforeMethod/BeforeClass scope is controlled by subclasses explicitly calling this method
     */
    protected void cleanCassandraDB() {
        logger.info("HectorDAOTestBase.cleanCassandraDB starting");
        KeyspaceManager keyspaceManager = daoManager.getKeyspaceManager();

        cleanColumnFamily(keyspaceManager, Schema.POLICIES);
        logger.info("HectorDAOTestBase.cleanCassandraDB finished");
    }

    private <K, N> void cleanColumnFamily(KeyspaceManager keyspaceManager, ColumnFamily<K, N> cf) {
        while (true) {
            logger.info("HectorDAOTestBase.cleanCassandraDB starting cycle for " + cf.getName());
            // Query a batch of keys in this column family.
            // Use of the common-to-all-CFs "EXISTS" column allows us to omit tombstone rows:
            // they will be present in the result but will lack this column.

            RangeSlicesQuery<K, N, Object> query = cf.createRangeSlicesQuery(keyspaceManager, DummySerializer.get(),
                    null, null, false, 1);

            query.setRowCount(100000);
            QueryResult<OrderedRows<K, N, Object>> result = query.execute();
            List<Row<K, N, Object>> rows = result.get().getList();
            logger.info("HectorDAOTestBase.cleanCassandraDB: rows: " + rows.size());
            if (rows.size() == 0) {
                break;
            }

            // Delete the rows for these keys
            MutatorWrapper<K> m = cf.createMutator(keyspaceManager);
            boolean rowsExist = false;
            for (Row<K, N, Object> row : rows) {
                logger.info("HectorDAOTestBase.cleanCassandraDB: key: " + row.getKey());
                if (row.getColumnSlice().getColumns().isEmpty()) {
                    logger.info("HectorDAOTestBase.cleanCassandraDB: tombstone row");
                    continue;
                }
                logger.info("HectorDAOTestBase.cleanCassandraDB: row exists");
                cf.addRowDeletion(m, row.getKey());
                rowsExist = true;
            }
            if (!rowsExist) { 
                logger.info("HectorDAOTestBase.cleanCassandraDB: no more rows");
                break;
            }
            m.execute();
            logger.info("HectorDAOTestBase.cleanCassandraDB ended cycle");
        }
        logger.info("HectorDAOTestBase.cleanCassandraDB finished for " + cf.getName());
    }
}
