package _fixtures;

import org.testng.annotations.BeforeClass;

public abstract class ReuseDbAcrossMethodsHectorTestBase extends HectorTestBase {

    @BeforeClass
    public void cleanDB() {
        cleanCassandraDB();
    }
}
