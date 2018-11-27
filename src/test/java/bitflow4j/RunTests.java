package bitflow4j;

import bitflow4j.misc.Config;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.logging.Logger;


/**
 * Created by anton on 27.12.16.
 */
public class RunTests {

    static final Logger logger = Logger.getLogger(RunTests.class.getName());

    static {
        Config.initializeLogger();
    }

    public static void main(String[] args) {
        logger.info("RUNNING TESTS");
        Result result = JUnitCore.runClasses(BitflowTestSuite.class);

        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        System.out.println(result.wasSuccessful());
    }

}
