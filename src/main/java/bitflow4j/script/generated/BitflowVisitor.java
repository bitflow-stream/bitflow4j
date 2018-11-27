// Generated from Bitflow.g4 by ANTLR 4.7.1
package bitflow4j.script.generated;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link BitflowParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface BitflowVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link BitflowParser#script}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScript(BitflowParser.ScriptContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#outputFork}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputFork(BitflowParser.OutputForkContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#fork}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFork(BitflowParser.ForkContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#window}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWindow(BitflowParser.WindowContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#multiinput}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiinput(BitflowParser.MultiinputContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#input}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput(BitflowParser.InputContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#output}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutput(BitflowParser.OutputContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#transform}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTransform(BitflowParser.TransformContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#subPipeline}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubPipeline(BitflowParser.SubPipelineContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#windowSubPipeline}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWindowSubPipeline(BitflowParser.WindowSubPipelineContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#pipeline}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPipeline(BitflowParser.PipelineContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(BitflowParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#transformParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTransformParameters(BitflowParser.TransformParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(BitflowParser.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#pipelineName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPipelineName(BitflowParser.PipelineNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#schedulingHints}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSchedulingHints(BitflowParser.SchedulingHintsContext ctx);
}