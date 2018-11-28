// Generated from BitflowQuery.g4 by ANTLR 4.7.1
package bitflow4j.steps.query.generated;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class BitflowQueryParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, SELECTKEYWORD=16, 
		WHEREKEYWORD=17, WINDOWKEYWORD=18, GROUPBYKEYWORD=19, HAVINGKEYWORD=20, 
		ALLWORD=21, ASWORD=22, SUMKEYWORD=23, MINKEYWORD=24, MAXKEYWORD=25, AVGKEYWORD=26, 
		MEDIANWORD=27, COUNTWORD=28, TAGWORD=29, FILEWORD=30, INWORD=31, PLUS=32, 
		MINUS=33, TIMES=34, DIVIDED=35, LPAREN=36, RPAREN=37, AND=38, OR=39, NOT=40, 
		TRUE=41, FALSE=42, GT=43, GE=44, LT=45, LE=46, EQ=47, NUMBER=48, STRING=49, 
		DECITIMES=50, IDENTIFIER=51, WS=52;
	public static final int
		RULE_parse = 0, RULE_aggregateSelections = 1, RULE_selectAll = 2, RULE_selectElement = 3, 
		RULE_selectDefault = 4, RULE_mathematicalSelection = 5, RULE_leftParen = 6, 
		RULE_rightParen = 7, RULE_selectFunction = 8, RULE_selectSum = 9, RULE_selectMin = 10, 
		RULE_selectMax = 11, RULE_selectAvg = 12, RULE_selectMedian = 13, RULE_selectCount = 14, 
		RULE_countTag = 15, RULE_countNorTIMES = 16, RULE_groupByFunction = 17, 
		RULE_whereFunction = 18, RULE_havingFunction = 19, RULE_windowFunction = 20, 
		RULE_windowmode = 21, RULE_allMode = 22, RULE_timeMode = 23, RULE_valueMode = 24, 
		RULE_tag = 25, RULE_days = 26, RULE_hours = 27, RULE_minutes = 28, RULE_seconds = 29, 
		RULE_expression = 30, RULE_inexpressiontag = 31, RULE_inexpressionmetric = 32, 
		RULE_hastag = 33, RULE_notnode = 34, RULE_endnode = 35, RULE_comparator = 36, 
		RULE_binary = 37, RULE_mathematicalOperation = 38, RULE_boolToken = 39, 
		RULE_list = 40;
	public static final String[] ruleNames = {
		"parse", "aggregateSelections", "selectAll", "selectElement", "selectDefault", 
		"mathematicalSelection", "leftParen", "rightParen", "selectFunction", 
		"selectSum", "selectMin", "selectMax", "selectAvg", "selectMedian", "selectCount", 
		"countTag", "countNorTIMES", "groupByFunction", "whereFunction", "havingFunction", 
		"windowFunction", "windowmode", "allMode", "timeMode", "valueMode", "tag", 
		"days", "hours", "minutes", "seconds", "expression", "inexpressiontag", 
		"inexpressionmetric", "hastag", "notnode", "endnode", "comparator", "binary", 
		"mathematicalOperation", "boolToken", "list"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "', '", "','", "'*)'", "'d'", "'D'", "'h'", "'H'", "'m'", "'M'", 
		"'s'", "'S'", "'{'", "'}'", "') = \"'", "'\"'", null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		"'+'", "'-'", "'*'", "'/'", "'('", "')'", null, null, null, null, null, 
		"'>'", "'>='", "'<'", "'<='", "'='"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, "SELECTKEYWORD", "WHEREKEYWORD", "WINDOWKEYWORD", 
		"GROUPBYKEYWORD", "HAVINGKEYWORD", "ALLWORD", "ASWORD", "SUMKEYWORD", 
		"MINKEYWORD", "MAXKEYWORD", "AVGKEYWORD", "MEDIANWORD", "COUNTWORD", "TAGWORD", 
		"FILEWORD", "INWORD", "PLUS", "MINUS", "TIMES", "DIVIDED", "LPAREN", "RPAREN", 
		"AND", "OR", "NOT", "TRUE", "FALSE", "GT", "GE", "LT", "LE", "EQ", "NUMBER", 
		"STRING", "DECITIMES", "IDENTIFIER", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "BitflowQuery.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public BitflowQueryParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ParseContext extends ParserRuleContext {
		public AggregateSelectionsContext aggregateSelections() {
			return getRuleContext(AggregateSelectionsContext.class,0);
		}
		public TerminalNode EOF() { return getToken(BitflowQueryParser.EOF, 0); }
		public WhereFunctionContext whereFunction() {
			return getRuleContext(WhereFunctionContext.class,0);
		}
		public GroupByFunctionContext groupByFunction() {
			return getRuleContext(GroupByFunctionContext.class,0);
		}
		public WindowFunctionContext windowFunction() {
			return getRuleContext(WindowFunctionContext.class,0);
		}
		public HavingFunctionContext havingFunction() {
			return getRuleContext(HavingFunctionContext.class,0);
		}
		public ParseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterParse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitParse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitParse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParseContext parse() throws RecognitionException {
		ParseContext _localctx = new ParseContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_parse);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			aggregateSelections();
			setState(84);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHEREKEYWORD) {
				{
				setState(83);
				whereFunction();
				}
			}

			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==GROUPBYKEYWORD) {
				{
				setState(86);
				groupByFunction();
				}
			}

			setState(90);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WINDOWKEYWORD) {
				{
				setState(89);
				windowFunction();
				}
			}

			setState(93);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==HAVINGKEYWORD) {
				{
				setState(92);
				havingFunction();
				}
			}

			setState(95);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AggregateSelectionsContext extends ParserRuleContext {
		public TerminalNode SELECTKEYWORD() { return getToken(BitflowQueryParser.SELECTKEYWORD, 0); }
		public SelectAllContext selectAll() {
			return getRuleContext(SelectAllContext.class,0);
		}
		public List<SelectElementContext> selectElement() {
			return getRuleContexts(SelectElementContext.class);
		}
		public SelectElementContext selectElement(int i) {
			return getRuleContext(SelectElementContext.class,i);
		}
		public AggregateSelectionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregateSelections; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterAggregateSelections(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitAggregateSelections(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitAggregateSelections(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AggregateSelectionsContext aggregateSelections() throws RecognitionException {
		AggregateSelectionsContext _localctx = new AggregateSelectionsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_aggregateSelections);
		try {
			int _alt;
			setState(109);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(97);
				match(SELECTKEYWORD);
				setState(98);
				selectAll();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(99);
				match(SELECTKEYWORD);
				setState(105);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(100);
						selectElement();
						setState(101);
						match(T__0);
						}
						} 
					}
					setState(107);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
				}
				setState(108);
				selectElement();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectAllContext extends ParserRuleContext {
		public TerminalNode ALLWORD() { return getToken(BitflowQueryParser.ALLWORD, 0); }
		public SelectAllContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectAll; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterSelectAll(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitSelectAll(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitSelectAll(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectAllContext selectAll() throws RecognitionException {
		SelectAllContext _localctx = new SelectAllContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_selectAll);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			_la = _input.LA(1);
			if ( !(_la==ALLWORD || _la==TIMES) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectElementContext extends ParserRuleContext {
		public MathematicalSelectionContext mathematicalSelection() {
			return getRuleContext(MathematicalSelectionContext.class,0);
		}
		public TerminalNode ASWORD() { return getToken(BitflowQueryParser.ASWORD, 0); }
		public TerminalNode STRING() { return getToken(BitflowQueryParser.STRING, 0); }
		public SelectFunctionContext selectFunction() {
			return getRuleContext(SelectFunctionContext.class,0);
		}
		public SelectDefaultContext selectDefault() {
			return getRuleContext(SelectDefaultContext.class,0);
		}
		public SelectElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterSelectElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitSelectElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitSelectElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectElementContext selectElement() throws RecognitionException {
		SelectElementContext _localctx = new SelectElementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_selectElement);
		try {
			setState(128);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(113);
				mathematicalSelection(0);
				setState(114);
				match(ASWORD);
				setState(115);
				match(STRING);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(117);
				mathematicalSelection(0);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(118);
				selectFunction();
				setState(119);
				match(ASWORD);
				setState(120);
				match(STRING);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(122);
				selectFunction();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(123);
				selectDefault();
				setState(124);
				match(ASWORD);
				setState(125);
				match(STRING);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(127);
				selectDefault();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectDefaultContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(BitflowQueryParser.STRING, 0); }
		public TerminalNode DECITIMES() { return getToken(BitflowQueryParser.DECITIMES, 0); }
		public TerminalNode NUMBER() { return getToken(BitflowQueryParser.NUMBER, 0); }
		public SelectDefaultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectDefault; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterSelectDefault(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitSelectDefault(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitSelectDefault(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectDefaultContext selectDefault() throws RecognitionException {
		SelectDefaultContext _localctx = new SelectDefaultContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_selectDefault);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NUMBER) | (1L << STRING) | (1L << DECITIMES))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MathematicalSelectionContext extends ParserRuleContext {
		public LeftParenContext leftParen() {
			return getRuleContext(LeftParenContext.class,0);
		}
		public List<MathematicalSelectionContext> mathematicalSelection() {
			return getRuleContexts(MathematicalSelectionContext.class);
		}
		public MathematicalSelectionContext mathematicalSelection(int i) {
			return getRuleContext(MathematicalSelectionContext.class,i);
		}
		public RightParenContext rightParen() {
			return getRuleContext(RightParenContext.class,0);
		}
		public SelectFunctionContext selectFunction() {
			return getRuleContext(SelectFunctionContext.class,0);
		}
		public SelectDefaultContext selectDefault() {
			return getRuleContext(SelectDefaultContext.class,0);
		}
		public MathematicalOperationContext mathematicalOperation() {
			return getRuleContext(MathematicalOperationContext.class,0);
		}
		public MathematicalSelectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mathematicalSelection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterMathematicalSelection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitMathematicalSelection(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitMathematicalSelection(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MathematicalSelectionContext mathematicalSelection() throws RecognitionException {
		return mathematicalSelection(0);
	}

	private MathematicalSelectionContext mathematicalSelection(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		MathematicalSelectionContext _localctx = new MathematicalSelectionContext(_ctx, _parentState);
		MathematicalSelectionContext _prevctx = _localctx;
		int _startState = 10;
		enterRecursionRule(_localctx, 10, RULE_mathematicalSelection, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LPAREN:
				{
				setState(133);
				leftParen();
				setState(134);
				mathematicalSelection(0);
				setState(135);
				rightParen();
				}
				break;
			case SUMKEYWORD:
			case MINKEYWORD:
			case MAXKEYWORD:
			case AVGKEYWORD:
			case MEDIANWORD:
			case COUNTWORD:
				{
				setState(137);
				selectFunction();
				}
				break;
			case NUMBER:
			case STRING:
			case DECITIMES:
				{
				setState(138);
				selectDefault();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(147);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new MathematicalSelectionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_mathematicalSelection);
					setState(141);
					if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
					setState(142);
					mathematicalOperation();
					setState(143);
					mathematicalSelection(4);
					}
					} 
				}
				setState(149);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class LeftParenContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(BitflowQueryParser.LPAREN, 0); }
		public LeftParenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_leftParen; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterLeftParen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitLeftParen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitLeftParen(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LeftParenContext leftParen() throws RecognitionException {
		LeftParenContext _localctx = new LeftParenContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_leftParen);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			match(LPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RightParenContext extends ParserRuleContext {
		public TerminalNode RPAREN() { return getToken(BitflowQueryParser.RPAREN, 0); }
		public RightParenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rightParen; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterRightParen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitRightParen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitRightParen(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RightParenContext rightParen() throws RecognitionException {
		RightParenContext _localctx = new RightParenContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_rightParen);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(152);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectFunctionContext extends ParserRuleContext {
		public SelectSumContext selectSum() {
			return getRuleContext(SelectSumContext.class,0);
		}
		public SelectMinContext selectMin() {
			return getRuleContext(SelectMinContext.class,0);
		}
		public SelectMaxContext selectMax() {
			return getRuleContext(SelectMaxContext.class,0);
		}
		public SelectAvgContext selectAvg() {
			return getRuleContext(SelectAvgContext.class,0);
		}
		public SelectMedianContext selectMedian() {
			return getRuleContext(SelectMedianContext.class,0);
		}
		public SelectCountContext selectCount() {
			return getRuleContext(SelectCountContext.class,0);
		}
		public SelectFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectFunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterSelectFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitSelectFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitSelectFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectFunctionContext selectFunction() throws RecognitionException {
		SelectFunctionContext _localctx = new SelectFunctionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_selectFunction);
		try {
			setState(160);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SUMKEYWORD:
				enterOuterAlt(_localctx, 1);
				{
				setState(154);
				selectSum();
				}
				break;
			case MINKEYWORD:
				enterOuterAlt(_localctx, 2);
				{
				setState(155);
				selectMin();
				}
				break;
			case MAXKEYWORD:
				enterOuterAlt(_localctx, 3);
				{
				setState(156);
				selectMax();
				}
				break;
			case AVGKEYWORD:
				enterOuterAlt(_localctx, 4);
				{
				setState(157);
				selectAvg();
				}
				break;
			case MEDIANWORD:
				enterOuterAlt(_localctx, 5);
				{
				setState(158);
				selectMedian();
				}
				break;
			case COUNTWORD:
				enterOuterAlt(_localctx, 6);
				{
				setState(159);
				selectCount();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectSumContext extends ParserRuleContext {
		public TerminalNode SUMKEYWORD() { return getToken(BitflowQueryParser.SUMKEYWORD, 0); }
		public TerminalNode STRING() { return getToken(BitflowQueryParser.STRING, 0); }
		public SelectSumContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectSum; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterSelectSum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitSelectSum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitSelectSum(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectSumContext selectSum() throws RecognitionException {
		SelectSumContext _localctx = new SelectSumContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_selectSum);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(162);
			match(SUMKEYWORD);
			setState(163);
			match(STRING);
			setState(164);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectMinContext extends ParserRuleContext {
		public TerminalNode MINKEYWORD() { return getToken(BitflowQueryParser.MINKEYWORD, 0); }
		public TerminalNode STRING() { return getToken(BitflowQueryParser.STRING, 0); }
		public SelectMinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectMin; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterSelectMin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitSelectMin(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitSelectMin(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectMinContext selectMin() throws RecognitionException {
		SelectMinContext _localctx = new SelectMinContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_selectMin);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			match(MINKEYWORD);
			setState(167);
			match(STRING);
			setState(168);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectMaxContext extends ParserRuleContext {
		public TerminalNode MAXKEYWORD() { return getToken(BitflowQueryParser.MAXKEYWORD, 0); }
		public TerminalNode STRING() { return getToken(BitflowQueryParser.STRING, 0); }
		public SelectMaxContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectMax; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterSelectMax(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitSelectMax(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitSelectMax(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectMaxContext selectMax() throws RecognitionException {
		SelectMaxContext _localctx = new SelectMaxContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_selectMax);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			match(MAXKEYWORD);
			setState(171);
			match(STRING);
			setState(172);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectAvgContext extends ParserRuleContext {
		public TerminalNode AVGKEYWORD() { return getToken(BitflowQueryParser.AVGKEYWORD, 0); }
		public TerminalNode STRING() { return getToken(BitflowQueryParser.STRING, 0); }
		public SelectAvgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectAvg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterSelectAvg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitSelectAvg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitSelectAvg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectAvgContext selectAvg() throws RecognitionException {
		SelectAvgContext _localctx = new SelectAvgContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_selectAvg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(174);
			match(AVGKEYWORD);
			setState(175);
			match(STRING);
			setState(176);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectMedianContext extends ParserRuleContext {
		public TerminalNode MEDIANWORD() { return getToken(BitflowQueryParser.MEDIANWORD, 0); }
		public TerminalNode STRING() { return getToken(BitflowQueryParser.STRING, 0); }
		public SelectMedianContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectMedian; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterSelectMedian(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitSelectMedian(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitSelectMedian(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectMedianContext selectMedian() throws RecognitionException {
		SelectMedianContext _localctx = new SelectMedianContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_selectMedian);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			match(MEDIANWORD);
			setState(179);
			match(STRING);
			setState(180);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SelectCountContext extends ParserRuleContext {
		public CountTagContext countTag() {
			return getRuleContext(CountTagContext.class,0);
		}
		public CountNorTIMESContext countNorTIMES() {
			return getRuleContext(CountNorTIMESContext.class,0);
		}
		public SelectCountContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectCount; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterSelectCount(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitSelectCount(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitSelectCount(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectCountContext selectCount() throws RecognitionException {
		SelectCountContext _localctx = new SelectCountContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_selectCount);
		try {
			setState(184);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(182);
				countTag();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(183);
				countNorTIMES();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CountTagContext extends ParserRuleContext {
		public TerminalNode COUNTWORD() { return getToken(BitflowQueryParser.COUNTWORD, 0); }
		public List<TerminalNode> STRING() { return getTokens(BitflowQueryParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(BitflowQueryParser.STRING, i);
		}
		public CountTagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_countTag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterCountTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitCountTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitCountTag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CountTagContext countTag() throws RecognitionException {
		CountTagContext _localctx = new CountTagContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_countTag);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(186);
			match(COUNTWORD);
			setState(187);
			match(STRING);
			setState(192);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(188);
				match(T__1);
				setState(189);
				match(STRING);
				}
				}
				setState(194);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(195);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CountNorTIMESContext extends ParserRuleContext {
		public TerminalNode COUNTWORD() { return getToken(BitflowQueryParser.COUNTWORD, 0); }
		public CountNorTIMESContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_countNorTIMES; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterCountNorTIMES(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitCountNorTIMES(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitCountNorTIMES(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CountNorTIMESContext countNorTIMES() throws RecognitionException {
		CountNorTIMESContext _localctx = new CountNorTIMESContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_countNorTIMES);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			match(COUNTWORD);
			setState(198);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GroupByFunctionContext extends ParserRuleContext {
		public TerminalNode GROUPBYKEYWORD() { return getToken(BitflowQueryParser.GROUPBYKEYWORD, 0); }
		public List<TagContext> tag() {
			return getRuleContexts(TagContext.class);
		}
		public TagContext tag(int i) {
			return getRuleContext(TagContext.class,i);
		}
		public GroupByFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_groupByFunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterGroupByFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitGroupByFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitGroupByFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GroupByFunctionContext groupByFunction() throws RecognitionException {
		GroupByFunctionContext _localctx = new GroupByFunctionContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_groupByFunction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			match(GROUPBYKEYWORD);
			setState(201);
			tag();
			setState(206);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(202);
				match(T__0);
				setState(203);
				tag();
				}
				}
				setState(208);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WhereFunctionContext extends ParserRuleContext {
		public TerminalNode WHEREKEYWORD() { return getToken(BitflowQueryParser.WHEREKEYWORD, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public WhereFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whereFunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterWhereFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitWhereFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitWhereFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhereFunctionContext whereFunction() throws RecognitionException {
		WhereFunctionContext _localctx = new WhereFunctionContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_whereFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(209);
			match(WHEREKEYWORD);
			setState(210);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HavingFunctionContext extends ParserRuleContext {
		public TerminalNode HAVINGKEYWORD() { return getToken(BitflowQueryParser.HAVINGKEYWORD, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public HavingFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_havingFunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterHavingFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitHavingFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitHavingFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HavingFunctionContext havingFunction() throws RecognitionException {
		HavingFunctionContext _localctx = new HavingFunctionContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_havingFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(212);
			match(HAVINGKEYWORD);
			setState(213);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WindowFunctionContext extends ParserRuleContext {
		public TerminalNode WINDOWKEYWORD() { return getToken(BitflowQueryParser.WINDOWKEYWORD, 0); }
		public WindowmodeContext windowmode() {
			return getRuleContext(WindowmodeContext.class,0);
		}
		public WindowFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_windowFunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterWindowFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitWindowFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitWindowFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WindowFunctionContext windowFunction() throws RecognitionException {
		WindowFunctionContext _localctx = new WindowFunctionContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_windowFunction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(215);
			match(WINDOWKEYWORD);
			setState(216);
			windowmode();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WindowmodeContext extends ParserRuleContext {
		public AllModeContext allMode() {
			return getRuleContext(AllModeContext.class,0);
		}
		public ValueModeContext valueMode() {
			return getRuleContext(ValueModeContext.class,0);
		}
		public TimeModeContext timeMode() {
			return getRuleContext(TimeModeContext.class,0);
		}
		public WindowmodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_windowmode; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterWindowmode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitWindowmode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitWindowmode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WindowmodeContext windowmode() throws RecognitionException {
		WindowmodeContext _localctx = new WindowmodeContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_windowmode);
		try {
			setState(221);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(218);
				allMode();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(219);
				valueMode();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(220);
				timeMode();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AllModeContext extends ParserRuleContext {
		public TerminalNode ALLWORD() { return getToken(BitflowQueryParser.ALLWORD, 0); }
		public AllModeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_allMode; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterAllMode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitAllMode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitAllMode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AllModeContext allMode() throws RecognitionException {
		AllModeContext _localctx = new AllModeContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_allMode);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(223);
			match(ALLWORD);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TimeModeContext extends ParserRuleContext {
		public TerminalNode FILEWORD() { return getToken(BitflowQueryParser.FILEWORD, 0); }
		public DaysContext days() {
			return getRuleContext(DaysContext.class,0);
		}
		public HoursContext hours() {
			return getRuleContext(HoursContext.class,0);
		}
		public MinutesContext minutes() {
			return getRuleContext(MinutesContext.class,0);
		}
		public SecondsContext seconds() {
			return getRuleContext(SecondsContext.class,0);
		}
		public TimeModeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_timeMode; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterTimeMode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitTimeMode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitTimeMode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeModeContext timeMode() throws RecognitionException {
		TimeModeContext _localctx = new TimeModeContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_timeMode);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(226);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FILEWORD) {
				{
				setState(225);
				match(FILEWORD);
				}
			}

			setState(229);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				{
				setState(228);
				days();
				}
				break;
			}
			setState(232);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				{
				setState(231);
				hours();
				}
				break;
			}
			setState(235);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				{
				setState(234);
				minutes();
				}
				break;
			}
			setState(238);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NUMBER) {
				{
				setState(237);
				seconds();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ValueModeContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(BitflowQueryParser.NUMBER, 0); }
		public ValueModeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_valueMode; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterValueMode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitValueMode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitValueMode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueModeContext valueMode() throws RecognitionException {
		ValueModeContext _localctx = new ValueModeContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_valueMode);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(240);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TagContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(BitflowQueryParser.STRING, 0); }
		public TagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitTag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TagContext tag() throws RecognitionException {
		TagContext _localctx = new TagContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_tag);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(242);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DaysContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(BitflowQueryParser.NUMBER, 0); }
		public DaysContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_days; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterDays(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitDays(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitDays(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DaysContext days() throws RecognitionException {
		DaysContext _localctx = new DaysContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_days);
		try {
			setState(248);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(244);
				match(NUMBER);
				setState(245);
				match(T__3);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(246);
				match(NUMBER);
				setState(247);
				match(T__4);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HoursContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(BitflowQueryParser.NUMBER, 0); }
		public HoursContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hours; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterHours(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitHours(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitHours(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HoursContext hours() throws RecognitionException {
		HoursContext _localctx = new HoursContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_hours);
		try {
			setState(254);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(250);
				match(NUMBER);
				setState(251);
				match(T__5);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(252);
				match(NUMBER);
				setState(253);
				match(T__6);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MinutesContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(BitflowQueryParser.NUMBER, 0); }
		public MinutesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_minutes; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterMinutes(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitMinutes(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitMinutes(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MinutesContext minutes() throws RecognitionException {
		MinutesContext _localctx = new MinutesContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_minutes);
		try {
			setState(260);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(256);
				match(NUMBER);
				setState(257);
				match(T__7);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(258);
				match(NUMBER);
				setState(259);
				match(T__8);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SecondsContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(BitflowQueryParser.NUMBER, 0); }
		public SecondsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_seconds; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterSeconds(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitSeconds(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitSeconds(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SecondsContext seconds() throws RecognitionException {
		SecondsContext _localctx = new SecondsContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_seconds);
		try {
			setState(266);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(262);
				match(NUMBER);
				setState(263);
				match(T__9);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(264);
				match(NUMBER);
				setState(265);
				match(T__10);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext left;
		public ExpressionContext right;
		public TerminalNode LPAREN() { return getToken(BitflowQueryParser.LPAREN, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(BitflowQueryParser.RPAREN, 0); }
		public NotnodeContext notnode() {
			return getRuleContext(NotnodeContext.class,0);
		}
		public BoolTokenContext boolToken() {
			return getRuleContext(BoolTokenContext.class,0);
		}
		public InexpressionmetricContext inexpressionmetric() {
			return getRuleContext(InexpressionmetricContext.class,0);
		}
		public InexpressiontagContext inexpressiontag() {
			return getRuleContext(InexpressiontagContext.class,0);
		}
		public HastagContext hastag() {
			return getRuleContext(HastagContext.class,0);
		}
		public EndnodeContext endnode() {
			return getRuleContext(EndnodeContext.class,0);
		}
		public ComparatorContext comparator() {
			return getRuleContext(ComparatorContext.class,0);
		}
		public BinaryContext binary() {
			return getRuleContext(BinaryContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 60;
		enterRecursionRule(_localctx, 60, RULE_expression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(281);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(269);
				match(LPAREN);
				setState(270);
				expression(0);
				setState(271);
				match(RPAREN);
				}
				break;
			case 2:
				{
				setState(273);
				notnode();
				setState(274);
				expression(8);
				}
				break;
			case 3:
				{
				setState(276);
				boolToken();
				}
				break;
			case 4:
				{
				setState(277);
				inexpressionmetric();
				}
				break;
			case 5:
				{
				setState(278);
				inexpressiontag();
				}
				break;
			case 6:
				{
				setState(279);
				hastag();
				}
				break;
			case 7:
				{
				setState(280);
				endnode();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(293);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(291);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(283);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(284);
						comparator();
						setState(285);
						((ExpressionContext)_localctx).right = expression(8);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						_localctx.left = _prevctx;
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(287);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(288);
						binary();
						setState(289);
						((ExpressionContext)_localctx).right = expression(7);
						}
						break;
					}
					} 
				}
				setState(295);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class InexpressiontagContext extends ParserRuleContext {
		public TerminalNode TAGWORD() { return getToken(BitflowQueryParser.TAGWORD, 0); }
		public List<TerminalNode> STRING() { return getTokens(BitflowQueryParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(BitflowQueryParser.STRING, i);
		}
		public TerminalNode INWORD() { return getToken(BitflowQueryParser.INWORD, 0); }
		public InexpressiontagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inexpressiontag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterInexpressiontag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitInexpressiontag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitInexpressiontag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InexpressiontagContext inexpressiontag() throws RecognitionException {
		InexpressiontagContext _localctx = new InexpressiontagContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_inexpressiontag);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(296);
			match(TAGWORD);
			setState(297);
			match(STRING);
			setState(298);
			match(RPAREN);
			setState(299);
			match(INWORD);
			setState(300);
			match(T__11);
			setState(301);
			match(STRING);
			setState(306);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(302);
				match(T__1);
				setState(303);
				match(STRING);
				}
				}
				setState(308);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(309);
			match(T__12);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InexpressionmetricContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(BitflowQueryParser.STRING, 0); }
		public TerminalNode INWORD() { return getToken(BitflowQueryParser.INWORD, 0); }
		public List<TerminalNode> NUMBER() { return getTokens(BitflowQueryParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(BitflowQueryParser.NUMBER, i);
		}
		public InexpressionmetricContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inexpressionmetric; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterInexpressionmetric(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitInexpressionmetric(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitInexpressionmetric(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InexpressionmetricContext inexpressionmetric() throws RecognitionException {
		InexpressionmetricContext _localctx = new InexpressionmetricContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_inexpressionmetric);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(311);
			match(STRING);
			setState(312);
			match(INWORD);
			setState(313);
			match(T__11);
			setState(314);
			match(NUMBER);
			setState(319);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(315);
				match(T__1);
				setState(316);
				match(NUMBER);
				}
				}
				setState(321);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(322);
			match(T__12);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HastagContext extends ParserRuleContext {
		public TerminalNode TAGWORD() { return getToken(BitflowQueryParser.TAGWORD, 0); }
		public List<TerminalNode> STRING() { return getTokens(BitflowQueryParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(BitflowQueryParser.STRING, i);
		}
		public HastagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hastag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterHastag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitHastag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitHastag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HastagContext hastag() throws RecognitionException {
		HastagContext _localctx = new HastagContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_hastag);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(324);
			match(TAGWORD);
			setState(325);
			match(STRING);
			setState(326);
			match(T__13);
			setState(327);
			match(STRING);
			setState(328);
			match(T__14);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NotnodeContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(BitflowQueryParser.NOT, 0); }
		public NotnodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_notnode; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterNotnode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitNotnode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitNotnode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NotnodeContext notnode() throws RecognitionException {
		NotnodeContext _localctx = new NotnodeContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_notnode);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(330);
			match(NOT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EndnodeContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(BitflowQueryParser.IDENTIFIER, 0); }
		public TerminalNode DECITIMES() { return getToken(BitflowQueryParser.DECITIMES, 0); }
		public TerminalNode STRING() { return getToken(BitflowQueryParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(BitflowQueryParser.NUMBER, 0); }
		public EndnodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_endnode; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterEndnode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitEndnode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitEndnode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EndnodeContext endnode() throws RecognitionException {
		EndnodeContext _localctx = new EndnodeContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_endnode);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(332);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NUMBER) | (1L << STRING) | (1L << DECITIMES) | (1L << IDENTIFIER))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComparatorContext extends ParserRuleContext {
		public TerminalNode GT() { return getToken(BitflowQueryParser.GT, 0); }
		public TerminalNode GE() { return getToken(BitflowQueryParser.GE, 0); }
		public TerminalNode LT() { return getToken(BitflowQueryParser.LT, 0); }
		public TerminalNode LE() { return getToken(BitflowQueryParser.LE, 0); }
		public TerminalNode EQ() { return getToken(BitflowQueryParser.EQ, 0); }
		public ComparatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterComparator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitComparator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitComparator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComparatorContext comparator() throws RecognitionException {
		ComparatorContext _localctx = new ComparatorContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_comparator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(334);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GT) | (1L << GE) | (1L << LT) | (1L << LE) | (1L << EQ))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BinaryContext extends ParserRuleContext {
		public TerminalNode AND() { return getToken(BitflowQueryParser.AND, 0); }
		public TerminalNode OR() { return getToken(BitflowQueryParser.OR, 0); }
		public BinaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_binary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterBinary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitBinary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitBinary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BinaryContext binary() throws RecognitionException {
		BinaryContext _localctx = new BinaryContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_binary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(336);
			_la = _input.LA(1);
			if ( !(_la==AND || _la==OR) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MathematicalOperationContext extends ParserRuleContext {
		public TerminalNode TIMES() { return getToken(BitflowQueryParser.TIMES, 0); }
		public TerminalNode DIVIDED() { return getToken(BitflowQueryParser.DIVIDED, 0); }
		public TerminalNode PLUS() { return getToken(BitflowQueryParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(BitflowQueryParser.MINUS, 0); }
		public MathematicalOperationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mathematicalOperation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterMathematicalOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitMathematicalOperation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitMathematicalOperation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MathematicalOperationContext mathematicalOperation() throws RecognitionException {
		MathematicalOperationContext _localctx = new MathematicalOperationContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_mathematicalOperation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(338);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << TIMES) | (1L << DIVIDED))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BoolTokenContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(BitflowQueryParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(BitflowQueryParser.FALSE, 0); }
		public BoolTokenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolToken; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterBoolToken(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitBoolToken(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitBoolToken(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoolTokenContext boolToken() throws RecognitionException {
		BoolTokenContext _localctx = new BoolTokenContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_boolToken);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(340);
			_la = _input.LA(1);
			if ( !(_la==TRUE || _la==FALSE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListContext extends ParserRuleContext {
		public List<TerminalNode> STRING() { return getTokens(BitflowQueryParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(BitflowQueryParser.STRING, i);
		}
		public ListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).enterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowQueryListener ) ((BitflowQueryListener)listener).exitList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowQueryVisitor ) return ((BitflowQueryVisitor<? extends T>)visitor).visitList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListContext list() throws RecognitionException {
		ListContext _localctx = new ListContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_list);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(346);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(342);
					match(STRING);
					setState(343);
					match(T__1);
					}
					} 
				}
				setState(348);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			}
			setState(349);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 5:
			return mathematicalSelection_sempred((MathematicalSelectionContext)_localctx, predIndex);
		case 30:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean mathematicalSelection_sempred(MathematicalSelectionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 3);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 7);
		case 2:
			return precpred(_ctx, 6);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\66\u0162\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\3\2\3\2"+
		"\5\2W\n\2\3\2\5\2Z\n\2\3\2\5\2]\n\2\3\2\5\2`\n\2\3\2\3\2\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\7\3j\n\3\f\3\16\3m\13\3\3\3\5\3p\n\3\3\4\3\4\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5\u0083\n\5\3\6\3\6\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u008e\n\7\3\7\3\7\3\7\3\7\7\7\u0094\n\7"+
		"\f\7\16\7\u0097\13\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00a3"+
		"\n\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\16\3\16\3\16"+
		"\3\16\3\17\3\17\3\17\3\17\3\20\3\20\5\20\u00bb\n\20\3\21\3\21\3\21\3\21"+
		"\7\21\u00c1\n\21\f\21\16\21\u00c4\13\21\3\21\3\21\3\22\3\22\3\22\3\23"+
		"\3\23\3\23\3\23\7\23\u00cf\n\23\f\23\16\23\u00d2\13\23\3\24\3\24\3\24"+
		"\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\5\27\u00e0\n\27\3\30\3\30"+
		"\3\31\5\31\u00e5\n\31\3\31\5\31\u00e8\n\31\3\31\5\31\u00eb\n\31\3\31\5"+
		"\31\u00ee\n\31\3\31\5\31\u00f1\n\31\3\32\3\32\3\33\3\33\3\34\3\34\3\34"+
		"\3\34\5\34\u00fb\n\34\3\35\3\35\3\35\3\35\5\35\u0101\n\35\3\36\3\36\3"+
		"\36\3\36\5\36\u0107\n\36\3\37\3\37\3\37\3\37\5\37\u010d\n\37\3 \3 \3 "+
		"\3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \5 \u011c\n \3 \3 \3 \3 \3 \3 \3 \3 \7 "+
		"\u0126\n \f \16 \u0129\13 \3!\3!\3!\3!\3!\3!\3!\3!\7!\u0133\n!\f!\16!"+
		"\u0136\13!\3!\3!\3\"\3\"\3\"\3\"\3\"\3\"\7\"\u0140\n\"\f\"\16\"\u0143"+
		"\13\"\3\"\3\"\3#\3#\3#\3#\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)"+
		"\3*\3*\7*\u015b\n*\f*\16*\u015e\13*\3*\3*\3*\2\4\f>+\2\4\6\b\n\f\16\20"+
		"\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNPR\2\t\4\2\27\27"+
		"$$\3\2\62\64\3\2\62\65\3\2-\61\3\2()\3\2\"%\3\2+,\2\u0164\2T\3\2\2\2\4"+
		"o\3\2\2\2\6q\3\2\2\2\b\u0082\3\2\2\2\n\u0084\3\2\2\2\f\u008d\3\2\2\2\16"+
		"\u0098\3\2\2\2\20\u009a\3\2\2\2\22\u00a2\3\2\2\2\24\u00a4\3\2\2\2\26\u00a8"+
		"\3\2\2\2\30\u00ac\3\2\2\2\32\u00b0\3\2\2\2\34\u00b4\3\2\2\2\36\u00ba\3"+
		"\2\2\2 \u00bc\3\2\2\2\"\u00c7\3\2\2\2$\u00ca\3\2\2\2&\u00d3\3\2\2\2(\u00d6"+
		"\3\2\2\2*\u00d9\3\2\2\2,\u00df\3\2\2\2.\u00e1\3\2\2\2\60\u00e4\3\2\2\2"+
		"\62\u00f2\3\2\2\2\64\u00f4\3\2\2\2\66\u00fa\3\2\2\28\u0100\3\2\2\2:\u0106"+
		"\3\2\2\2<\u010c\3\2\2\2>\u011b\3\2\2\2@\u012a\3\2\2\2B\u0139\3\2\2\2D"+
		"\u0146\3\2\2\2F\u014c\3\2\2\2H\u014e\3\2\2\2J\u0150\3\2\2\2L\u0152\3\2"+
		"\2\2N\u0154\3\2\2\2P\u0156\3\2\2\2R\u015c\3\2\2\2TV\5\4\3\2UW\5&\24\2"+
		"VU\3\2\2\2VW\3\2\2\2WY\3\2\2\2XZ\5$\23\2YX\3\2\2\2YZ\3\2\2\2Z\\\3\2\2"+
		"\2[]\5*\26\2\\[\3\2\2\2\\]\3\2\2\2]_\3\2\2\2^`\5(\25\2_^\3\2\2\2_`\3\2"+
		"\2\2`a\3\2\2\2ab\7\2\2\3b\3\3\2\2\2cd\7\22\2\2dp\5\6\4\2ek\7\22\2\2fg"+
		"\5\b\5\2gh\7\3\2\2hj\3\2\2\2if\3\2\2\2jm\3\2\2\2ki\3\2\2\2kl\3\2\2\2l"+
		"n\3\2\2\2mk\3\2\2\2np\5\b\5\2oc\3\2\2\2oe\3\2\2\2p\5\3\2\2\2qr\t\2\2\2"+
		"r\7\3\2\2\2st\5\f\7\2tu\7\30\2\2uv\7\63\2\2v\u0083\3\2\2\2w\u0083\5\f"+
		"\7\2xy\5\22\n\2yz\7\30\2\2z{\7\63\2\2{\u0083\3\2\2\2|\u0083\5\22\n\2}"+
		"~\5\n\6\2~\177\7\30\2\2\177\u0080\7\63\2\2\u0080\u0083\3\2\2\2\u0081\u0083"+
		"\5\n\6\2\u0082s\3\2\2\2\u0082w\3\2\2\2\u0082x\3\2\2\2\u0082|\3\2\2\2\u0082"+
		"}\3\2\2\2\u0082\u0081\3\2\2\2\u0083\t\3\2\2\2\u0084\u0085\t\3\2\2\u0085"+
		"\13\3\2\2\2\u0086\u0087\b\7\1\2\u0087\u0088\5\16\b\2\u0088\u0089\5\f\7"+
		"\2\u0089\u008a\5\20\t\2\u008a\u008e\3\2\2\2\u008b\u008e\5\22\n\2\u008c"+
		"\u008e\5\n\6\2\u008d\u0086\3\2\2\2\u008d\u008b\3\2\2\2\u008d\u008c\3\2"+
		"\2\2\u008e\u0095\3\2\2\2\u008f\u0090\f\5\2\2\u0090\u0091\5N(\2\u0091\u0092"+
		"\5\f\7\6\u0092\u0094\3\2\2\2\u0093\u008f\3\2\2\2\u0094\u0097\3\2\2\2\u0095"+
		"\u0093\3\2\2\2\u0095\u0096\3\2\2\2\u0096\r\3\2\2\2\u0097\u0095\3\2\2\2"+
		"\u0098\u0099\7&\2\2\u0099\17\3\2\2\2\u009a\u009b\7\'\2\2\u009b\21\3\2"+
		"\2\2\u009c\u00a3\5\24\13\2\u009d\u00a3\5\26\f\2\u009e\u00a3\5\30\r\2\u009f"+
		"\u00a3\5\32\16\2\u00a0\u00a3\5\34\17\2\u00a1\u00a3\5\36\20\2\u00a2\u009c"+
		"\3\2\2\2\u00a2\u009d\3\2\2\2\u00a2\u009e\3\2\2\2\u00a2\u009f\3\2\2\2\u00a2"+
		"\u00a0\3\2\2\2\u00a2\u00a1\3\2\2\2\u00a3\23\3\2\2\2\u00a4\u00a5\7\31\2"+
		"\2\u00a5\u00a6\7\63\2\2\u00a6\u00a7\7\'\2\2\u00a7\25\3\2\2\2\u00a8\u00a9"+
		"\7\32\2\2\u00a9\u00aa\7\63\2\2\u00aa\u00ab\7\'\2\2\u00ab\27\3\2\2\2\u00ac"+
		"\u00ad\7\33\2\2\u00ad\u00ae\7\63\2\2\u00ae\u00af\7\'\2\2\u00af\31\3\2"+
		"\2\2\u00b0\u00b1\7\34\2\2\u00b1\u00b2\7\63\2\2\u00b2\u00b3\7\'\2\2\u00b3"+
		"\33\3\2\2\2\u00b4\u00b5\7\35\2\2\u00b5\u00b6\7\63\2\2\u00b6\u00b7\7\'"+
		"\2\2\u00b7\35\3\2\2\2\u00b8\u00bb\5 \21\2\u00b9\u00bb\5\"\22\2\u00ba\u00b8"+
		"\3\2\2\2\u00ba\u00b9\3\2\2\2\u00bb\37\3\2\2\2\u00bc\u00bd\7\36\2\2\u00bd"+
		"\u00c2\7\63\2\2\u00be\u00bf\7\4\2\2\u00bf\u00c1\7\63\2\2\u00c0\u00be\3"+
		"\2\2\2\u00c1\u00c4\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3"+
		"\u00c5\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c5\u00c6\7\'\2\2\u00c6!\3\2\2\2"+
		"\u00c7\u00c8\7\36\2\2\u00c8\u00c9\7\5\2\2\u00c9#\3\2\2\2\u00ca\u00cb\7"+
		"\25\2\2\u00cb\u00d0\5\64\33\2\u00cc\u00cd\7\3\2\2\u00cd\u00cf\5\64\33"+
		"\2\u00ce\u00cc\3\2\2\2\u00cf\u00d2\3\2\2\2\u00d0\u00ce\3\2\2\2\u00d0\u00d1"+
		"\3\2\2\2\u00d1%\3\2\2\2\u00d2\u00d0\3\2\2\2\u00d3\u00d4\7\23\2\2\u00d4"+
		"\u00d5\5> \2\u00d5\'\3\2\2\2\u00d6\u00d7\7\26\2\2\u00d7\u00d8\5> \2\u00d8"+
		")\3\2\2\2\u00d9\u00da\7\24\2\2\u00da\u00db\5,\27\2\u00db+\3\2\2\2\u00dc"+
		"\u00e0\5.\30\2\u00dd\u00e0\5\62\32\2\u00de\u00e0\5\60\31\2\u00df\u00dc"+
		"\3\2\2\2\u00df\u00dd\3\2\2\2\u00df\u00de\3\2\2\2\u00e0-\3\2\2\2\u00e1"+
		"\u00e2\7\27\2\2\u00e2/\3\2\2\2\u00e3\u00e5\7 \2\2\u00e4\u00e3\3\2\2\2"+
		"\u00e4\u00e5\3\2\2\2\u00e5\u00e7\3\2\2\2\u00e6\u00e8\5\66\34\2\u00e7\u00e6"+
		"\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8\u00ea\3\2\2\2\u00e9\u00eb\58\35\2\u00ea"+
		"\u00e9\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb\u00ed\3\2\2\2\u00ec\u00ee\5:"+
		"\36\2\u00ed\u00ec\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee\u00f0\3\2\2\2\u00ef"+
		"\u00f1\5<\37\2\u00f0\u00ef\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1\61\3\2\2"+
		"\2\u00f2\u00f3\7\62\2\2\u00f3\63\3\2\2\2\u00f4\u00f5\7\63\2\2\u00f5\65"+
		"\3\2\2\2\u00f6\u00f7\7\62\2\2\u00f7\u00fb\7\6\2\2\u00f8\u00f9\7\62\2\2"+
		"\u00f9\u00fb\7\7\2\2\u00fa\u00f6\3\2\2\2\u00fa\u00f8\3\2\2\2\u00fb\67"+
		"\3\2\2\2\u00fc\u00fd\7\62\2\2\u00fd\u0101\7\b\2\2\u00fe\u00ff\7\62\2\2"+
		"\u00ff\u0101\7\t\2\2\u0100\u00fc\3\2\2\2\u0100\u00fe\3\2\2\2\u01019\3"+
		"\2\2\2\u0102\u0103\7\62\2\2\u0103\u0107\7\n\2\2\u0104\u0105\7\62\2\2\u0105"+
		"\u0107\7\13\2\2\u0106\u0102\3\2\2\2\u0106\u0104\3\2\2\2\u0107;\3\2\2\2"+
		"\u0108\u0109\7\62\2\2\u0109\u010d\7\f\2\2\u010a\u010b\7\62\2\2\u010b\u010d"+
		"\7\r\2\2\u010c\u0108\3\2\2\2\u010c\u010a\3\2\2\2\u010d=\3\2\2\2\u010e"+
		"\u010f\b \1\2\u010f\u0110\7&\2\2\u0110\u0111\5> \2\u0111\u0112\7\'\2\2"+
		"\u0112\u011c\3\2\2\2\u0113\u0114\5F$\2\u0114\u0115\5> \n\u0115\u011c\3"+
		"\2\2\2\u0116\u011c\5P)\2\u0117\u011c\5B\"\2\u0118\u011c\5@!\2\u0119\u011c"+
		"\5D#\2\u011a\u011c\5H%\2\u011b\u010e\3\2\2\2\u011b\u0113\3\2\2\2\u011b"+
		"\u0116\3\2\2\2\u011b\u0117\3\2\2\2\u011b\u0118\3\2\2\2\u011b\u0119\3\2"+
		"\2\2\u011b\u011a\3\2\2\2\u011c\u0127\3\2\2\2\u011d\u011e\f\t\2\2\u011e"+
		"\u011f\5J&\2\u011f\u0120\5> \n\u0120\u0126\3\2\2\2\u0121\u0122\f\b\2\2"+
		"\u0122\u0123\5L\'\2\u0123\u0124\5> \t\u0124\u0126\3\2\2\2\u0125\u011d"+
		"\3\2\2\2\u0125\u0121\3\2\2\2\u0126\u0129\3\2\2\2\u0127\u0125\3\2\2\2\u0127"+
		"\u0128\3\2\2\2\u0128?\3\2\2\2\u0129\u0127\3\2\2\2\u012a\u012b\7\37\2\2"+
		"\u012b\u012c\7\63\2\2\u012c\u012d\7\'\2\2\u012d\u012e\7!\2\2\u012e\u012f"+
		"\7\16\2\2\u012f\u0134\7\63\2\2\u0130\u0131\7\4\2\2\u0131\u0133\7\63\2"+
		"\2\u0132\u0130\3\2\2\2\u0133\u0136\3\2\2\2\u0134\u0132\3\2\2\2\u0134\u0135"+
		"\3\2\2\2\u0135\u0137\3\2\2\2\u0136\u0134\3\2\2\2\u0137\u0138\7\17\2\2"+
		"\u0138A\3\2\2\2\u0139\u013a\7\63\2\2\u013a\u013b\7!\2\2\u013b\u013c\7"+
		"\16\2\2\u013c\u0141\7\62\2\2\u013d\u013e\7\4\2\2\u013e\u0140\7\62\2\2"+
		"\u013f\u013d\3\2\2\2\u0140\u0143\3\2\2\2\u0141\u013f\3\2\2\2\u0141\u0142"+
		"\3\2\2\2\u0142\u0144\3\2\2\2\u0143\u0141\3\2\2\2\u0144\u0145\7\17\2\2"+
		"\u0145C\3\2\2\2\u0146\u0147\7\37\2\2\u0147\u0148\7\63\2\2\u0148\u0149"+
		"\7\20\2\2\u0149\u014a\7\63\2\2\u014a\u014b\7\21\2\2\u014bE\3\2\2\2\u014c"+
		"\u014d\7*\2\2\u014dG\3\2\2\2\u014e\u014f\t\4\2\2\u014fI\3\2\2\2\u0150"+
		"\u0151\t\5\2\2\u0151K\3\2\2\2\u0152\u0153\t\6\2\2\u0153M\3\2\2\2\u0154"+
		"\u0155\t\7\2\2\u0155O\3\2\2\2\u0156\u0157\t\b\2\2\u0157Q\3\2\2\2\u0158"+
		"\u0159\7\63\2\2\u0159\u015b\7\4\2\2\u015a\u0158\3\2\2\2\u015b\u015e\3"+
		"\2\2\2\u015c\u015a\3\2\2\2\u015c\u015d\3\2\2\2\u015d\u015f\3\2\2\2\u015e"+
		"\u015c\3\2\2\2\u015f\u0160\7\63\2\2\u0160S\3\2\2\2\37VY\\_ko\u0082\u008d"+
		"\u0095\u00a2\u00ba\u00c2\u00d0\u00df\u00e4\u00e7\u00ea\u00ed\u00f0\u00fa"+
		"\u0100\u0106\u010c\u011b\u0125\u0127\u0134\u0141\u015c";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}