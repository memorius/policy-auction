package _fixtures;

import org.testng.annotations.BeforeMethod;

public abstract class CleanDbEveryMethodDAOManagerTestBase extends DAOManagerTestBase {

    @BeforeMethod(groups = {"dao"})
    public void cleanDB() {
        cleanCassandraDB();
    }
}
