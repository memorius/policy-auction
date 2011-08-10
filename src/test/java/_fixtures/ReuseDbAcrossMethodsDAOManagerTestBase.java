package _fixtures;

import org.testng.annotations.BeforeClass;

public abstract class ReuseDbAcrossMethodsDAOManagerTestBase extends DAOManagerTestBase {

    @BeforeClass(groups = {"dao"})
    public void cleanDB() {
        cleanCassandraDB();
    }
}
