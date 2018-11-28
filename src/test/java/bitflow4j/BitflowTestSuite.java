package bitflow4j;

import bitflow4j.main.registry.AnalysisRegistrationTest;
import bitflow4j.main.registry.ScanForPipelineStepTest;
import bitflow4j.main.script.MainTest;
import bitflow4j.main.script.endpoints.EndpointFactoryTest;
import org.junit.runners.Suite;

@org.junit.runner.RunWith(Suite.class)
@Suite.SuiteClasses({
        TestMarshaller.class,
        AnalysisRegistrationTest.class,
        ScanForPipelineStepTest.class,
        MainTest.class,
        EndpointFactoryTest.class,
        TestDatabase.class
})
public class BitflowTestSuite {
}
