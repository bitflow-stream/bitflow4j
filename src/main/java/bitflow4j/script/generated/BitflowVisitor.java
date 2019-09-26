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
	 * Visit a parse tree produced by {@link BitflowParser#dataInput}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDataInput(BitflowParser.DataInputContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#dataOutput}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDataOutput(BitflowParser.DataOutputContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(BitflowParser.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(BitflowParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#parameterValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterValue(BitflowParser.ParameterValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#primitiveValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveValue(BitflowParser.PrimitiveValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#listValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListValue(BitflowParser.ListValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#mapValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMapValue(BitflowParser.MapValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#mapValueElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMapValueElement(BitflowParser.MapValueElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#parameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterList(BitflowParser.ParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#parameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameters(BitflowParser.ParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#pipelines}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPipelines(BitflowParser.PipelinesContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#pipeline}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPipeline(BitflowParser.PipelineContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#pipelineElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPipelineElement(BitflowParser.PipelineElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#pipelineTailElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPipelineTailElement(BitflowParser.PipelineTailElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#processingStep}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcessingStep(BitflowParser.ProcessingStepContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#fork}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFork(BitflowParser.ForkContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#namedSubPipeline}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamedSubPipeline(BitflowParser.NamedSubPipelineContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#subPipeline}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubPipeline(BitflowParser.SubPipelineContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#batchPipeline}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBatchPipeline(BitflowParser.BatchPipelineContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#multiplexFork}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplexFork(BitflowParser.MultiplexForkContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#batch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBatch(BitflowParser.BatchContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowParser#schedulingHints}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSchedulingHints(BitflowParser.SchedulingHintsContext ctx);
}