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
	 * Enter a parse tree produced by {@link BitflowParser#outputFork}.
	 * @param ctx the parse tree
	 */
	void enterOutputFork(BitflowParser.OutputForkContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#outputFork}.
	 * @param ctx the parse tree
	 */
	void exitOutputFork(BitflowParser.OutputForkContext ctx);
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
	 * Enter a parse tree produced by {@link BitflowParser#multiinput}.
	 * @param ctx the parse tree
	 */
	void enterMultiinput(BitflowParser.MultiinputContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#multiinput}.
	 * @param ctx the parse tree
	 */
	void exitMultiinput(BitflowParser.MultiinputContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#input}.
	 * @param ctx the parse tree
	 */
	void enterInput(BitflowParser.InputContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#input}.
	 * @param ctx the parse tree
	 */
	void exitInput(BitflowParser.InputContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowParser#output}.
	 * @param ctx the parse tree
	 */
	void enterOutput(BitflowParser.OutputContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#output}.
	 * @param ctx the parse tree
	 */
	void exitOutput(BitflowParser.OutputContext ctx);
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
	 * Enter a parse tree produced by {@link BitflowParser#windowSubPipeline}.
	 * @param ctx the parse tree
	 */
	void enterWindowSubPipeline(BitflowParser.WindowSubPipelineContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#windowSubPipeline}.
	 * @param ctx the parse tree
	 */
	void exitWindowSubPipeline(BitflowParser.WindowSubPipelineContext ctx);
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
	 * Enter a parse tree produced by {@link BitflowParser#pipelineName}.
	 * @param ctx the parse tree
	 */
	void enterPipelineName(BitflowParser.PipelineNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowParser#pipelineName}.
	 * @param ctx the parse tree
	 */
	void exitPipelineName(BitflowParser.PipelineNameContext ctx);
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