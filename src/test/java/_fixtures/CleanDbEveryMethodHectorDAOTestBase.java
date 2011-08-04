package _fixtures;

import org.testng.annotations.BeforeMethod;

public abstract class CleanDbEveryMethodHectorDAOTestBase extends HectorDAOTestBase {

    @BeforeMethod(groups = {"dao"})
    public void cleanDB() {
        cleanCassandraDB();
    }
}
