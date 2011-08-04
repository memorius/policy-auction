package _fixtures;

import org.testng.annotations.BeforeClass;

public abstract class ReuseDbAcrossMethodsHectorTestBase extends HectorTestBase {

    @BeforeClass(groups = {"dao"})
    public void cleanDB() {
        cleanCassandraDB();
    }
}
