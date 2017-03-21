package bitflow4j;

import org.junit.runners.Suite;

@org.junit.runner.RunWith(Suite.class)
@Suite.SuiteClasses({
    TestWithSamples.class, TestDatabase.class,
})
public class BitflowTestSuite {
}
