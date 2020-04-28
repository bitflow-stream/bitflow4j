package bitflow4j.steps;

import bitflow4j.Context;
import bitflow4j.registry.Description;
import bitflow4j.registry.StepName;

import java.io.IOException;
import java.util.logging.Logger;

@Description("Print a log message upon initialization and forward all received samples unchanged")
@StepName("echo")
public class EchoStep extends NoopStep {

    private static final Logger logger = Logger.getLogger(EchoStep.class.getName());

    private final String msg;

    public EchoStep(String msg) {
        this.msg = msg;
    }

    @Override
    public void initialize(Context context) throws IOException {
        System.err.println(msg);
        super.initialize(context);
    }

}
