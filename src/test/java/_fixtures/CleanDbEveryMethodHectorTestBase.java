package _fixtures;

import org.testng.annotations.BeforeMethod;

public abstract class CleanDbEveryMethodHectorTestBase extends HectorTestBase {

    @BeforeMethod
    public void cleanDB() {
        cleanCassandraDB();
    }
}
