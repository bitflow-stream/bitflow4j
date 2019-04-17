package bitflow4j;

import bitflow4j.misc.TreeFormatterTest;
import bitflow4j.script.MainTest;
import bitflow4j.script.ScriptCompilationTest;
import bitflow4j.script.endpoints.EndpointFactoryTest;
import bitflow4j.script.registry.NameResolveTest;
import bitflow4j.script.registry.RegisteredPipelineStepTest;
import bitflow4j.script.registry.ScanForPipelineStepTest;
import org.junit.runners.Suite;

@org.junit.runner.RunWith(Suite.class)
@Suite.SuiteClasses({
        TestMarshaller.class,
        TestDatabase.class,
        TreeFormatterTest.class,

        // Bitflow Script
        RegisteredPipelineStepTest.class,
        NameResolveTest.class,
        ScanForPipelineStepTest.class,
        MainTest.class,
        ScriptCompilationTest.class,
        EndpointFactoryTest.class,

        SampleTest.class

        // Bitflow Query Language
        // TODO fix Bitflow Query Language scripts
//        TestsForV1.class, TestsForV2.class, TestsForV3.class, TestsForV4.class
})
public class BitflowTestSuite {
}
