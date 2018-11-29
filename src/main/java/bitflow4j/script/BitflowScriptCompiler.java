package bitflow4j.script;

import bitflow4j.Pipeline;
import bitflow4j.PipelineStep;
import bitflow4j.Source;
import bitflow4j.script.endpoints.EndpointFactory;
import bitflow4j.script.generated.BitflowLexer;
import bitflow4j.script.generated.BitflowListener;
import bitflow4j.script.generated.BitflowParser;
import bitflow4j.script.registry.AnalysisRegistration;
import bitflow4j.script.registry.ForkRegistration;
import bitflow4j.script.registry.Registry;
import bitflow4j.script.registry.StepConstructionException;
import bitflow4j.steps.fork.Fork;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * BitflowScriptCompiler wraps anything Antlr related and implements the Antlr AST listener that can parse
 * and convert a Bitflow Script into a Pipeline.
 */
class BitflowScriptCompiler {

    private static final Logger logger = Logger.getLogger(BitflowScriptCompiler.class.getName());

    private static final int MAX_ERROR_TEXT = 10;

    private final Registry registry;
    private final EndpointFactory endpointFactory;

    public BitflowScriptCompiler(Registry registry, EndpointFactory endpointFactory) {
        this.registry = registry;
        this.endpointFactory = endpointFactory;
    }

    /**
     * parseScript takes a raw script and compiles it. The result contains the Pipeline or an array of error messages.
     *
     * @param script the raw bitflow script as a string
     * @return the CompileResult, containing the pipeline or an array of error messages
     */
    public CompileResult parseScript(String script) {
        CharStream charStream = CharStreams.fromString(script);
        BitflowLexer lexer = new BitflowLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BitflowParser parser = new BitflowParser(tokens);

        // Fail parsing silently: errors will be reported through the CompileResult. Not sure why it is so hard to silently obtain a verbose error message.
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                // Wrap the message in a new RecognitionException, because the provided RecognitionException does not contain any Exception message
                RecognitionException rec = new RecognitionException(msg, recognizer, null, (ParserRuleContext) e.getCtx()) {
                    @Override
                    public Token getOffendingToken() {
                        return e.getOffendingToken();
                    }
                };
                throw new ParseCancellationException(rec);
            }
        });

        BitflowScriptListener scriptListener = new BitflowScriptListener();
        BitflowParser.ScriptContext parsedScript;
        try {
            parsedScript = parser.script();
        } catch (ParseCancellationException e) {
            if (e.getCause() instanceof RecognitionException) {
                return new CompileResult((RecognitionException) e.getCause());
            }
            logger.log(Level.SEVERE, "Unknown exception during Bitflow script compilation", e);
            return new CompileResult(null, Collections.singletonList("Unknown error: " + e.getCause().toString()));
        } catch (RecognitionException e) {
            return new CompileResult(e);
        }
        ParseTreeWalker.DEFAULT.walk(scriptListener, parsedScript);
        return new CompileResult(scriptListener.currentPipeline(), scriptListener.errors);
    }

    public static class CompileResult {
        private List<String> errors;
        private Pipeline pipeline;

        private CompileResult(Pipeline pipeline, List<String> errors) {
            this.errors = errors;
            this.pipeline = pipeline;
        }

        private CompileResult(RecognitionException e) {
            this(null, Collections.singletonList(
                    formatError(e.getOffendingToken(), e.getCtx(), e.getMessage())));
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

    static String formatError(Token position, RuleContext ctx, String msg) {
        String text = ctx.getText();
        if (text == null || text.isEmpty()) {
            text = position.getText();
        }
        if (text.length() > MAX_ERROR_TEXT + "...".length()) {
            text = text.substring(0, MAX_ERROR_TEXT) + "...";
        }

        return String.format("Line %s (%s) '%s': %s",
                position.getLine(), position.getCharPositionInLine(), text, msg);
    }

    /**
     * BitflowScriptListener listens on a AST tree of a bitflow script and generates the Pipeline.
     */
    private class BitflowScriptListener implements BitflowListener {
        private GenericStateMap state = new GenericStateMap();
        private List<String> errors = new ArrayList<>();

        private void pushError(ParserRuleContext ctx, String msg) {
            errors.add(formatError(ctx.getStart(), ctx, msg));
        }

        private Pipeline currentPipeline() {
            return state.peek("pipeline");
        }

        @Override
        public void exitFork(BitflowParser.ForkContext ctx) {
            String forkName = state.pop("name");
            Map<String, String> forkParams = state.pop("parameters");
            Map<String, Pipeline> subpipes = state.pop("fork_subpipeline_map");

            ForkRegistration forkReg = registry.getFork(forkName);
            if (forkReg == null) {
                pushError(ctx, "Pipeline fork is unknown.");
                return;
            }

            try {
                Fork fork = forkReg.getForkConstructor().constructForkStep(subpipes, forkParams);
                currentPipeline().step(fork);
            } catch (StepConstructionException e) {
                pushError(ctx, e.getStepName() + ": " + e.getMessage());
            }
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
            String[] inputs = new String[state.len("input_descriptions")];
            for (int i = 0; i < inputs.length; i++) {
                inputs[i] = state.pop("input_descriptions");
            }
            try {
                Source source = endpointFactory.createSource(inputs);
                currentPipeline().input(source);
            } catch (IOException e) {
                pushError(ctx, "Could not create multisource input:" + e.getMessage());
            }
        }

        @Override
        public void exitInput(BitflowParser.InputContext ctx) {
            String name = state.pop("name");
            Boolean isMultiInput = state.peekOrDefault("is_multi_input", Boolean.FALSE);
            if (isMultiInput) {
                state.push("input_descriptions", name);
            } else {
                try {
                    Source source = endpointFactory.createSource(name);
                    currentPipeline().input(source);
                } catch (IOException e) {
                    pushError(ctx, "Could not create source:" + e.getMessage());
                }
            }
        }

        @Override
        public void exitOutput(BitflowParser.OutputContext ctx) {
            String name = state.pop("name");
            try {
                PipelineStep sink = endpointFactory.createSink(name);
                currentPipeline().step(sink);
            } catch (IOException e) {
                pushError(ctx, "Could not create sink: " + e.getMessage());
            }
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

        @Override
        public void enterPipeline(BitflowParser.PipelineContext ctx) {
            state.push("pipeline", new Pipeline());
        }

        // ################ UNUSED ####################
        // left for convenience, can be removed by extending from BitflowBaseListener
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