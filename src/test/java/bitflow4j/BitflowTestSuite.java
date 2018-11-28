package bitflow4j;

import bitflow4j.script.MainTest;
import bitflow4j.script.endpoints.EndpointFactoryTest;
import bitflow4j.script.registry.AnalysisRegistrationTest;
import bitflow4j.script.registry.ScanForPipelineStepTest;
import org.junit.runners.Suite;

@org.junit.runner.RunWith(Suite.class)
@Suite.SuiteClasses({
        TestMarshaller.class,
        TestDatabase.class,

        // Bitflow Script
        AnalysisRegistrationTest.class,
        ScanForPipelineStepTest.class,
        MainTest.class,
        EndpointFactoryTest.class,

        // Bitflow Query Language
        // TODO fix Bitflow Query Language scripts
//        TestsForV1.class, TestsForV2.class, TestsForV3.class, TestsForV4.class
})
public class BitflowTestSuite {
}
