package bitflow4j.script;

import bitflow4j.Pipeline;
import bitflow4j.PipelineStep;
import bitflow4j.Source;
import bitflow4j.misc.Pair;
import bitflow4j.script.endpoints.EndpointFactory;
import bitflow4j.script.generated.BitflowLexer;
import bitflow4j.script.generated.BitflowParser;
import bitflow4j.script.registry.ConstructionException;
import bitflow4j.script.registry.RegisteredFork;
import bitflow4j.script.registry.RegisteredPipelineStep;
import bitflow4j.script.registry.Registry;
import bitflow4j.steps.fork.Fork;
import bitflow4j.steps.fork.ScriptableDistributor;
import bitflow4j.steps.fork.distribute.MultiplexDistributor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
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

    private final Registry registry;
    private final EndpointFactory endpointFactory;

    public BitflowScriptCompiler(Registry registry, EndpointFactory endpointFactory) {
        this.registry = registry;
        this.endpointFactory = endpointFactory;
    }

    /**
     * parseScript takes a raw script and compiles it. The result contains the Pipeline or an array of error messages.
     */
    public Pipeline parseScript(String script) throws CompilationException {
        BitflowParser.ScriptContext ctx = parseScriptContext(script);
        return buildPipeline(ctx);
    }

    public BitflowParser.ScriptContext parseScriptContext(String script) throws CompilationException {
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

        BitflowParser.ScriptContext parsedScript;
        try {
            return parser.script();
        } catch (ParseCancellationException e) {
            if (e.getCause() instanceof RecognitionException) {
                throw new CompilationException((RecognitionException) e.getCause());
            }
            logger.log(Level.SEVERE, "Unknown exception during Bitflow script compilation", e);
            throw new CompilationException("Unknown error: " + e.getCause().toString());
        } catch (RecognitionException e) {
            throw new CompilationException(e);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Exception during Bitflow script compilation", e);
            throw new CompilationException(String.format("Unknown %s during Bitflow script compilation: %s", e.getClass().getName(), e.getMessage()));
        }
    }

    public Pipeline buildPipeline(BitflowParser.ScriptContext parsedScript) throws CompilationException {
        return buildPipeline(parsedScript.multiInputPipeline());
    }

    public Pipeline buildPipeline(BitflowParser.MultiInputPipelineContext ctx) throws CompilationException {
        if (ctx.pipeline().size() != 1) {
            // TODO implement parallel multi input
            throw new CompilationException("Multiple parallel multi-input pipelines are not yet supported");
        }
        return buildPipeline(ctx.pipeline(0));
    }

    public Pipeline buildPipeline(BitflowParser.PipelineContext ctx) throws CompilationException {
        Pipeline result = new Pipeline();
        if (ctx.multiInputPipeline() != null) {
            throw new CompilationException("Nested multi-input pipelines are not yet supported");
        } else if (ctx.input() != null) {
            result.input(buildInput(ctx.input()));
        }
        buildPipelineTail(ctx.pipelineElement(), result);
        return result;
    }

    public void buildPipelineTail(List<BitflowParser.PipelineElementContext> ctx, Pipeline pipeline) throws CompilationException {
        for (BitflowParser.PipelineElementContext elem : ctx) {

            // TODO implement batch mode

            buildStep(elem, pipeline, false);
        }
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

    public Source buildInput(BitflowParser.InputContext ctx) throws CompilationException {
        String[] inputs = ctx.name().stream().map(this::unwrap).toArray(String[]::new);
        try {
            return endpointFactory.createSource(inputs);
        } catch (IOException e) {
            throw new CompilationException(ctx, "Could not create source: " + e.getMessage());
        }
    }

    public PipelineStep buildOutput(BitflowParser.OutputContext ctx) throws CompilationException {
        String output = unwrap(ctx.name());
        try {
            return endpointFactory.createSink(output);
        } catch (IOException e) {
            throw new CompilationException(ctx, "Could not create sink: " + e.getMessage());
        }
    }

    public void buildStep(BitflowParser.PipelineElementContext ctx, Pipeline pipeline, boolean isBatched) throws CompilationException {
        if (ctx.output() != null) {
            pipeline.step(buildOutput(ctx.output()));
        } else if (ctx.transform() != null) {
            buildTransform(ctx.transform(), pipeline, isBatched);
        } else if (ctx.fork() != null) {
            buildFork(ctx.fork(), pipeline);
        } else if (ctx.multiplexFork() != null) {
            pipeline.step(buildMultiplexFork(ctx.multiplexFork()));
        } else if (ctx.window() != null) {
            throw new CompilationException(ctx, "Window is not yet supported");
        } else {
            throw new CompilationException(ctx, "Unexpected pipeline element: " + ctx);
        }
    }

    public void buildTransform(BitflowParser.TransformContext ctx, Pipeline pipeline, boolean isBatched) throws CompilationException {
        String name = unwrap(ctx.name());
        Map<String, String> params = buildParameters(ctx.transformParameters());

        RegisteredPipelineStep regAnalysis = registry.getAnalysisRegistration(name);
        if (regAnalysis == null) {
            throw new CompilationException(ctx, "Unknown Processor.");
        } else if (isBatched && !regAnalysis.supportsBatch()) {
            throw new CompilationException(ctx, "Processor used in window, but does not support batch processing.");
        } else if (!isBatched && !regAnalysis.supportsStream()) {
            throw new CompilationException(ctx, "Processor used outside window, but does not support stream processing.");
        }
        regAnalysis.validateParameters(params).forEach(e -> {
            throw new CompilationException(ctx, e);
        });

        try {
            regAnalysis.buildStep(pipeline, params);
        } catch (ConstructionException e) {
            throw new CompilationException(ctx, e.getStepName() + ": " + e.getMessage());
        }
    }

    public void buildFork(BitflowParser.ForkContext ctx, Pipeline pipeline) throws CompilationException {
        Map<String, String> forkParams = buildParameters(ctx.transformParameters());
        String forkName = unwrap(ctx.name());

        RegisteredFork forkReg = registry.getFork(forkName);
        if (forkReg == null) {
            throw new CompilationException(ctx, "Unknown fork: " + forkName);
        }

        Collection<Pair<String, ScriptableDistributor.PipelineBuilder>> subPipes = new ArrayList<>();
        for (BitflowParser.NamedSubPipelineContext subPipeline : ctx.namedSubPipeline()) {
            SubPipelineBuilder builder = new SubPipelineBuilder(subPipeline.subPipeline());
            for (BitflowParser.NameContext name : subPipeline.name()) {
                subPipes.add(new Pair<>(unwrap(name), builder));
            }
        }

        try {
            forkReg.buildFork(pipeline, subPipes, forkParams);
        } catch (ConstructionException e) {
            throw new CompilationException(ctx, e.getStepName() + ": " + e.getMessage());
        }
    }

    public Pipeline buildSubPipeline(BitflowParser.SubPipelineContext ctx) {
        Pipeline result = new Pipeline();
        buildPipelineTail(ctx.pipelineElement(), result);
        return result;
    }

    public class SubPipelineBuilder implements ScriptableDistributor.PipelineBuilder {

        private final BitflowParser.SubPipelineContext ctx;

        public SubPipelineBuilder(BitflowParser.SubPipelineContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public Pipeline build() throws IOException {
            return buildSubPipeline(ctx);
        }
    }

    public PipelineStep buildMultiplexFork(BitflowParser.MultiplexForkContext ctx) throws CompilationException {
        List<Pipeline> subPipes = new ArrayList<>();
        for (BitflowParser.MultiplexSubPipelineContext subPipeContext : ctx.multiplexSubPipeline()) {
            subPipes.add(buildSubPipeline(subPipeContext.subPipeline()));
        }
        MultiplexDistributor multiplex = new MultiplexDistributor(subPipes);
        return new Fork(multiplex);
    }

    public Map<String, String> buildParameters(BitflowParser.TransformParametersContext ctx) throws CompilationException {
        Map<String, String> params = new HashMap<>();
        for (BitflowParser.ParameterContext param : ctx.parameter()) {
            String key = unwrap(param.name());
            String value = unwrap(param.val());
            if (params.containsKey(key)) {
                throw new CompilationException(ctx, String.format("Duplicate parameter %s (values '%s' and '%s')", key, params.get(key), value));
            } else {
                params.put(key, value);
            }
        }
        return params;
    }

}
