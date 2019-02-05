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
	 * Enter a parse tree produced by {@link BitflowParser#val}.
	 * @param ctx the parse tree
	 */
	void enterVal(BitflowParser.ValContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#val}.
	 * @param ctx the parse tree
	 */
	void exitVal(BitflowParser.ValContext ctx);
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
	 * Enter a parse tree produced by {@link BitflowParser#transformParameters}.
	 * @param ctx the parse tree
	 */
	void enterTransformParameters(BitflowParser.TransformParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#transformParameters}.
	 * @param ctx the parse tree
	 */
	void exitTransformParameters(BitflowParser.TransformParametersContext ctx);
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
	 * Enter a parse tree produced by {@link BitflowParser#multiInputPipeline}.
	 * @param ctx the parse tree
	 */
	void enterMultiInputPipeline(BitflowParser.MultiInputPipelineContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#multiInputPipeline}.
	 * @param ctx the parse tree
	 */
	void exitMultiInputPipeline(BitflowParser.MultiInputPipelineContext ctx);
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
	 * Enter a parse tree produced by {@link BitflowParser#transform}.
	 * @param ctx the parse tree
	 */
	void enterTransform(BitflowParser.TransformContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#transform}.
	 * @param ctx the parse tree
	 */
	void exitTransform(BitflowParser.TransformContext ctx);
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
	 * Enter a parse tree produced by {@link BitflowParser#multiplexSubPipeline}.
	 * @param ctx the parse tree
	 */
	void enterMultiplexSubPipeline(BitflowParser.MultiplexSubPipelineContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#multiplexSubPipeline}.
	 * @param ctx the parse tree
	 */
	void exitMultiplexSubPipeline(BitflowParser.MultiplexSubPipelineContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#window}.
	 * @param ctx the parse tree
	 */
	void enterWindow(BitflowParser.WindowContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#window}.
	 * @param ctx the parse tree
	 */
	void exitWindow(BitflowParser.WindowContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#windowPipeline}.
	 * @param ctx the parse tree
	 */
	void enterWindowPipeline(BitflowParser.WindowPipelineContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#windowPipeline}.
	 * @param ctx the parse tree
	 */
	void exitWindowPipeline(BitflowParser.WindowPipelineContext ctx);
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
	/**
	 * Enter a parse tree produced by {@link BitflowParser#schedulingParameter}.
	 * @param ctx the parse tree
	 */
	void enterSchedulingParameter(BitflowParser.SchedulingParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#schedulingParameter}.
	 * @param ctx the parse tree
	 */
	void exitSchedulingParameter(BitflowParser.SchedulingParameterContext ctx);
}