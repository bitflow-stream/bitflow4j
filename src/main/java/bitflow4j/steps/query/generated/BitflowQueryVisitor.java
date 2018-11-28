// Generated from BitflowQuery.g4 by ANTLR 4.7.1
package bitflow4j.steps.query.generated;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link BitflowQueryParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface BitflowQueryVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#parse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParse(BitflowQueryParser.ParseContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#aggregateSelections}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggregateSelections(BitflowQueryParser.AggregateSelectionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#selectAll}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectAll(BitflowQueryParser.SelectAllContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#selectElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectElement(BitflowQueryParser.SelectElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#selectDefault}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectDefault(BitflowQueryParser.SelectDefaultContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#mathematicalSelection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathematicalSelection(BitflowQueryParser.MathematicalSelectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#leftParen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLeftParen(BitflowQueryParser.LeftParenContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#rightParen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRightParen(BitflowQueryParser.RightParenContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#selectFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectFunction(BitflowQueryParser.SelectFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#selectSum}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectSum(BitflowQueryParser.SelectSumContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#selectMin}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectMin(BitflowQueryParser.SelectMinContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#selectMax}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectMax(BitflowQueryParser.SelectMaxContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#selectAvg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectAvg(BitflowQueryParser.SelectAvgContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#selectMedian}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectMedian(BitflowQueryParser.SelectMedianContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#selectCount}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectCount(BitflowQueryParser.SelectCountContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#countTag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCountTag(BitflowQueryParser.CountTagContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#countNorTIMES}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCountNorTIMES(BitflowQueryParser.CountNorTIMESContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#groupByFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupByFunction(BitflowQueryParser.GroupByFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#whereFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereFunction(BitflowQueryParser.WhereFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#havingFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHavingFunction(BitflowQueryParser.HavingFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#windowFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWindowFunction(BitflowQueryParser.WindowFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#windowmode}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWindowmode(BitflowQueryParser.WindowmodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#allMode}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAllMode(BitflowQueryParser.AllModeContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#timeMode}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeMode(BitflowQueryParser.TimeModeContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#valueMode}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueMode(BitflowQueryParser.ValueModeContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#tag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTag(BitflowQueryParser.TagContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#days}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDays(BitflowQueryParser.DaysContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#hours}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHours(BitflowQueryParser.HoursContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#minutes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMinutes(BitflowQueryParser.MinutesContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#seconds}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSeconds(BitflowQueryParser.SecondsContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(BitflowQueryParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#inexpressiontag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInexpressiontag(BitflowQueryParser.InexpressiontagContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#inexpressionmetric}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInexpressionmetric(BitflowQueryParser.InexpressionmetricContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#hastag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHastag(BitflowQueryParser.HastagContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#notnode}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotnode(BitflowQueryParser.NotnodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#endnode}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndnode(BitflowQueryParser.EndnodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#comparator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparator(BitflowQueryParser.ComparatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#binary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary(BitflowQueryParser.BinaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#mathematicalOperation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathematicalOperation(BitflowQueryParser.MathematicalOperationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#boolToken}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolToken(BitflowQueryParser.BoolTokenContext ctx);
	/**
	 * Visit a parse tree produced by {@link BitflowQueryParser#list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList(BitflowQueryParser.ListContext ctx);
}