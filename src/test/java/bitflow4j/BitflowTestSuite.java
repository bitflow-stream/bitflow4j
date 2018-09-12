package bitflow4j;

import bitflow4j.main.registry.AnalysisRegistrationTest;
import org.junit.runners.Suite;

@org.junit.runner.RunWith(Suite.class)
@Suite.SuiteClasses({
        TestWithSamples.class,
        AnalysisRegistrationTest.class
})
public class BitflowTestSuite {
}
