// Generated from BitflowQuery.g4 by ANTLR 4.7.1
package bitflow4j.steps.query.generated;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BitflowQueryParser}.
 */
public interface BitflowQueryListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#parse}.
	 * @param ctx the parse tree
	 */
	void enterParse(BitflowQueryParser.ParseContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#parse}.
	 * @param ctx the parse tree
	 */
	void exitParse(BitflowQueryParser.ParseContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#aggregateSelections}.
	 * @param ctx the parse tree
	 */
	void enterAggregateSelections(BitflowQueryParser.AggregateSelectionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#aggregateSelections}.
	 * @param ctx the parse tree
	 */
	void exitAggregateSelections(BitflowQueryParser.AggregateSelectionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#selectAll}.
	 * @param ctx the parse tree
	 */
	void enterSelectAll(BitflowQueryParser.SelectAllContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#selectAll}.
	 * @param ctx the parse tree
	 */
	void exitSelectAll(BitflowQueryParser.SelectAllContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#selectElement}.
	 * @param ctx the parse tree
	 */
	void enterSelectElement(BitflowQueryParser.SelectElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#selectElement}.
	 * @param ctx the parse tree
	 */
	void exitSelectElement(BitflowQueryParser.SelectElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#selectDefault}.
	 * @param ctx the parse tree
	 */
	void enterSelectDefault(BitflowQueryParser.SelectDefaultContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#selectDefault}.
	 * @param ctx the parse tree
	 */
	void exitSelectDefault(BitflowQueryParser.SelectDefaultContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#mathematicalSelection}.
	 * @param ctx the parse tree
	 */
	void enterMathematicalSelection(BitflowQueryParser.MathematicalSelectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#mathematicalSelection}.
	 * @param ctx the parse tree
	 */
	void exitMathematicalSelection(BitflowQueryParser.MathematicalSelectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#leftParen}.
	 * @param ctx the parse tree
	 */
	void enterLeftParen(BitflowQueryParser.LeftParenContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#leftParen}.
	 * @param ctx the parse tree
	 */
	void exitLeftParen(BitflowQueryParser.LeftParenContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#rightParen}.
	 * @param ctx the parse tree
	 */
	void enterRightParen(BitflowQueryParser.RightParenContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#rightParen}.
	 * @param ctx the parse tree
	 */
	void exitRightParen(BitflowQueryParser.RightParenContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#selectFunction}.
	 * @param ctx the parse tree
	 */
	void enterSelectFunction(BitflowQueryParser.SelectFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#selectFunction}.
	 * @param ctx the parse tree
	 */
	void exitSelectFunction(BitflowQueryParser.SelectFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#selectSum}.
	 * @param ctx the parse tree
	 */
	void enterSelectSum(BitflowQueryParser.SelectSumContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#selectSum}.
	 * @param ctx the parse tree
	 */
	void exitSelectSum(BitflowQueryParser.SelectSumContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#selectMin}.
	 * @param ctx the parse tree
	 */
	void enterSelectMin(BitflowQueryParser.SelectMinContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#selectMin}.
	 * @param ctx the parse tree
	 */
	void exitSelectMin(BitflowQueryParser.SelectMinContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#selectMax}.
	 * @param ctx the parse tree
	 */
	void enterSelectMax(BitflowQueryParser.SelectMaxContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#selectMax}.
	 * @param ctx the parse tree
	 */
	void exitSelectMax(BitflowQueryParser.SelectMaxContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#selectAvg}.
	 * @param ctx the parse tree
	 */
	void enterSelectAvg(BitflowQueryParser.SelectAvgContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#selectAvg}.
	 * @param ctx the parse tree
	 */
	void exitSelectAvg(BitflowQueryParser.SelectAvgContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#selectMedian}.
	 * @param ctx the parse tree
	 */
	void enterSelectMedian(BitflowQueryParser.SelectMedianContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#selectMedian}.
	 * @param ctx the parse tree
	 */
	void exitSelectMedian(BitflowQueryParser.SelectMedianContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#selectCount}.
	 * @param ctx the parse tree
	 */
	void enterSelectCount(BitflowQueryParser.SelectCountContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#selectCount}.
	 * @param ctx the parse tree
	 */
	void exitSelectCount(BitflowQueryParser.SelectCountContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#countTag}.
	 * @param ctx the parse tree
	 */
	void enterCountTag(BitflowQueryParser.CountTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#countTag}.
	 * @param ctx the parse tree
	 */
	void exitCountTag(BitflowQueryParser.CountTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#countNorTIMES}.
	 * @param ctx the parse tree
	 */
	void enterCountNorTIMES(BitflowQueryParser.CountNorTIMESContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#countNorTIMES}.
	 * @param ctx the parse tree
	 */
	void exitCountNorTIMES(BitflowQueryParser.CountNorTIMESContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#groupByFunction}.
	 * @param ctx the parse tree
	 */
	void enterGroupByFunction(BitflowQueryParser.GroupByFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#groupByFunction}.
	 * @param ctx the parse tree
	 */
	void exitGroupByFunction(BitflowQueryParser.GroupByFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#whereFunction}.
	 * @param ctx the parse tree
	 */
	void enterWhereFunction(BitflowQueryParser.WhereFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#whereFunction}.
	 * @param ctx the parse tree
	 */
	void exitWhereFunction(BitflowQueryParser.WhereFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#havingFunction}.
	 * @param ctx the parse tree
	 */
	void enterHavingFunction(BitflowQueryParser.HavingFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#havingFunction}.
	 * @param ctx the parse tree
	 */
	void exitHavingFunction(BitflowQueryParser.HavingFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#windowFunction}.
	 * @param ctx the parse tree
	 */
	void enterWindowFunction(BitflowQueryParser.WindowFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#windowFunction}.
	 * @param ctx the parse tree
	 */
	void exitWindowFunction(BitflowQueryParser.WindowFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#windowmode}.
	 * @param ctx the parse tree
	 */
	void enterWindowmode(BitflowQueryParser.WindowmodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#windowmode}.
	 * @param ctx the parse tree
	 */
	void exitWindowmode(BitflowQueryParser.WindowmodeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#allMode}.
	 * @param ctx the parse tree
	 */
	void enterAllMode(BitflowQueryParser.AllModeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#allMode}.
	 * @param ctx the parse tree
	 */
	void exitAllMode(BitflowQueryParser.AllModeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#timeMode}.
	 * @param ctx the parse tree
	 */
	void enterTimeMode(BitflowQueryParser.TimeModeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#timeMode}.
	 * @param ctx the parse tree
	 */
	void exitTimeMode(BitflowQueryParser.TimeModeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#valueMode}.
	 * @param ctx the parse tree
	 */
	void enterValueMode(BitflowQueryParser.ValueModeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#valueMode}.
	 * @param ctx the parse tree
	 */
	void exitValueMode(BitflowQueryParser.ValueModeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#tag}.
	 * @param ctx the parse tree
	 */
	void enterTag(BitflowQueryParser.TagContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#tag}.
	 * @param ctx the parse tree
	 */
	void exitTag(BitflowQueryParser.TagContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#days}.
	 * @param ctx the parse tree
	 */
	void enterDays(BitflowQueryParser.DaysContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#days}.
	 * @param ctx the parse tree
	 */
	void exitDays(BitflowQueryParser.DaysContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#hours}.
	 * @param ctx the parse tree
	 */
	void enterHours(BitflowQueryParser.HoursContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#hours}.
	 * @param ctx the parse tree
	 */
	void exitHours(BitflowQueryParser.HoursContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#minutes}.
	 * @param ctx the parse tree
	 */
	void enterMinutes(BitflowQueryParser.MinutesContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#minutes}.
	 * @param ctx the parse tree
	 */
	void exitMinutes(BitflowQueryParser.MinutesContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#seconds}.
	 * @param ctx the parse tree
	 */
	void enterSeconds(BitflowQueryParser.SecondsContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#seconds}.
	 * @param ctx the parse tree
	 */
	void exitSeconds(BitflowQueryParser.SecondsContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(BitflowQueryParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(BitflowQueryParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#inexpressiontag}.
	 * @param ctx the parse tree
	 */
	void enterInexpressiontag(BitflowQueryParser.InexpressiontagContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#inexpressiontag}.
	 * @param ctx the parse tree
	 */
	void exitInexpressiontag(BitflowQueryParser.InexpressiontagContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#inexpressionmetric}.
	 * @param ctx the parse tree
	 */
	void enterInexpressionmetric(BitflowQueryParser.InexpressionmetricContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#inexpressionmetric}.
	 * @param ctx the parse tree
	 */
	void exitInexpressionmetric(BitflowQueryParser.InexpressionmetricContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#hastag}.
	 * @param ctx the parse tree
	 */
	void enterHastag(BitflowQueryParser.HastagContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#hastag}.
	 * @param ctx the parse tree
	 */
	void exitHastag(BitflowQueryParser.HastagContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#notnode}.
	 * @param ctx the parse tree
	 */
	void enterNotnode(BitflowQueryParser.NotnodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#notnode}.
	 * @param ctx the parse tree
	 */
	void exitNotnode(BitflowQueryParser.NotnodeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#endnode}.
	 * @param ctx the parse tree
	 */
	void enterEndnode(BitflowQueryParser.EndnodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#endnode}.
	 * @param ctx the parse tree
	 */
	void exitEndnode(BitflowQueryParser.EndnodeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#comparator}.
	 * @param ctx the parse tree
	 */
	void enterComparator(BitflowQueryParser.ComparatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#comparator}.
	 * @param ctx the parse tree
	 */
	void exitComparator(BitflowQueryParser.ComparatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#binary}.
	 * @param ctx the parse tree
	 */
	void enterBinary(BitflowQueryParser.BinaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#binary}.
	 * @param ctx the parse tree
	 */
	void exitBinary(BitflowQueryParser.BinaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#mathematicalOperation}.
	 * @param ctx the parse tree
	 */
	void enterMathematicalOperation(BitflowQueryParser.MathematicalOperationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#mathematicalOperation}.
	 * @param ctx the parse tree
	 */
	void exitMathematicalOperation(BitflowQueryParser.MathematicalOperationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#boolToken}.
	 * @param ctx the parse tree
	 */
	void enterBoolToken(BitflowQueryParser.BoolTokenContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#boolToken}.
	 * @param ctx the parse tree
	 */
	void exitBoolToken(BitflowQueryParser.BoolTokenContext ctx);
	/**
	 * Enter a parse tree produced by {@link BitflowQueryParser#list}.
	 * @param ctx the parse tree
	 */
	void enterList(BitflowQueryParser.ListContext ctx);
	/**
	 * Exit a parse tree produced by {@link BitflowQueryParser#list}.
	 * @param ctx the parse tree
	 */
	void exitList(BitflowQueryParser.ListContext ctx);
}