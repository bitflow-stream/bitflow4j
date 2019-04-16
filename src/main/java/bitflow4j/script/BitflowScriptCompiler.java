package bitflow4j.script;

import bitflow4j.Pipeline;
import bitflow4j.PipelineStep;
import bitflow4j.Source;
import bitflow4j.misc.Pair;
import bitflow4j.script.endpoints.EndpointFactory;
import bitflow4j.script.generated.BitflowLexer;
import bitflow4j.script.generated.BitflowParser;
import bitflow4j.script.registry.*;
import bitflow4j.steps.BatchHandler;
import bitflow4j.steps.BatchPipelineStep;
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

    private Pipeline buildPipeline(BitflowParser.ScriptContext parsedScript) throws CompilationException {
        return buildPipeline(parsedScript.pipelines());
    }

    private Pipeline buildPipeline(BitflowParser.PipelinesContext ctx) throws CompilationException {
        if (ctx.pipeline().size() != 1) {
            // TODO implement parallel multi input
            throw new CompilationException("Multiple parallel input pipelines are not yet supported");
        }
        return buildPipeline(ctx.pipeline(0));
    }

    private Pipeline buildPipeline(BitflowParser.PipelineContext ctx) throws CompilationException {
        Pipeline pipe = new Pipeline();

        if (ctx.pipelines() != null) {
            // TODO implement
            throw new CompilationException("Nested input pipelines are not yet supported");
        } else if (ctx.dataInput() != null) {
            pipe.input(buildInput(ctx.dataInput()));
        } else if (ctx.pipelineElement() != null) {
            buildPipelineElement(pipe, ctx.pipelineElement());
        }

        buildPipelineTail(pipe, ctx.pipelineTailElement());
        return pipe;
    }

    private void buildPipelineElement(Pipeline pipe, BitflowParser.PipelineElementContext ctx) throws CompilationException {
        if (ctx.processingStep() != null) {
            buildProcessingStep(pipe, ctx.processingStep());
        } else if (ctx.fork() != null) {
            buildFork(pipe, ctx.fork());
        } else if (ctx.window() != null) {
            buildWindow(pipe, ctx.window());
        }
    }

    private void buildPipelineTail(Pipeline pipe, List<BitflowParser.PipelineTailElementContext> elements) throws CompilationException {
        for (BitflowParser.PipelineTailElementContext elem : elements) {
            if (elem.dataOutput() != null) {
                pipe.step(buildOutput(elem.dataOutput()));
            } else if (elem.multiplexFork() != null) {
                pipe.step(buildMultiplexFork(elem.multiplexFork()));
            } else if (elem.pipelineElement() != null) {
                buildPipelineElement(pipe, elem.pipelineElement());
            }
        }
    }

    private String unwrapSTRING(TerminalNode token) {
        String text = token.getText();
        return text.substring(1, text.length() - 1);
    }

    private String unwrap(BitflowParser.NameContext ctx) {
        return ctx.STRING() == null ? ctx.getText() : unwrapSTRING(ctx.STRING());
    }

    private Source buildInput(BitflowParser.DataInputContext ctx) throws CompilationException {
        String[] inputs = ctx.name().stream().map(this::unwrap).toArray(String[]::new);
        try {
            return endpointFactory.createSource(inputs);
        } catch (IOException e) {
            throw new CompilationException(ctx, "Could not create source: " + e.getMessage());
        }
    }

    private PipelineStep buildOutput(BitflowParser.DataOutputContext ctx) throws CompilationException {
        String output = unwrap(ctx.name());
        try {
            return endpointFactory.createSink(output);
        } catch (IOException e) {
            throw new CompilationException(ctx, "Could not create sink: " + e.getMessage());
        }
    }

    private void buildProcessingStep(Pipeline pipe, BitflowParser.ProcessingStepContext ctx) throws CompilationException {
        String name = unwrap(ctx.name());
        Map<String, String> params = buildParameters(ctx.parameters());

        RegisteredPipelineStep regAnalysis = registry.getAnalysisRegistration(name);
        if (regAnalysis == null) {
            throw new CompilationException(ctx, String.format("Unknown Processor: '%s'", name));
        }
        regAnalysis.validateParameters(params).forEach(e -> {
            throw new CompilationException(ctx, e);
        });

        try {
            regAnalysis.buildStep(pipe, params);
        } catch (ConstructionException e) {
            throw new CompilationException(ctx, e.getStepName() + ": " + e.getMessage());
        }
    }

    private void buildFork(Pipeline pipe, BitflowParser.ForkContext ctx) throws CompilationException {
        Map<String, String> forkParams = buildParameters(ctx.parameters());
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
            forkReg.buildFork(pipe, subPipes, forkParams);
        } catch (ConstructionException e) {
            throw new CompilationException(ctx, e.getStepName() + ": " + e.getMessage());
        }
    }

    private Pipeline buildSubPipeline(BitflowParser.SubPipelineContext ctx) {
        Pipeline pipe = new Pipeline();
        buildPipelineTail(pipe, ctx.pipelineTailElement());
        return pipe;
    }

    private class SubPipelineBuilder implements ScriptableDistributor.PipelineBuilder {

        private final BitflowParser.SubPipelineContext ctx;

        public SubPipelineBuilder(BitflowParser.SubPipelineContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public Pipeline build() throws IOException {
            return buildSubPipeline(ctx);
        }
    }

    private PipelineStep buildMultiplexFork(BitflowParser.MultiplexForkContext ctx) throws CompilationException {
        List<Pipeline> subPipes = new ArrayList<>();
        for (BitflowParser.SubPipelineContext subPipeContext : ctx.subPipeline()) {
            subPipes.add(buildSubPipeline(subPipeContext));
        }
        MultiplexDistributor multiplex = new MultiplexDistributor(subPipes);
        return new Fork(multiplex);
    }

    private Map<String, String> buildParameters(BitflowParser.ParametersContext ctx) throws CompilationException {
        Map<String, String> params = new HashMap<>();
        if (ctx.parameterList() != null) {
            for (BitflowParser.ParameterContext param : ctx.parameterList().parameter()) {
                String key = unwrap(param.name(0));
                String value = unwrap(param.name(1));
                if (params.containsKey(key)) {
                    throw new CompilationException(ctx, String.format("Duplicate parameter %s (values '%s' and '%s')", key, params.get(key), value));
                } else {
                    params.put(key, value);
                }
            }
        }
        return params;
    }

    private void buildWindow(Pipeline pipe, BitflowParser.WindowContext window) {
        Map<String, String> params = buildParameters(window.parameters());
        BatchPipelineStep batchStep = BatchPipelineStep.createFromParameters(params);
        for (BitflowParser.ProcessingStepContext step : window.processingStep()) {
            String name = unwrap(step.name());
            RegisteredBatchStep registeredStep = registry.getBatchStepRegistration(name);
            if (registeredStep == null) {
                throw new CompilationException(step, "Unknown batch processing step: " + name);
            }
            Map<String, String> registeredStepParams = buildParameters(step.parameters());

            try {
                BatchHandler handler = registeredStep.buildStep(registeredStepParams);
                batchStep.addBatchHandler(handler);
            } catch (ConstructionException e) {
                throw new CompilationException(step, e.getStepName() + ": " + e.getMessage());
            }
        }
        pipe.step(batchStep);
    }

}
