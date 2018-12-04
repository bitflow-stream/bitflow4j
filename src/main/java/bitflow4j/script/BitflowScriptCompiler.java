package bitflow4j.script;

import bitflow4j.Pipeline;
import bitflow4j.PipelineStep;
import bitflow4j.Source;
import bitflow4j.misc.Pair;
import bitflow4j.script.endpoints.EndpointFactory;
import bitflow4j.script.generated.BitflowBaseListener;
import bitflow4j.script.generated.BitflowLexer;
import bitflow4j.script.generated.BitflowParser;
import bitflow4j.script.registry.AnalysisRegistration;
import bitflow4j.script.registry.ForkRegistration;
import bitflow4j.script.registry.Registry;
import bitflow4j.script.registry.StepConstructionException;
import bitflow4j.steps.fork.Distributor;
import bitflow4j.steps.fork.Fork;
import bitflow4j.steps.fork.distribute.MultiplexDistributor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
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

    private static final int MAX_ERROR_TEXT = 30;

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
                ParserRuleContext ctx = e == null ? null : (ParserRuleContext) e.getCtx();
                RecognitionException rec = new RecognitionException(msg, recognizer, null, ctx) {
                    @Override
                    public Token getOffendingToken() {
                        Token result = null;
                        if (e != null)
                            result = e.getOffendingToken();
                        if (result == null && offendingSymbol instanceof Token)
                            result = (Token) offendingSymbol;
                        return result;
                    }
                };
                throw new ParseCancellationException(rec);
            }
        });

        BitflowScriptListener scriptListener = new BitflowScriptListener();
        BitflowParser.ScriptContext parsedScript;
        try {
            parsedScript = parser.script();
            ParseTreeWalker.DEFAULT.walk(scriptListener, parsedScript);
            return new CompileResult(scriptListener.currentPipeline(), scriptListener.errors);
        } catch (ParseCancellationException e) {
            if (e.getCause() instanceof RecognitionException) {
                return new CompileResult((RecognitionException) e.getCause());
            }
            logger.log(Level.SEVERE, "Unknown exception during Bitflow script compilation", e);
            return new CompileResult(null, Collections.singletonList("Unknown error: " + e.getCause().toString()));
        } catch (RecognitionException e) {
            return new CompileResult(e);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Exception during Bitflow script compilation", e);
            return new CompileResult(null, Collections.singletonList(String.format("Unknown %s during Bitflow script compilation: %s", e.getClass().getName(), e.getMessage())));
        }
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
        String text = "";
        if (ctx != null)
            text = ctx.getText();
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
    private class BitflowScriptListener extends BitflowBaseListener {

        private GenericStateMap state = new GenericStateMap();
        private List<String> errors = new ArrayList<>();

        private void pushError(ParserRuleContext ctx, String msg) {
            errors.add(formatError(ctx.getStart(), ctx, msg));
        }

        private Pipeline currentPipeline() {
            return state.peek("pipeline");
        }

        private String unwrapSTRING(TerminalNode token) {
            String text = token.getText();
            return text.substring(1, text.length() - 1);
        }

        private String unwrap(BitflowParser.NameContext ctx) {
            return ctx.STRING() == null ? ctx.getText() : unwrapSTRING(ctx.STRING());
        }

        private String unwrap(BitflowParser.ValContext ctx) {
            return ctx.STRING() == null ? ctx.getText() : unwrapSTRING(ctx.STRING());
        }

        @Override
        public void exitInput(BitflowParser.InputContext ctx) {
            String[] inputs = ctx.name().stream().map(this::unwrap).toArray(String[]::new);
            try {
                Source source = endpointFactory.createSource(inputs);
                currentPipeline().input(source);
            } catch (IOException e) {
                pushError(ctx, "Could not create source: " + e.getMessage());
            }
        }

        @Override
        public void exitOutput(BitflowParser.OutputContext ctx) {
            String output = unwrap(ctx.name());
            try {
                PipelineStep sink = endpointFactory.createSink(output);
                currentPipeline().step(sink);
            } catch (IOException e) {
                pushError(ctx, "Could not create sink: " + e.getMessage());
            }
        }

        @Override
        public void exitTransform(BitflowParser.TransformContext ctx) {
            String name = unwrap(ctx.name());
            Map<String, String> params = state.pop("parameters");
            Boolean isBatched = state.peekOrDefault("is_batched", Boolean.FALSE);

            AnalysisRegistration regAnalysis = registry.getAnalysisRegistration(name);
            if (regAnalysis == null) {
                pushError(ctx, "Unknown Processor.");
                return;
            } else if (isBatched && !regAnalysis.supportsBatch()) {
                pushError(ctx, "Processor used in window, but does not support batch processing.");
                return;
            } else if (!isBatched && !regAnalysis.supportsStream()) {
                pushError(ctx, "Processor used outside window, but does not support stream processing.");
                return;
            }
            regAnalysis.validateParameters(params).forEach(e -> pushError(ctx, e));

            try {
                PipelineStep step = regAnalysis.getStepConstructor().constructPipelineStep(params);
                currentPipeline().step(step);
            } catch (StepConstructionException e) {
                pushError(ctx, e.getStepName() + ": " + e.getMessage());
            }
        }

        @Override
        public void enterTransformParameters(BitflowParser.TransformParametersContext ctx) {
            state.push("parameters", new HashMap<String, String>());
        }

        @Override
        public void exitParameter(BitflowParser.ParameterContext ctx) {
            Map<String, String> params = state.peek("parameters");
            String key = unwrap(ctx.name());
            String value = unwrap(ctx.val());
            if (params.containsKey(key)) {
                pushError(ctx, String.format("Duplicate parameter %s (values '%s' and '%s')", key, params.get(key), value));
            } else {
                params.put(key, value);
            }
        }

        @Override
        public void enterPipeline(BitflowParser.PipelineContext ctx) {
            state.push("pipeline", new Pipeline());
        }

        @Override
        public void enterSubPipeline(BitflowParser.SubPipelineContext ctx) {
            state.push("pipeline", new Pipeline());
        }

        @Override
        public void enterMultiplexFork(BitflowParser.MultiplexForkContext ctx) {
            state.push("multiplexPipelines", new ArrayList<Pipeline>());
        }

        @Override
        public void exitMultiplexFork(BitflowParser.MultiplexForkContext ctx) {
            List<Pipeline> subPipelines = state.pop("multiplexPipelines");
            MultiplexDistributor multiplex = new MultiplexDistributor(subPipelines);
            Fork fork = new Fork(multiplex);
            currentPipeline().step(fork);
        }

        @Override
        public void exitMultiplexSubPipeline(BitflowParser.MultiplexSubPipelineContext ctx) {
            Pipeline pipeline = state.pop("pipeline");
            List<Pipeline> subPipelines = state.peek("multiplexPipelines");
            subPipelines.add(pipeline);
        }

        @Override
        public void enterFork(BitflowParser.ForkContext ctx) {
            state.push("forkedSubPipelines", new ArrayList<Pair<String, Pipeline>>());
        }

        @Override
        public void exitNamedSubPipeline(BitflowParser.NamedSubPipelineContext ctx) {
            Collection<Pair<String, Pipeline>> forkSubPipes = state.peek("forkedSubPipelines");
            Pipeline subPipe = state.pop("pipeline");
            ctx.name().stream().map(this::unwrap).forEach((key) -> forkSubPipes.add(new Pair<>(key, subPipe)));
        }

        @Override
        public void exitFork(BitflowParser.ForkContext ctx) {
            String forkName = unwrap(ctx.name());
            Map<String, String> forkParams = state.pop("parameters");
            Collection<Pair<String, Pipeline>> subPipes = state.pop("forkedSubPipelines");

            ForkRegistration forkReg = registry.getFork(forkName);
            if (forkReg == null) {
                pushError(ctx, "Unknown fork: " + forkName);
                return;
            }

            try {
                Distributor distributor = forkReg.getForkConstructor().constructForkStep(subPipes, forkParams);
                currentPipeline().step(new Fork(distributor));
            } catch (StepConstructionException e) {
                pushError(ctx, e.getStepName() + ": " + e.getMessage());
            }
        }

        // =====================

        // =====================

        // =====================

        @Override
        public void enterMultiInputPipeline(BitflowParser.MultiInputPipelineContext ctx) {
            // TODO implement parallel multi input
            pushError(ctx, "Parallel multi Input pipelines are not yet supported");
        }

        @Override
        public void enterWindow(BitflowParser.WindowContext ctx) {
            state.push("is_batched", Boolean.TRUE);
        }

        public void enterWindowPipeline(BitflowParser.WindowContext ctx) {
            state.push("pipeline", new Pipeline());
        }

        @Override
        public void exitWindow(BitflowParser.WindowContext ctx) {

            // TODO correctly apply batch parameters. The popped pipeline contains all sequential batch steps

            pushError(ctx, "Window is not yet supported");

            Pipeline batchPipeline = state.pop("pipeline");
            state.pop("is_batched");
        }

    }
}