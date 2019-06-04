// Generated from Bitflow.g4 by ANTLR 4.7.1
package bitflow4j.script.generated;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class BitflowParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		OPEN=1, CLOSE=2, EOP=3, NEXT=4, OPEN_PARAMS=5, CLOSE_PARAMS=6, EQ=7, SEP=8, 
		OPEN_HINTS=9, CLOSE_HINTS=10, WINDOW=11, STRING=12, IDENTIFIER=13, COMMENT=14, 
		NEWLINE=15, WHITESPACE=16, TAB=17;
	public static final int
		RULE_script = 0, RULE_dataInput = 1, RULE_dataOutput = 2, RULE_name = 3, 
		RULE_parameter = 4, RULE_parameterValue = 5, RULE_primitiveValue = 6, 
		RULE_listValue = 7, RULE_mapValue = 8, RULE_mapValueElement = 9, RULE_parameterList = 10, 
		RULE_parameters = 11, RULE_pipelines = 12, RULE_pipeline = 13, RULE_pipelineElement = 14, 
		RULE_pipelineTailElement = 15, RULE_processingStep = 16, RULE_fork = 17, 
		RULE_namedSubPipeline = 18, RULE_subPipeline = 19, RULE_multiplexFork = 20, 
		RULE_window = 21, RULE_schedulingHints = 22;
	public static final String[] ruleNames = {
		"script", "dataInput", "dataOutput", "name", "parameter", "parameterValue", 
		"primitiveValue", "listValue", "mapValue", "mapValueElement", "parameterList", 
		"parameters", "pipelines", "pipeline", "pipelineElement", "pipelineTailElement", 
		"processingStep", "fork", "namedSubPipeline", "subPipeline", "multiplexFork", 
		"window", "schedulingHints"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'{'", "'}'", "';'", "'->'", "'('", "')'", "'='", "','", "'['", 
		"']'", "'batch'", null, null, null, null, null, "'\t'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "OPEN", "CLOSE", "EOP", "NEXT", "OPEN_PARAMS", "CLOSE_PARAMS", "EQ", 
		"SEP", "OPEN_HINTS", "CLOSE_HINTS", "WINDOW", "STRING", "IDENTIFIER", 
		"COMMENT", "NEWLINE", "WHITESPACE", "TAB"
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
	public String getGrammarFileName() { return "Bitflow.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public BitflowParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ScriptContext extends ParserRuleContext {
		public PipelinesContext pipelines() {
			return getRuleContext(PipelinesContext.class,0);
		}
		public TerminalNode EOF() { return getToken(BitflowParser.EOF, 0); }
		public ScriptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_script; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterScript(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitScript(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitScript(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScriptContext script() throws RecognitionException {
		ScriptContext _localctx = new ScriptContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_script);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			pipelines();
			setState(47);
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

	public static class DataInputContext extends ParserRuleContext {
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public SchedulingHintsContext schedulingHints() {
			return getRuleContext(SchedulingHintsContext.class,0);
		}
		public DataInputContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dataInput; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterDataInput(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitDataInput(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitDataInput(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DataInputContext dataInput() throws RecognitionException {
		DataInputContext _localctx = new DataInputContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_dataInput);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(49);
				name();
				}
				}
				setState(52); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==STRING || _la==IDENTIFIER );
			setState(55);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_HINTS) {
				{
				setState(54);
				schedulingHints();
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

	public static class DataOutputContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public SchedulingHintsContext schedulingHints() {
			return getRuleContext(SchedulingHintsContext.class,0);
		}
		public DataOutputContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dataOutput; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterDataOutput(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitDataOutput(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitDataOutput(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DataOutputContext dataOutput() throws RecognitionException {
		DataOutputContext _localctx = new DataOutputContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_dataOutput);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(57);
			name();
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_HINTS) {
				{
				setState(58);
				schedulingHints();
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

	public static class NameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(BitflowParser.IDENTIFIER, 0); }
		public TerminalNode STRING() { return getToken(BitflowParser.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61);
			_la = _input.LA(1);
			if ( !(_la==STRING || _la==IDENTIFIER) ) {
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

	public static class ParameterContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TerminalNode EQ() { return getToken(BitflowParser.EQ, 0); }
		public ParameterValueContext parameterValue() {
			return getRuleContext(ParameterValueContext.class,0);
		}
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitParameter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_parameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			name();
			setState(64);
			match(EQ);
			setState(65);
			parameterValue();
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

	public static class ParameterValueContext extends ParserRuleContext {
		public PrimitiveValueContext primitiveValue() {
			return getRuleContext(PrimitiveValueContext.class,0);
		}
		public ListValueContext listValue() {
			return getRuleContext(ListValueContext.class,0);
		}
		public MapValueContext mapValue() {
			return getRuleContext(MapValueContext.class,0);
		}
		public ParameterValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameterValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterParameterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitParameterValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitParameterValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterValueContext parameterValue() throws RecognitionException {
		ParameterValueContext _localctx = new ParameterValueContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_parameterValue);
		try {
			setState(70);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(67);
				primitiveValue();
				}
				break;
			case OPEN_HINTS:
				enterOuterAlt(_localctx, 2);
				{
				setState(68);
				listValue();
				}
				break;
			case OPEN:
				enterOuterAlt(_localctx, 3);
				{
				setState(69);
				mapValue();
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

	public static class PrimitiveValueContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public PrimitiveValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primitiveValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterPrimitiveValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitPrimitiveValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitPrimitiveValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimitiveValueContext primitiveValue() throws RecognitionException {
		PrimitiveValueContext _localctx = new PrimitiveValueContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_primitiveValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			name();
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

	public static class ListValueContext extends ParserRuleContext {
		public TerminalNode OPEN_HINTS() { return getToken(BitflowParser.OPEN_HINTS, 0); }
		public TerminalNode CLOSE_HINTS() { return getToken(BitflowParser.CLOSE_HINTS, 0); }
		public List<PrimitiveValueContext> primitiveValue() {
			return getRuleContexts(PrimitiveValueContext.class);
		}
		public PrimitiveValueContext primitiveValue(int i) {
			return getRuleContext(PrimitiveValueContext.class,i);
		}
		public List<TerminalNode> SEP() { return getTokens(BitflowParser.SEP); }
		public TerminalNode SEP(int i) {
			return getToken(BitflowParser.SEP, i);
		}
		public ListValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterListValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitListValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitListValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListValueContext listValue() throws RecognitionException {
		ListValueContext _localctx = new ListValueContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_listValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(OPEN_HINTS);
			setState(83);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING || _la==IDENTIFIER) {
				{
				setState(75);
				primitiveValue();
				setState(80);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==SEP) {
					{
					{
					setState(76);
					match(SEP);
					setState(77);
					primitiveValue();
					}
					}
					setState(82);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(85);
			match(CLOSE_HINTS);
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

	public static class MapValueContext extends ParserRuleContext {
		public TerminalNode OPEN() { return getToken(BitflowParser.OPEN, 0); }
		public TerminalNode CLOSE() { return getToken(BitflowParser.CLOSE, 0); }
		public List<MapValueElementContext> mapValueElement() {
			return getRuleContexts(MapValueElementContext.class);
		}
		public MapValueElementContext mapValueElement(int i) {
			return getRuleContext(MapValueElementContext.class,i);
		}
		public List<TerminalNode> SEP() { return getTokens(BitflowParser.SEP); }
		public TerminalNode SEP(int i) {
			return getToken(BitflowParser.SEP, i);
		}
		public MapValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mapValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterMapValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitMapValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitMapValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MapValueContext mapValue() throws RecognitionException {
		MapValueContext _localctx = new MapValueContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_mapValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			match(OPEN);
			setState(96);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING || _la==IDENTIFIER) {
				{
				setState(88);
				mapValueElement();
				setState(93);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==SEP) {
					{
					{
					setState(89);
					match(SEP);
					setState(90);
					mapValueElement();
					}
					}
					setState(95);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(98);
			match(CLOSE);
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

	public static class MapValueElementContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TerminalNode EQ() { return getToken(BitflowParser.EQ, 0); }
		public PrimitiveValueContext primitiveValue() {
			return getRuleContext(PrimitiveValueContext.class,0);
		}
		public MapValueElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mapValueElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterMapValueElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitMapValueElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitMapValueElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MapValueElementContext mapValueElement() throws RecognitionException {
		MapValueElementContext _localctx = new MapValueElementContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_mapValueElement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100);
			name();
			setState(101);
			match(EQ);
			setState(102);
			primitiveValue();
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

	public static class ParameterListContext extends ParserRuleContext {
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public List<TerminalNode> SEP() { return getTokens(BitflowParser.SEP); }
		public TerminalNode SEP(int i) {
			return getToken(BitflowParser.SEP, i);
		}
		public ParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitParameterList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitParameterList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterListContext parameterList() throws RecognitionException {
		ParameterListContext _localctx = new ParameterListContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_parameterList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
			parameter();
			setState(109);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(105);
					match(SEP);
					setState(106);
					parameter();
					}
					} 
				}
				setState(111);
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
			exitRule();
		}
		return _localctx;
	}

	public static class ParametersContext extends ParserRuleContext {
		public TerminalNode OPEN_PARAMS() { return getToken(BitflowParser.OPEN_PARAMS, 0); }
		public TerminalNode CLOSE_PARAMS() { return getToken(BitflowParser.CLOSE_PARAMS, 0); }
		public ParameterListContext parameterList() {
			return getRuleContext(ParameterListContext.class,0);
		}
		public TerminalNode SEP() { return getToken(BitflowParser.SEP, 0); }
		public ParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitParameters(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitParameters(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParametersContext parameters() throws RecognitionException {
		ParametersContext _localctx = new ParametersContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_parameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			match(OPEN_PARAMS);
			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING || _la==IDENTIFIER) {
				{
				setState(113);
				parameterList();
				setState(115);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEP) {
					{
					setState(114);
					match(SEP);
					}
				}

				}
			}

			setState(119);
			match(CLOSE_PARAMS);
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

	public static class PipelinesContext extends ParserRuleContext {
		public List<PipelineContext> pipeline() {
			return getRuleContexts(PipelineContext.class);
		}
		public PipelineContext pipeline(int i) {
			return getRuleContext(PipelineContext.class,i);
		}
		public List<TerminalNode> EOP() { return getTokens(BitflowParser.EOP); }
		public TerminalNode EOP(int i) {
			return getToken(BitflowParser.EOP, i);
		}
		public PipelinesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pipelines; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterPipelines(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitPipelines(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitPipelines(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PipelinesContext pipelines() throws RecognitionException {
		PipelinesContext _localctx = new PipelinesContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_pipelines);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(121);
			pipeline();
			setState(126);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(122);
					match(EOP);
					setState(123);
					pipeline();
					}
					} 
				}
				setState(128);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			}
			setState(130);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOP) {
				{
				setState(129);
				match(EOP);
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

	public static class PipelineContext extends ParserRuleContext {
		public DataInputContext dataInput() {
			return getRuleContext(DataInputContext.class,0);
		}
		public PipelineElementContext pipelineElement() {
			return getRuleContext(PipelineElementContext.class,0);
		}
		public TerminalNode OPEN() { return getToken(BitflowParser.OPEN, 0); }
		public PipelinesContext pipelines() {
			return getRuleContext(PipelinesContext.class,0);
		}
		public TerminalNode CLOSE() { return getToken(BitflowParser.CLOSE, 0); }
		public List<TerminalNode> NEXT() { return getTokens(BitflowParser.NEXT); }
		public TerminalNode NEXT(int i) {
			return getToken(BitflowParser.NEXT, i);
		}
		public List<PipelineTailElementContext> pipelineTailElement() {
			return getRuleContexts(PipelineTailElementContext.class);
		}
		public PipelineTailElementContext pipelineTailElement(int i) {
			return getRuleContext(PipelineTailElementContext.class,i);
		}
		public PipelineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pipeline; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterPipeline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitPipeline(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitPipeline(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PipelineContext pipeline() throws RecognitionException {
		PipelineContext _localctx = new PipelineContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_pipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(138);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(132);
				dataInput();
				}
				break;
			case 2:
				{
				setState(133);
				pipelineElement();
				}
				break;
			case 3:
				{
				setState(134);
				match(OPEN);
				setState(135);
				pipelines();
				setState(136);
				match(CLOSE);
				}
				break;
			}
			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEXT) {
				{
				{
				setState(140);
				match(NEXT);
				setState(141);
				pipelineTailElement();
				}
				}
				setState(146);
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

	public static class PipelineElementContext extends ParserRuleContext {
		public ProcessingStepContext processingStep() {
			return getRuleContext(ProcessingStepContext.class,0);
		}
		public ForkContext fork() {
			return getRuleContext(ForkContext.class,0);
		}
		public WindowContext window() {
			return getRuleContext(WindowContext.class,0);
		}
		public PipelineElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pipelineElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterPipelineElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitPipelineElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitPipelineElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PipelineElementContext pipelineElement() throws RecognitionException {
		PipelineElementContext _localctx = new PipelineElementContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_pipelineElement);
		try {
			setState(150);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(147);
				processingStep();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(148);
				fork();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(149);
				window();
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

	public static class PipelineTailElementContext extends ParserRuleContext {
		public PipelineElementContext pipelineElement() {
			return getRuleContext(PipelineElementContext.class,0);
		}
		public MultiplexForkContext multiplexFork() {
			return getRuleContext(MultiplexForkContext.class,0);
		}
		public DataOutputContext dataOutput() {
			return getRuleContext(DataOutputContext.class,0);
		}
		public PipelineTailElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pipelineTailElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterPipelineTailElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitPipelineTailElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitPipelineTailElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PipelineTailElementContext pipelineTailElement() throws RecognitionException {
		PipelineTailElementContext _localctx = new PipelineTailElementContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_pipelineTailElement);
		try {
			setState(155);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(152);
				pipelineElement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(153);
				multiplexFork();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(154);
				dataOutput();
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

	public static class ProcessingStepContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public SchedulingHintsContext schedulingHints() {
			return getRuleContext(SchedulingHintsContext.class,0);
		}
		public ProcessingStepContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_processingStep; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterProcessingStep(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitProcessingStep(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitProcessingStep(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProcessingStepContext processingStep() throws RecognitionException {
		ProcessingStepContext _localctx = new ProcessingStepContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_processingStep);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157);
			name();
			setState(158);
			parameters();
			setState(160);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_HINTS) {
				{
				setState(159);
				schedulingHints();
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

	public static class ForkContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public TerminalNode OPEN() { return getToken(BitflowParser.OPEN, 0); }
		public List<NamedSubPipelineContext> namedSubPipeline() {
			return getRuleContexts(NamedSubPipelineContext.class);
		}
		public NamedSubPipelineContext namedSubPipeline(int i) {
			return getRuleContext(NamedSubPipelineContext.class,i);
		}
		public TerminalNode CLOSE() { return getToken(BitflowParser.CLOSE, 0); }
		public SchedulingHintsContext schedulingHints() {
			return getRuleContext(SchedulingHintsContext.class,0);
		}
		public List<TerminalNode> EOP() { return getTokens(BitflowParser.EOP); }
		public TerminalNode EOP(int i) {
			return getToken(BitflowParser.EOP, i);
		}
		public ForkContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fork; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterFork(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitFork(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitFork(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForkContext fork() throws RecognitionException {
		ForkContext _localctx = new ForkContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_fork);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(162);
			name();
			setState(163);
			parameters();
			setState(165);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_HINTS) {
				{
				setState(164);
				schedulingHints();
				}
			}

			setState(167);
			match(OPEN);
			setState(168);
			namedSubPipeline();
			setState(173);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(169);
					match(EOP);
					setState(170);
					namedSubPipeline();
					}
					} 
				}
				setState(175);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			}
			setState(177);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOP) {
				{
				setState(176);
				match(EOP);
				}
			}

			setState(179);
			match(CLOSE);
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

	public static class NamedSubPipelineContext extends ParserRuleContext {
		public TerminalNode NEXT() { return getToken(BitflowParser.NEXT, 0); }
		public SubPipelineContext subPipeline() {
			return getRuleContext(SubPipelineContext.class,0);
		}
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public NamedSubPipelineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_namedSubPipeline; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterNamedSubPipeline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitNamedSubPipeline(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitNamedSubPipeline(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NamedSubPipelineContext namedSubPipeline() throws RecognitionException {
		NamedSubPipelineContext _localctx = new NamedSubPipelineContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_namedSubPipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(182); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(181);
				name();
				}
				}
				setState(184); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==STRING || _la==IDENTIFIER );
			setState(186);
			match(NEXT);
			setState(187);
			subPipeline();
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

	public static class SubPipelineContext extends ParserRuleContext {
		public List<PipelineTailElementContext> pipelineTailElement() {
			return getRuleContexts(PipelineTailElementContext.class);
		}
		public PipelineTailElementContext pipelineTailElement(int i) {
			return getRuleContext(PipelineTailElementContext.class,i);
		}
		public List<TerminalNode> NEXT() { return getTokens(BitflowParser.NEXT); }
		public TerminalNode NEXT(int i) {
			return getToken(BitflowParser.NEXT, i);
		}
		public SubPipelineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subPipeline; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterSubPipeline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitSubPipeline(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitSubPipeline(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SubPipelineContext subPipeline() throws RecognitionException {
		SubPipelineContext _localctx = new SubPipelineContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_subPipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(189);
			pipelineTailElement();
			setState(194);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEXT) {
				{
				{
				setState(190);
				match(NEXT);
				setState(191);
				pipelineTailElement();
				}
				}
				setState(196);
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

	public static class MultiplexForkContext extends ParserRuleContext {
		public TerminalNode OPEN() { return getToken(BitflowParser.OPEN, 0); }
		public List<SubPipelineContext> subPipeline() {
			return getRuleContexts(SubPipelineContext.class);
		}
		public SubPipelineContext subPipeline(int i) {
			return getRuleContext(SubPipelineContext.class,i);
		}
		public TerminalNode CLOSE() { return getToken(BitflowParser.CLOSE, 0); }
		public List<TerminalNode> EOP() { return getTokens(BitflowParser.EOP); }
		public TerminalNode EOP(int i) {
			return getToken(BitflowParser.EOP, i);
		}
		public MultiplexForkContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplexFork; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterMultiplexFork(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitMultiplexFork(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitMultiplexFork(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiplexForkContext multiplexFork() throws RecognitionException {
		MultiplexForkContext _localctx = new MultiplexForkContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_multiplexFork);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			match(OPEN);
			setState(198);
			subPipeline();
			setState(203);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(199);
					match(EOP);
					setState(200);
					subPipeline();
					}
					} 
				}
				setState(205);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			}
			setState(207);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOP) {
				{
				setState(206);
				match(EOP);
				}
			}

			setState(209);
			match(CLOSE);
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

	public static class WindowContext extends ParserRuleContext {
		public TerminalNode WINDOW() { return getToken(BitflowParser.WINDOW, 0); }
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public TerminalNode OPEN() { return getToken(BitflowParser.OPEN, 0); }
		public List<ProcessingStepContext> processingStep() {
			return getRuleContexts(ProcessingStepContext.class);
		}
		public ProcessingStepContext processingStep(int i) {
			return getRuleContext(ProcessingStepContext.class,i);
		}
		public TerminalNode CLOSE() { return getToken(BitflowParser.CLOSE, 0); }
		public SchedulingHintsContext schedulingHints() {
			return getRuleContext(SchedulingHintsContext.class,0);
		}
		public List<TerminalNode> NEXT() { return getTokens(BitflowParser.NEXT); }
		public TerminalNode NEXT(int i) {
			return getToken(BitflowParser.NEXT, i);
		}
		public WindowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_window; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterWindow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitWindow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitWindow(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WindowContext window() throws RecognitionException {
		WindowContext _localctx = new WindowContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_window);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			match(WINDOW);
			setState(212);
			parameters();
			setState(214);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_HINTS) {
				{
				setState(213);
				schedulingHints();
				}
			}

			setState(216);
			match(OPEN);
			setState(217);
			processingStep();
			setState(222);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEXT) {
				{
				{
				setState(218);
				match(NEXT);
				setState(219);
				processingStep();
				}
				}
				setState(224);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(225);
			match(CLOSE);
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

	public static class SchedulingHintsContext extends ParserRuleContext {
		public TerminalNode OPEN_HINTS() { return getToken(BitflowParser.OPEN_HINTS, 0); }
		public TerminalNode CLOSE_HINTS() { return getToken(BitflowParser.CLOSE_HINTS, 0); }
		public ParameterListContext parameterList() {
			return getRuleContext(ParameterListContext.class,0);
		}
		public TerminalNode SEP() { return getToken(BitflowParser.SEP, 0); }
		public SchedulingHintsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schedulingHints; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterSchedulingHints(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitSchedulingHints(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitSchedulingHints(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SchedulingHintsContext schedulingHints() throws RecognitionException {
		SchedulingHintsContext _localctx = new SchedulingHintsContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_schedulingHints);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(227);
			match(OPEN_HINTS);
			setState(232);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING || _la==IDENTIFIER) {
				{
				setState(228);
				parameterList();
				setState(230);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEP) {
					{
					setState(229);
					match(SEP);
					}
				}

				}
			}

			setState(234);
			match(CLOSE_HINTS);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\23\u00ef\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\3\2\3\2\3"+
		"\2\3\3\6\3\65\n\3\r\3\16\3\66\3\3\5\3:\n\3\3\4\3\4\5\4>\n\4\3\5\3\5\3"+
		"\6\3\6\3\6\3\6\3\7\3\7\3\7\5\7I\n\7\3\b\3\b\3\t\3\t\3\t\3\t\7\tQ\n\t\f"+
		"\t\16\tT\13\t\5\tV\n\t\3\t\3\t\3\n\3\n\3\n\3\n\7\n^\n\n\f\n\16\na\13\n"+
		"\5\nc\n\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\7\fn\n\f\f\f\16\fq\13"+
		"\f\3\r\3\r\3\r\5\rv\n\r\5\rx\n\r\3\r\3\r\3\16\3\16\3\16\7\16\177\n\16"+
		"\f\16\16\16\u0082\13\16\3\16\5\16\u0085\n\16\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\5\17\u008d\n\17\3\17\3\17\7\17\u0091\n\17\f\17\16\17\u0094\13\17"+
		"\3\20\3\20\3\20\5\20\u0099\n\20\3\21\3\21\3\21\5\21\u009e\n\21\3\22\3"+
		"\22\3\22\5\22\u00a3\n\22\3\23\3\23\3\23\5\23\u00a8\n\23\3\23\3\23\3\23"+
		"\3\23\7\23\u00ae\n\23\f\23\16\23\u00b1\13\23\3\23\5\23\u00b4\n\23\3\23"+
		"\3\23\3\24\6\24\u00b9\n\24\r\24\16\24\u00ba\3\24\3\24\3\24\3\25\3\25\3"+
		"\25\7\25\u00c3\n\25\f\25\16\25\u00c6\13\25\3\26\3\26\3\26\3\26\7\26\u00cc"+
		"\n\26\f\26\16\26\u00cf\13\26\3\26\5\26\u00d2\n\26\3\26\3\26\3\27\3\27"+
		"\3\27\5\27\u00d9\n\27\3\27\3\27\3\27\3\27\7\27\u00df\n\27\f\27\16\27\u00e2"+
		"\13\27\3\27\3\27\3\30\3\30\3\30\5\30\u00e9\n\30\5\30\u00eb\n\30\3\30\3"+
		"\30\3\30\2\2\31\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\2\3\3"+
		"\2\16\17\2\u00f8\2\60\3\2\2\2\4\64\3\2\2\2\6;\3\2\2\2\b?\3\2\2\2\nA\3"+
		"\2\2\2\fH\3\2\2\2\16J\3\2\2\2\20L\3\2\2\2\22Y\3\2\2\2\24f\3\2\2\2\26j"+
		"\3\2\2\2\30r\3\2\2\2\32{\3\2\2\2\34\u008c\3\2\2\2\36\u0098\3\2\2\2 \u009d"+
		"\3\2\2\2\"\u009f\3\2\2\2$\u00a4\3\2\2\2&\u00b8\3\2\2\2(\u00bf\3\2\2\2"+
		"*\u00c7\3\2\2\2,\u00d5\3\2\2\2.\u00e5\3\2\2\2\60\61\5\32\16\2\61\62\7"+
		"\2\2\3\62\3\3\2\2\2\63\65\5\b\5\2\64\63\3\2\2\2\65\66\3\2\2\2\66\64\3"+
		"\2\2\2\66\67\3\2\2\2\679\3\2\2\28:\5.\30\298\3\2\2\29:\3\2\2\2:\5\3\2"+
		"\2\2;=\5\b\5\2<>\5.\30\2=<\3\2\2\2=>\3\2\2\2>\7\3\2\2\2?@\t\2\2\2@\t\3"+
		"\2\2\2AB\5\b\5\2BC\7\t\2\2CD\5\f\7\2D\13\3\2\2\2EI\5\16\b\2FI\5\20\t\2"+
		"GI\5\22\n\2HE\3\2\2\2HF\3\2\2\2HG\3\2\2\2I\r\3\2\2\2JK\5\b\5\2K\17\3\2"+
		"\2\2LU\7\13\2\2MR\5\16\b\2NO\7\n\2\2OQ\5\16\b\2PN\3\2\2\2QT\3\2\2\2RP"+
		"\3\2\2\2RS\3\2\2\2SV\3\2\2\2TR\3\2\2\2UM\3\2\2\2UV\3\2\2\2VW\3\2\2\2W"+
		"X\7\f\2\2X\21\3\2\2\2Yb\7\3\2\2Z_\5\24\13\2[\\\7\n\2\2\\^\5\24\13\2]["+
		"\3\2\2\2^a\3\2\2\2_]\3\2\2\2_`\3\2\2\2`c\3\2\2\2a_\3\2\2\2bZ\3\2\2\2b"+
		"c\3\2\2\2cd\3\2\2\2de\7\4\2\2e\23\3\2\2\2fg\5\b\5\2gh\7\t\2\2hi\5\16\b"+
		"\2i\25\3\2\2\2jo\5\n\6\2kl\7\n\2\2ln\5\n\6\2mk\3\2\2\2nq\3\2\2\2om\3\2"+
		"\2\2op\3\2\2\2p\27\3\2\2\2qo\3\2\2\2rw\7\7\2\2su\5\26\f\2tv\7\n\2\2ut"+
		"\3\2\2\2uv\3\2\2\2vx\3\2\2\2ws\3\2\2\2wx\3\2\2\2xy\3\2\2\2yz\7\b\2\2z"+
		"\31\3\2\2\2{\u0080\5\34\17\2|}\7\5\2\2}\177\5\34\17\2~|\3\2\2\2\177\u0082"+
		"\3\2\2\2\u0080~\3\2\2\2\u0080\u0081\3\2\2\2\u0081\u0084\3\2\2\2\u0082"+
		"\u0080\3\2\2\2\u0083\u0085\7\5\2\2\u0084\u0083\3\2\2\2\u0084\u0085\3\2"+
		"\2\2\u0085\33\3\2\2\2\u0086\u008d\5\4\3\2\u0087\u008d\5\36\20\2\u0088"+
		"\u0089\7\3\2\2\u0089\u008a\5\32\16\2\u008a\u008b\7\4\2\2\u008b\u008d\3"+
		"\2\2\2\u008c\u0086\3\2\2\2\u008c\u0087\3\2\2\2\u008c\u0088\3\2\2\2\u008d"+
		"\u0092\3\2\2\2\u008e\u008f\7\6\2\2\u008f\u0091\5 \21\2\u0090\u008e\3\2"+
		"\2\2\u0091\u0094\3\2\2\2\u0092\u0090\3\2\2\2\u0092\u0093\3\2\2\2\u0093"+
		"\35\3\2\2\2\u0094\u0092\3\2\2\2\u0095\u0099\5\"\22\2\u0096\u0099\5$\23"+
		"\2\u0097\u0099\5,\27\2\u0098\u0095\3\2\2\2\u0098\u0096\3\2\2\2\u0098\u0097"+
		"\3\2\2\2\u0099\37\3\2\2\2\u009a\u009e\5\36\20\2\u009b\u009e\5*\26\2\u009c"+
		"\u009e\5\6\4\2\u009d\u009a\3\2\2\2\u009d\u009b\3\2\2\2\u009d\u009c\3\2"+
		"\2\2\u009e!\3\2\2\2\u009f\u00a0\5\b\5\2\u00a0\u00a2\5\30\r\2\u00a1\u00a3"+
		"\5.\30\2\u00a2\u00a1\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3#\3\2\2\2\u00a4"+
		"\u00a5\5\b\5\2\u00a5\u00a7\5\30\r\2\u00a6\u00a8\5.\30\2\u00a7\u00a6\3"+
		"\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\u00aa\7\3\2\2\u00aa"+
		"\u00af\5&\24\2\u00ab\u00ac\7\5\2\2\u00ac\u00ae\5&\24\2\u00ad\u00ab\3\2"+
		"\2\2\u00ae\u00b1\3\2\2\2\u00af\u00ad\3\2\2\2\u00af\u00b0\3\2\2\2\u00b0"+
		"\u00b3\3\2\2\2\u00b1\u00af\3\2\2\2\u00b2\u00b4\7\5\2\2\u00b3\u00b2\3\2"+
		"\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b6\7\4\2\2\u00b6"+
		"%\3\2\2\2\u00b7\u00b9\5\b\5\2\u00b8\u00b7\3\2\2\2\u00b9\u00ba\3\2\2\2"+
		"\u00ba\u00b8\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00bd"+
		"\7\6\2\2\u00bd\u00be\5(\25\2\u00be\'\3\2\2\2\u00bf\u00c4\5 \21\2\u00c0"+
		"\u00c1\7\6\2\2\u00c1\u00c3\5 \21\2\u00c2\u00c0\3\2\2\2\u00c3\u00c6\3\2"+
		"\2\2\u00c4\u00c2\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5)\3\2\2\2\u00c6\u00c4"+
		"\3\2\2\2\u00c7\u00c8\7\3\2\2\u00c8\u00cd\5(\25\2\u00c9\u00ca\7\5\2\2\u00ca"+
		"\u00cc\5(\25\2\u00cb\u00c9\3\2\2\2\u00cc\u00cf\3\2\2\2\u00cd\u00cb\3\2"+
		"\2\2\u00cd\u00ce\3\2\2\2\u00ce\u00d1\3\2\2\2\u00cf\u00cd\3\2\2\2\u00d0"+
		"\u00d2\7\5\2\2\u00d1\u00d0\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00d3\3\2"+
		"\2\2\u00d3\u00d4\7\4\2\2\u00d4+\3\2\2\2\u00d5\u00d6\7\r\2\2\u00d6\u00d8"+
		"\5\30\r\2\u00d7\u00d9\5.\30\2\u00d8\u00d7\3\2\2\2\u00d8\u00d9\3\2\2\2"+
		"\u00d9\u00da\3\2\2\2\u00da\u00db\7\3\2\2\u00db\u00e0\5\"\22\2\u00dc\u00dd"+
		"\7\6\2\2\u00dd\u00df\5\"\22\2\u00de\u00dc\3\2\2\2\u00df\u00e2\3\2\2\2"+
		"\u00e0\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00e3\3\2\2\2\u00e2\u00e0"+
		"\3\2\2\2\u00e3\u00e4\7\4\2\2\u00e4-\3\2\2\2\u00e5\u00ea\7\13\2\2\u00e6"+
		"\u00e8\5\26\f\2\u00e7\u00e9\7\n\2\2\u00e8\u00e7\3\2\2\2\u00e8\u00e9\3"+
		"\2\2\2\u00e9\u00eb\3\2\2\2\u00ea\u00e6\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb"+
		"\u00ec\3\2\2\2\u00ec\u00ed\7\f\2\2\u00ed/\3\2\2\2\37\669=HRU_bouw\u0080"+
		"\u0084\u008c\u0092\u0098\u009d\u00a2\u00a7\u00af\u00b3\u00ba\u00c4\u00cd"+
		"\u00d1\u00d8\u00e0\u00e8\u00ea";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}