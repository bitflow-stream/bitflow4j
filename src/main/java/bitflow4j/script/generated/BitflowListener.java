// Generated from Bitflow.g4 by ANTLR 4.7.1
package bitflow4j.script.generated;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BitflowParser}.
 */
public interface BitflowListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link BitflowParser#script}.
	 * @param ctx the parse tree
	 */
	void enterScript(BitflowParser.ScriptContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#script}.
	 * @param ctx the parse tree
	 */
	void exitScript(BitflowParser.ScriptContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#dataInput}.
	 * @param ctx the parse tree
	 */
	void enterDataInput(BitflowParser.DataInputContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#dataInput}.
	 * @param ctx the parse tree
	 */
	void exitDataInput(BitflowParser.DataInputContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#dataOutput}.
	 * @param ctx the parse tree
	 */
	void enterDataOutput(BitflowParser.DataOutputContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#dataOutput}.
	 * @param ctx the parse tree
	 */
	void exitDataOutput(BitflowParser.DataOutputContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(BitflowParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(BitflowParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(BitflowParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(BitflowParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#parameterValue}.
	 * @param ctx the parse tree
	 */
	void enterParameterValue(BitflowParser.ParameterValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#parameterValue}.
	 * @param ctx the parse tree
	 */
	void exitParameterValue(BitflowParser.ParameterValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#primitiveValue}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveValue(BitflowParser.PrimitiveValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#primitiveValue}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveValue(BitflowParser.PrimitiveValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#listValue}.
	 * @param ctx the parse tree
	 */
	void enterListValue(BitflowParser.ListValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#listValue}.
	 * @param ctx the parse tree
	 */
	void exitListValue(BitflowParser.ListValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#mapValue}.
	 * @param ctx the parse tree
	 */
	void enterMapValue(BitflowParser.MapValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#mapValue}.
	 * @param ctx the parse tree
	 */
	void exitMapValue(BitflowParser.MapValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#mapValueElement}.
	 * @param ctx the parse tree
	 */
	void enterMapValueElement(BitflowParser.MapValueElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#mapValueElement}.
	 * @param ctx the parse tree
	 */
	void exitMapValueElement(BitflowParser.MapValueElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(BitflowParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(BitflowParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#parameters}.
	 * @param ctx the parse tree
	 */
	void enterParameters(BitflowParser.ParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#parameters}.
	 * @param ctx the parse tree
	 */
	void exitParameters(BitflowParser.ParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#pipelines}.
	 * @param ctx the parse tree
	 */
	void enterPipelines(BitflowParser.PipelinesContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#pipelines}.
	 * @param ctx the parse tree
	 */
	void exitPipelines(BitflowParser.PipelinesContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#pipeline}.
	 * @param ctx the parse tree
	 */
	void enterPipeline(BitflowParser.PipelineContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#pipeline}.
	 * @param ctx the parse tree
	 */
	void exitPipeline(BitflowParser.PipelineContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#pipelineElement}.
	 * @param ctx the parse tree
	 */
	void enterPipelineElement(BitflowParser.PipelineElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#pipelineElement}.
	 * @param ctx the parse tree
	 */
	void exitPipelineElement(BitflowParser.PipelineElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#pipelineTailElement}.
	 * @param ctx the parse tree
	 */
	void enterPipelineTailElement(BitflowParser.PipelineTailElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#pipelineTailElement}.
	 * @param ctx the parse tree
	 */
	void exitPipelineTailElement(BitflowParser.PipelineTailElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#processingStep}.
	 * @param ctx the parse tree
	 */
	void enterProcessingStep(BitflowParser.ProcessingStepContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#processingStep}.
	 * @param ctx the parse tree
	 */
	void exitProcessingStep(BitflowParser.ProcessingStepContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#fork}.
	 * @param ctx the parse tree
	 */
	void enterFork(BitflowParser.ForkContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#fork}.
	 * @param ctx the parse tree
	 */
	void exitFork(BitflowParser.ForkContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#namedSubPipeline}.
	 * @param ctx the parse tree
	 */
	void enterNamedSubPipeline(BitflowParser.NamedSubPipelineContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#namedSubPipeline}.
	 * @param ctx the parse tree
	 */
	void exitNamedSubPipeline(BitflowParser.NamedSubPipelineContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#subPipeline}.
	 * @param ctx the parse tree
	 */
	void enterSubPipeline(BitflowParser.SubPipelineContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#subPipeline}.
	 * @param ctx the parse tree
	 */
	void exitSubPipeline(BitflowParser.SubPipelineContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#batchPipeline}.
	 * @param ctx the parse tree
	 */
	void enterBatchPipeline(BitflowParser.BatchPipelineContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#batchPipeline}.
	 * @param ctx the parse tree
	 */
	void exitBatchPipeline(BitflowParser.BatchPipelineContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#multiplexFork}.
	 * @param ctx the parse tree
	 */
	void enterMultiplexFork(BitflowParser.MultiplexForkContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#multiplexFork}.
	 * @param ctx the parse tree
	 */
	void exitMultiplexFork(BitflowParser.MultiplexForkContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#batch}.
	 * @param ctx the parse tree
	 */
	void enterBatch(BitflowParser.BatchContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#batch}.
	 * @param ctx the parse tree
	 */
	void exitBatch(BitflowParser.BatchContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#schedulingHints}.
	 * @param ctx the parse tree
	 */
	void enterSchedulingHints(BitflowParser.SchedulingHintsContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#schedulingHints}.
	 * @param ctx the parse tree
	 */
	void exitSchedulingHints(BitflowParser.SchedulingHintsContext ctx);
}