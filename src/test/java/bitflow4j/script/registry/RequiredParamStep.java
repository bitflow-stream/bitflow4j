package bitflow4j.script.registry;

import bitflow4j.AbstractPipelineStep;

/**
 * This is a  Class to test constructor autodiscovery by argument.
 * It implements one constructor per argument type plus a mixed constructor and can be used to test.
 */
public class RequiredParamStep extends AbstractPipelineStep {

    public final String requiredArg;

    public String optionalArg;

    public RequiredParamStep() {
        requiredArg = "";
    }

    public RequiredParamStep(String requiredArg) {
        this.requiredArg = requiredArg;
    }

    public RequiredParamStep(String requiredArg, String OptionalArg) {
        this.requiredArg = requiredArg;
        this.optionalArg = OptionalArg;
    }
}
