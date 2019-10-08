package bitflow4j.script;

import bitflow4j.Pipeline;
import bitflow4j.PipelineStep;
import bitflow4j.Source;
import bitflow4j.misc.Pair;
import bitflow4j.script.endpoints.EndpointFactory;
import bitflow4j.script.generated.BitflowLexer;
import bitflow4j.script.generated.BitflowParser;
import bitflow4j.script.registry.*;
import bitflow4j.steps.AbstractBatchPipelineStep;
import bitflow4j.steps.BatchHandler;
import bitflow4j.steps.fork.Fork;
import bitflow4j.steps.fork.ScriptableDistributor;
import bitflow4j.steps.fork.distribute.MultiplexDistributor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
            throw new CompilationException(e.getCause());
        } catch (RecognitionException e) {
            throw new CompilationException(e);
        } catch (Throwable e) {
            throw new CompilationException(e);
        }
    }

    private Pipeline buildPipeline(BitflowParser.ScriptContext parsedScript) throws CompilationException {
        return buildPipeline(parsedScript.pipelines());
    }

    private Pipeline buildPipeline(BitflowParser.PipelinesContext ctx) throws CompilationException {
        if (ctx.pipeline().size() != 1) {
            // TODO implement parallel multi input
            throw new CompilationException(ctx, "Multiple parallel input pipelines are not yet supported");
        }
        return buildPipeline(ctx.pipeline(0));
    }

    private Pipeline buildPipeline(BitflowParser.PipelineContext ctx) throws CompilationException {
        Pipeline pipe = new Pipeline();

        if (ctx.pipelines() != null) {
            // TODO implement
            throw new CompilationException(ctx, "Nested input pipelines are not yet supported");
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
        } else if (ctx.batch() != null) {
            buildBatch(pipe, ctx.batch());
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
            throw new CompilationException(ctx, e);
        }
    }

    private PipelineStep buildOutput(BitflowParser.DataOutputContext ctx) throws CompilationException {
        String output = unwrap(ctx.name());
        try {
            return endpointFactory.createSink(output);
        } catch (IOException e) {
            throw new CompilationException(ctx, e);
        }
    }

    private void buildProcessingStep(Pipeline pipe, BitflowParser.ProcessingStepContext ctx) throws CompilationException {
        String name = unwrap(ctx.name());
        RegisteredStep<ProcessingStepBuilder> regAnalysis = registry.getRegisteredStep(name);
        if (regAnalysis == null) {
            throw new CompilationException(ctx, name, "Unknown processing step");
        }

        Map<String, Object> params = buildParameters(regAnalysis.parameters, ctx.parameters());
        try {
            pipe.step(regAnalysis.builder.buildProcessingStep(params));
        } catch (IOException e) {
            throw new CompilationException(ctx, name, e);
        }
    }

    private void buildFork(Pipeline pipe, BitflowParser.ForkContext ctx) throws CompilationException {
        String forkName = unwrap(ctx.name());
        RegisteredStep<ForkBuilder> forkReg = registry.getRegisteredFork(forkName);
        if (forkReg == null) {
            throw new CompilationException(ctx, forkName, "Unknown fork");
        }
        Map<String, Object> forkParams = buildParameters(forkReg.parameters, ctx.parameters());

        Collection<Pair<String, ScriptableDistributor.PipelineBuilder>> subPipes = new ArrayList<>();
        for (BitflowParser.NamedSubPipelineContext subPipeline : ctx.namedSubPipeline()) {
            SubPipelineBuilder builder = new SubPipelineBuilder(subPipeline.subPipeline());
            for (BitflowParser.NameContext name : subPipeline.name()) {
                subPipes.add(new Pair<>(unwrap(name), builder));
            }
        }

        try {
            ScriptableDistributor distributor = forkReg.builder.buildFork(forkParams);
            try {
                distributor.setSubPipelines(subPipes);
            } catch (IOException e) {
                throw new CompilationException(ctx, forkName, e);
            }
            pipe.step(new Fork(distributor));
        } catch (IOException e) {
            throw new CompilationException(ctx, forkName, e);
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

    private Map<String, Object> buildParameters(RegisteredParameterList params, BitflowParser.ParametersContext ctx) throws CompilationException {
        Map<String, Object> rawParameters = extractRawParameters(ctx);
        return buildExtractedParameters(params, ctx, rawParameters);
    }

    private Map<String, Object> buildExtractedParameters(RegisteredParameterList params, BitflowParser.ParametersContext ctx, Map<String, Object> rawParameters) throws CompilationException {
        try {
            return params.parseRawParameters(rawParameters);
        } catch (IllegalArgumentException e) {
            throw new CompilationException(ctx, e);
        }
    }

    private Map<String, Object> extractRawParameters(BitflowParser.ParametersContext ctx) throws CompilationException {
        Map<String, Object> params = new HashMap<>();
        if (ctx.parameterList() != null) {
            for (BitflowParser.ParameterContext param : ctx.parameterList().parameter()) {
                String key = unwrap(param.name());
                if (params.containsKey(key)) {
                    throw new CompilationException(ctx, String.format("Duplicate parameter %s", key));
                } else {
                    BitflowParser.ParameterValueContext value = param.parameterValue();
                    Object valueObj;
                    if (value.primitiveValue() != null) {
                        valueObj = unwrap(value.primitiveValue().name());
                    } else if (value.listValue() != null) {
                        valueObj = value.listValue().primitiveValue().stream().
                                map(v -> unwrap(v.name())).collect(Collectors.toList());
                    } else if (value.mapValue() != null) {
                        valueObj = value.mapValue().mapValueElement().stream().
                                collect(Collectors.toMap(
                                        e -> unwrap(e.name()),
                                        e -> unwrap(e.primitiveValue().name())));
                    } else {
                        throw new CompilationException(value, "Unexpected parameter value type");
                    }
                    params.put(key, valueObj);
                }
            }
        }
        return params;
    }

    private void buildBatch(Pipeline pipe, BitflowParser.BatchContext batch) {
        Map<String, Object> rawParams = extractRawParameters(batch.parameters());
        Map<String, Object> parsedParams = buildExtractedParameters(AbstractBatchPipelineStep.getParameterList(rawParams), batch.parameters(), rawParams);
        AbstractBatchPipelineStep batchStep = AbstractBatchPipelineStep.createFromParameters(parsedParams);

        BitflowParser.BatchPipelineContext batchPipeline = batch.batchPipeline();
        for (BitflowParser.ProcessingStepContext step : batchPipeline.processingStep()) {
            String name = unwrap(step.name());
            RegisteredStep<BatchStepBuilder> registeredStep = registry.getRegisteredBatchStep(name);
            if (registeredStep == null) {
                throw new CompilationException(step, name, "Unknown batch processing step");
            }
            Map<String, Object> registeredStepParams = buildParameters(registeredStep.parameters, step.parameters());
            try {
                BatchHandler handler = registeredStep.builder.buildBatchStep(registeredStepParams);
                batchStep.addBatchHandler(handler);
            } catch (IOException e) {
                throw new CompilationException(step, name, e);
            }
        }
        pipe.step(batchStep);
    }

}
