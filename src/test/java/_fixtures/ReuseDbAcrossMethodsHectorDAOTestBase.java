package _fixtures;

import org.testng.annotations.BeforeClass;

public abstract class ReuseDbAcrossMethodsHectorDAOTestBase extends HectorDAOTestBase {

    @BeforeClass(groups = {"dao"})
    public void cleanDB() {
        cleanCassandraDB();
    }
}
