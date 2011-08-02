package _fixtures;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import me.prettyprint.cassandra.testutils.EmbeddedServerHelper;

import org.apache.cassandra.cli.CliMain;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.io.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modified version of me.prettyprint.cassandra.testutils.EmbeddedServerHelper
 *
 * @author Ran Tavory (rantav@gmail.com)
 * @author Nick Clarke
 * 
 */
public class TestCassandra {
  private static final String LOG4J_TEST_PROPERTIES = "/log4j-cassandra.test.properties";
  private static final String TEST_YAML_FILE = "/cassandra.test.yaml";
  private static final String TEST_SCHEMA_FILE = "/cassandra-schema.test.txt";

  private static Logger log = LoggerFactory.getLogger(TestCassandra.class);

  private static final String TMP = "target/cassandra-unit-test-config";

  private ShortSocketTimeoutCassandraDaemon cassandraDaemon;

  public TestCassandra() {
      // delete tmp dir first
      rmdir(TMP);
      // make a tmp dir and copy cassandra.yaml and log4j.properties to it
      mkdir(TMP);
      copy(LOG4J_TEST_PROPERTIES, TMP);
      copy(TEST_YAML_FILE, TMP);
      copy(TEST_SCHEMA_FILE, TMP);
      System.setProperty("cassandra.config", "file:" + TMP + TEST_YAML_FILE);
      System.setProperty("log4j.configuration", "file:" + TMP + LOG4J_TEST_PROPERTIES);
      System.setProperty("cassandra-foreground", "true");
  }

  private ExecutorService executor = Executors.newSingleThreadExecutor();

  private boolean dbInitialized = false;

  /**
   * Set embedded cassandra up and spawn it in a new thread.
   */
  public void start() {
      if (dbInitialized) {
          // Can't do this because DatabaseDescriptor has static datastructures.
          throw new IllegalStateException("start() has already been called");
      }
      if (executor == null) {
          throw new IllegalStateException("teardown() has already been called");
      }

    cleanupAndLeaveDirs();

    log.info("Starting executor");

    CassandraRunner command = new CassandraRunner();
    executor.execute(command);

    log.info("Started executor");
    try
    {
        command.await(15, TimeUnit.SECONDS);
        log.info("Cassandra is ready");
    }
    catch (InterruptedException e)
    {
        throw new RuntimeException("Embedded Cassandra instance took too long to start up", e);
    }

    dbInitialized = true;

    loadCliSchema();
  }

  /**
   * Get the TCP port to connect Hector to, as read from the yaml config file.
   * <p>
   * {@link #start()} must have been called first.
   */
  public int getRpcPort() {
      if (!dbInitialized) {
          throw new IllegalStateException("initializeCleanCassandraDB must be called first");
      }
      return DatabaseDescriptor.getRpcPort();
  }

    private void loadCliSchema() {
        try {
            CliMain.main(new String[] {
                    "--host", "localhost", "--port", String.valueOf(getRpcPort()), "--file", (TMP + TEST_SCHEMA_FILE)
            });
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            teardown();
        } finally {
            super.finalize();
        }
    }

  public void teardown() {
    stopDaemon();

    // If startup failed, executor may still be running the CassandraRunner. Try to get rid of it. 
    executor.shutdown();
    executor.shutdownNow();
    executor = null;

    rmdir(TMP);
    log.info("Teardown complete");
  }

    private void stopDaemon() {
        if (cassandraDaemon != null) {
            // TODO: deactivate currently blocks for about one minute.
            //       Unless I figure out why, fire it off in the background as a daemon thread:
            //       this way TestNG can still exit promptly when run in a separate JVM;
            ///      no need to wait for clean shutdown.
            //       Don't use the existing executor since we're shutting it down right away.
            Thread t = new Thread("TestCassandra daemon deactivator") {
                @Override
                public void run() {
                    log.info("Trying to deactivate CassandraDaemon...");
                    cassandraDaemon.deactivate();
                    log.info("CassandraDaemon deactivated.");
                    cassandraDaemon = null;
                }
            };
            t.setDaemon(true);
            t.start();
        }
    }

  private static void rmdir(String dir) {
    File dirFile = new File(dir);
    if (dirFile.exists()) {
      try {
        FileUtils.deleteRecursive(new File(dir));
    } catch (IOException e) {
        throw new AssertionError(e);
    }
    }
  }

  /**
   * Copies a resource from within the jar to a directory.
   * 
   * @param resource
   * @param directory
   */
  private static void copy(String resource, String directory) {
    InputStream is = EmbeddedServerHelper.class.getResourceAsStream(resource);
    if (is == null) {
        throw new AssertionError("Required resource '" + resource
                + "' does not exist. Please run initialize-cassandra-for-tests.sh before running tests.");
    }
    String fileName = resource.substring(resource.lastIndexOf("/") + 1);
    File file = new File(directory + System.getProperty("file.separator")
        + fileName);
    
    try {
        OutputStream out = new FileOutputStream(file);
        byte buf[] = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
          out.write(buf, 0, len);
        }
        out.close();
        is.close();
    } catch (IOException e) {
        throw new AssertionError(e);
    }
  }

  /**
   * Creates a directory
   * 
   * @param dir
   */
  private static void mkdir(String dir) {
    try {
        FileUtils.createDirectory(dir);
    } catch (IOException e) {
        throw new AssertionError(e);
    }
  }
  

  private static void cleanupAndLeaveDirs()
  {
      mkdirs();
      cleanup();
      mkdirs();
      // CommitLog.instance.resetUnsafe(); // cleanup screws w/ CommitLog, this brings it back to safe state
  }

  private static void cleanup()
  {
      // clean up commitlog
      String[] directoryNames = { DatabaseDescriptor.getCommitLogLocation(), };
      for (String dirName : directoryNames)
      {
          File dir = new File(dirName);
          if (!dir.exists()) {
            throw new RuntimeException("No such directory: " + dir.getAbsolutePath());
          }
          try {
              FileUtils.deleteRecursive(dir);
          } catch (IOException e) {
              throw new AssertionError(e);
          }
      }

      // clean up data directory which are stored as data directory/table/data files
      for (String dirName : DatabaseDescriptor.getAllDataFileLocations())
      {
          File dir = new File(dirName);
          if (!dir.exists()) {
            throw new RuntimeException("No such directory: " + dir.getAbsolutePath());
          }
          try {
              FileUtils.deleteRecursive(dir);
          } catch (IOException e) {
              throw new AssertionError(e);
          }
      }
  }

  private static void mkdirs()
  {
      try
      {
          DatabaseDescriptor.createAllDirectories();
      }
      catch (IOException e)
      {
          throw new RuntimeException(e);
      }
  }  

  private class CassandraRunner implements Runnable {    

      private final CountDownLatch initLatch = new CountDownLatch(1);

      public void await(long timeout, TimeUnit unit) throws InterruptedException {
          initLatch.await(timeout, unit);
      }

        @Override
        public void run() {
            cassandraDaemon = new ShortSocketTimeoutCassandraDaemon();

            log.info("Created CassandraDaemon");

            cassandraDaemon.activate();

            log.info("Activated CassandraDaemon");

            initLatch.countDown();
        }
    }
}
