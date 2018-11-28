package bitflow4j;

import bitflow4j.script.MainTest;
import bitflow4j.script.endpoints.EndpointFactoryTest;
import bitflow4j.script.registry.AnalysisRegistrationTest;
import bitflow4j.script.registry.ScanForPipelineStepTest;
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
