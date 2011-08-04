package _fixtures;

import java.util.List;

import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import net.retakethe.policyauction.data.impl.HectorDAOManagerImpl;
import net.retakethe.policyauction.data.impl.schema.ColumnFamily;
import net.retakethe.policyauction.data.impl.schema.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Base class for all cassandra DAOManager tests.
 *
 * @author Nick Clarke
 */
public abstract class HectorTestBase {

    private static final Logger logger = LoggerFactory.getLogger(HectorTestBase.class);

    private boolean runningInTestSuite = false;
    private HectorDAOManagerImpl daoManager;

    @BeforeClass
    public void setupCassandraAndDAOManager() {
        logger.info("HectorTestBase.setupCassandraAndDAOManager starting");
        runningInTestSuite = TestCassandraManager.isInitialized();
        if (!runningInTestSuite) {
            // Standalone execution of only this test class, e.g. from Eclipse plugin.
            // We must trigger TestCassandra init ourselves.
            TestCassandraManager.setupCassandra();
        }
        // else we're running ALL the tests, and TestCassandraManager is run as a test class,
        // so its @BeforeSuite method is called before the first test.

        daoManager = new HectorDAOManagerImpl("localhost", TestCassandraManager.getCassandraRpcPort());
        logger.info("HectorTestBase.setupCassandraAndDAOManager finished");
    }

    @AfterClass
    public void teardownDAOManagerAndCassandra() {
        logger.info("HectorTestBase.teardownDAOManagerAndCassandra starting");
        daoManager.destroy();
        daoManager = null;

        if (!runningInTestSuite) {
            // Standalone execution of only this test class, e.g. from Eclipse plugin.
            // We must trigger TestCassandra teardown ourselves.
            TestCassandraManager.teardownCassandra();
        }
        // else we're running ALL the tests, and TestCassandraManager is run as a test class,
        // so its @AfterSuite method is called after the last test.

        logger.info("HectorTestBase.teardownDAOManagerAndCassandra finished");
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
        logger.info("HectorTestBase.cleanCassandraDB starting");
        Keyspace mainKeyspace = daoManager.getMainKeyspace();

        cleanColumnFamily(mainKeyspace, Schema.POLICIES);
        logger.info("HectorTestBase.cleanCassandraDB finished");
    }

    private <K> void cleanColumnFamily(Keyspace ks, ColumnFamily<K> cf) {
        while (true) {
            logger.info("HectorTestBase.cleanCassandraDB starting cycle for " + cf.getName());
            // Query a batch of keys in this column family.
            // Use of the common-to-all-CFs "EXISTS" column allows us to omit tombstone rows:
            // they will be present in the result but will lack this column.

            // TODO: fix up
            @SuppressWarnings("unchecked") // generic array creation, ok
            RangeSlicesQuery<K, String, byte[]> query = cf.createRangeSlicesQuery(ks, cf.EXISTS);

            query.setRowCount(100000);
            QueryResult<OrderedRows<K, String, byte[]>> result = query.execute();
            List<Row<K, String, byte[]>> rows = result.get().getList();
            logger.info("HectorTestBase.cleanCassandraDB: rows: " + rows.size());
            if (rows.size() == 0) {
                break;
            }

            // Delete the rows for these keys
            Mutator<K> m = cf.createMutator(ks);
            boolean rowsExist = false;
            for (Row<K, String, byte[]> row : rows) {
                logger.info("HectorTestBase.cleanCassandraDB: key: " + row.getKey());
                HColumn<String, byte[]> exists = row.getColumnSlice().getColumnByName(cf.EXISTS.getName());
                if (exists == null) {
                    logger.info("HectorTestBase.cleanCassandraDB: tombstone row");
                    continue;
                }
                logger.info("HectorTestBase.cleanCassandraDB: row exists");
                cf.addRowDeletion(m, row.getKey());
                rowsExist = true;
            }
            if (!rowsExist) { 
                logger.info("HectorTestBase.cleanCassandraDB: no more rows");
                break;
            }
            m.execute();
            logger.info("HectorTestBase.cleanCassandraDB ended cycle");
        }
        logger.info("HectorTestBase.cleanCassandraDB finished for " + cf.getName());
    }
}
