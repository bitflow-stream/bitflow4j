// Generated from Bitflow.g4 by ANTLR 4.7.1
package bitflow4j.main.script;

import bitflow4j.main.Pipeline;
import bitflow4j.main.registry.AnalysisRegistration;
import bitflow4j.main.registry.ForkRegistration;
import bitflow4j.main.registry.StepConstructionException;
import bitflow4j.main.registry.Registry;
import bitflow4j.main.script.generated.BitflowLexer;
import bitflow4j.main.script.generated.BitflowListener;
import bitflow4j.main.script.generated.BitflowParser;
import bitflow4j.steps.PipelineStep;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

class BitflowScriptCompiler {

    private final Registry registry;

    BitflowScriptCompiler(Registry registry) {
        this.registry = registry;
    }

    /**
     * ParseScript takes a raw script and compiles it. The result contains the Pipeline or an array of error messages.
     *
     * @param script the raw bitflow script as a string
     * @return the CompileResult, containing the pipeline or an array of error messages
     */
    CompileResult ParseScript(String script) {
        CharStream charStream = CharStreams.fromString(script);
        BitflowLexer lexer = new BitflowLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BitflowParser parser = new BitflowParser(tokens);

        BitflowScriptListener scriptListener = new BitflowScriptListener();
        ParseTreeWalker.DEFAULT.walk(scriptListener, parser.script());
        return new CompileResult(scriptListener.currentPipeline(), scriptListener.errors);

    }

    public class CompileResult {
        private List<String> errors;
        private Pipeline pipeline;

        private CompileResult(Pipeline pipeline, List<String> errors) {
            this.errors = errors;
            this.pipeline = pipeline;
        }

        public Pipeline getPipeline() {
            return pipeline;
        }

        public List<String> getErrors() {
            return errors;
        }

        public boolean hasErrors() {
            return errors != null && errors.size() > 0;
        }
    }

    private class BitflowScriptListener implements BitflowListener {
        private GenericStateMap state = new GenericStateMap();
        private List<String> errors = new ArrayList<>();

        private void pushError(ParserRuleContext ctx, String msg) {
            int start = ctx.getStart().getStartIndex();
            int stop = ctx.getStop().getStopIndex();
            String text = ctx.getText();
            if (text.length() > 13) {
                text = text.substring(0, 10) + "...";
            }
            text = String.format("%1$-" + 13 + "s", text);
            errors.add(String.format("[%s-%s]\t'%s':\t%s", start, stop, text, msg));
        }

        private Pipeline currentPipeline() {
            return state.peek("pipeline");
        }

        @Override
        public void exitFork(BitflowParser.ForkContext ctx) {
            String forkName = state.pop("name");
            Map<String, String> forkParams = state.pop("parameters");
            Map<String, Pipeline> subpipes = state.pop("fork_subpipeline_map");

            // TODO
            ForkRegistration fork = registry.getFork(forkName);

            if (fork == null) {
                pushError(ctx, "Pipeline fork is unknown.");
                return;
            }
            pushError(ctx, "not implemented yet");

            // TODO create fork and add to pipeline
        }

        @Override
        public void enterWindow(BitflowParser.WindowContext ctx) {
            if (state.peek("is_batched") == Boolean.TRUE) {
                pushError(ctx, "window{ ... }: Window inside Window is not allowed.");
            }
            state.push("is_batched", Boolean.TRUE);
        }

        @Override
        public void exitWindow(BitflowParser.WindowContext ctx) {
            state.pop("is_batched");
        }

        @Override
        public void enterMultiinput(BitflowParser.MultiinputContext ctx) {
            state.push("is_multi_input", true);
        }

        @Override
        public void exitMultiinput(BitflowParser.MultiinputContext ctx) {
            state.pop("is_multi_input"); // reset
            List<String> inputs = new ArrayList<>();
            while (state.len("input_descriptions") > 0) {
                inputs.add(state.pop("input_descriptions"));
            }
            //TODO: create multiinput from list of inputs and set source of pipeline
            pushError(ctx, "not implemented yet");

        }

        @Override
        public void exitInput(BitflowParser.InputContext ctx) {
            String name = state.pop("name");
            Boolean isMultiInput = state.peekOrDefault("is_multi_input", Boolean.FALSE);
            if (isMultiInput) {
                state.push("input_descriptions", name);
            } else {

                // TODO create source from name and set source of pipeline
                pushError(ctx, "not implemented yet");
            }
        }

        @Override
        public void exitOutput(BitflowParser.OutputContext ctx) {
            String name = state.pop("name");
            pushError(ctx, "not implemented yet");

            // TODO create Output from name
        }

        @Override
        public void exitTransform(BitflowParser.TransformContext ctx) {
            String name = state.pop("name");
            Map<String, String> params = state.popOrDefault("parameters", Collections.emptyMap());
            Boolean isBatched = state.peekOrDefault("is_batched", Boolean.FALSE);

            AnalysisRegistration regAnalysis = registry.getAnalysisRegistration(name);
            if (regAnalysis == null) {
                pushError(ctx, "Unknown Processor.");
                return;
            } else if (isBatched && !regAnalysis.isSupportsBatchProcessing()) {
                pushError(ctx, "Processor used in window, but does not support batch processing.");
                return;
            } else if (!isBatched && !regAnalysis.isSupportsStreamProcessing()) {
                pushError(ctx, "Processor used outside window, but does not support stream processing.");
                return;
            }
            List<String> paramErrors = regAnalysis.validateParameters(params);
            paramErrors.forEach(e -> pushError(ctx, e));

            try {
                PipelineStep step = regAnalysis.getStepConstructor().constructPipelineStep(params);
                currentPipeline().step(step);
            } catch (StepConstructionException e) {
                pushError(ctx, e.getStepName() + ": " + e.getMessage());
            }
        }

        @Override
        public void enterSubPipeline(BitflowParser.SubPipelineContext ctx) {
            state.push("pipeline", new Pipeline());
        }

        @Override
        public void exitSubPipeline(BitflowParser.SubPipelineContext ctx) {
            Map<String, Pipeline> forkSubPipes = state.peek("fork_subpipeline_map");
            String subPipeKey = state.pop("pipeline_name");
            if (subPipeKey == null) {
                subPipeKey = "" + (forkSubPipes.size() + 1);
            }
            Pipeline subPipe = state.pop("pipeline");
            forkSubPipes.put(subPipeKey, subPipe);
        }

        @Override
        public void exitParameter(BitflowParser.ParameterContext ctx) {
            Map<String, String> params = state.peek("parameters");
            String key = ctx.getChild(0).getText();
            String value = ctx.getChild(2).getText();
            params.put(key, value);
        }

        @Override
        public void enterTransformParameters(BitflowParser.TransformParametersContext ctx) {
            state.push("parameters", new HashMap<String, String>());
        }

        @Override
        public void exitName(BitflowParser.NameContext ctx) {
            state.push("name", ctx.getText());
        }

        @Override
        public void exitPipelineName(BitflowParser.PipelineNameContext ctx) {
            state.push("pipeline_name", ctx.getText());
        }

        @Override
        public void enterFork(BitflowParser.ForkContext ctx) {
            state.push("fork_subpipeline_map", new HashMap<String, Pipeline>());
        }

        // ############################################
        // ############################################
        // ################ UNUSED ####################
        // ############################################
        // ############################################

        @Override
        public void enterTransform(BitflowParser.TransformContext ctx) {

        }


        @Override
        public void enterOutputFork(BitflowParser.OutputForkContext ctx) {

        }

        @Override
        public void exitOutputFork(BitflowParser.OutputForkContext ctx) {

        }


        @Override
        public void enterScript(BitflowParser.ScriptContext ctx) {
        }

        @Override
        public void exitScript(BitflowParser.ScriptContext ctx) {

        }

        @Override
        public void enterWindowSubPipeline(BitflowParser.WindowSubPipelineContext ctx) {

        }

        @Override
        public void exitWindowSubPipeline(BitflowParser.WindowSubPipelineContext ctx) {
        }

        @Override
        public void enterInput(BitflowParser.InputContext ctx) {

        }

        @Override
        public void enterOutput(BitflowParser.OutputContext ctx) {

        }

        @Override
        public void exitTransformParameters(BitflowParser.TransformParametersContext ctx) {

        }

        @Override
        public void enterPipeline(BitflowParser.PipelineContext ctx) {
            state.push("pipeline", new Pipeline());
        }

        @Override
        public void exitPipeline(BitflowParser.PipelineContext ctx) {

        }

        @Override
        public void enterParameter(BitflowParser.ParameterContext ctx) {

        }

        @Override
        public void enterName(BitflowParser.NameContext ctx) {

        }

        @Override
        public void enterPipelineName(BitflowParser.PipelineNameContext ctx) {

        }

        @Override
        public void enterSchedulingHints(BitflowParser.SchedulingHintsContext ctx) {

        }

        @Override
        public void exitSchedulingHints(BitflowParser.SchedulingHintsContext ctx) {

        }

        @Override
        public void visitTerminal(TerminalNode node) {

        }

        @Override
        public void visitErrorNode(ErrorNode node) {

        }

        @Override
        public void enterEveryRule(ParserRuleContext ctx) {

        }

        @Override
        public void exitEveryRule(ParserRuleContext ctx) {
        }
    }
}