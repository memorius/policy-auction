package _fixtures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * Holder to control creation/destruction of embedded cassandra instance for unit tests.
 * <p>
 * This is static because there's no way to tear down and recreate the cassandra instance within the same JVM,
 * due to its internal use of static datastructures, so we have to use the same instance for the whole test run.
 *
 * @author Nick Clarke
 */
public final class TestCassandraManager {

    private static final Logger logger = LoggerFactory.getLogger(TestCassandraManager.class);

    private static TestCassandra testCassandra;

    /**
     * When running a full suite (e.g. with "mvn test"), the annotation means this is automatically called by TestNG
     * to set up the test Cassandra server before any of the actual tests run.
     * <p>
     * When running single tests from Eclipse, {@link DAOManagerTestBase} calls this explicitly.
     */
    @BeforeSuite(groups = {"dao"})
    public static void setupCassandra() {
        logger.info("TestCassandraManager.setupCassandra starting");
        testCassandra = new TestCassandra();
        testCassandra.start();
        logger.info("TestCassandraManager.setupCassandra finished");
    }

    /**
     * When running a full suite (e.g. with "mvn test"), the annotation means this is automatically called by TestNG
     * to tear down the test Cassandra server after all of the actual tests have run.
     * <p>
     * When running single tests from Eclipse, {@link DAOManagerTestBase} calls this explicitly.
     */
    @AfterSuite(groups = {"dao"})
    public static void teardownCassandra() {
        logger.info("TestCassandraManager.teardownCassandra starting");
        testCassandra.teardown();
        logger.info("TestCassandraManager.teardownCassandra finished");
    }

    public static boolean isInitialized() {
        return (testCassandra != null);
    }

    /**
     * Get the TCP port to connect Hector to, as read from the yaml config file.
     */
    public static int getCassandraRpcPort() {
        return testCassandra.getRpcPort();
    }
}
