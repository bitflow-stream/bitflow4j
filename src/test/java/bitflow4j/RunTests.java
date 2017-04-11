package bitflow4j;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


/**
 * Created by anton on 27.12.16.
 */
public class RunTests {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(BitflowTestSuite.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println(result.wasSuccessful());
    }
}
