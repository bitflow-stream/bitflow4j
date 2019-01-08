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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		EOP=10, NEXT=11, STRING=12, NUMBER=13, BOOL=14, IDENTIFIER=15, COMMENT=16, 
		NEWLINE=17, WHITESPACE=18, TAB=19;
	public static final int
		RULE_script = 0, RULE_input = 1, RULE_output = 2, RULE_name = 3, RULE_val = 4, 
		RULE_parameter = 5, RULE_transformParameters = 6, RULE_pipeline = 7, RULE_multiInputPipeline = 8, 
		RULE_pipelineElement = 9, RULE_transform = 10, RULE_fork = 11, RULE_namedSubPipeline = 12, 
		RULE_subPipeline = 13, RULE_multiplexFork = 14, RULE_multiplexSubPipeline = 15, 
		RULE_window = 16, RULE_windowPipeline = 17, RULE_schedulingHints = 18, 
		RULE_schedulingParameter = 19;
	public static final String[] ruleNames = {
		"script", "input", "output", "name", "val", "parameter", "transformParameters", 
		"pipeline", "multiInputPipeline", "pipelineElement", "transform", "fork", 
		"namedSubPipeline", "subPipeline", "multiplexFork", "multiplexSubPipeline", 
		"window", "windowPipeline", "schedulingHints", "schedulingParameter"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'='", "'('", "','", "')'", "'{'", "'}'", "'window'", "'['", "']'", 
		"';'", "'->'", null, null, null, null, null, null, null, "'\t'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, "EOP", "NEXT", 
		"STRING", "NUMBER", "BOOL", "IDENTIFIER", "COMMENT", "NEWLINE", "WHITESPACE", 
		"TAB"
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
		public MultiInputPipelineContext multiInputPipeline() {
			return getRuleContext(MultiInputPipelineContext.class,0);
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
			setState(40);
			multiInputPipeline();
			setState(41);
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

	public static class InputContext extends ParserRuleContext {
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public SchedulingHintsContext schedulingHints() {
			return getRuleContext(SchedulingHintsContext.class,0);
		}
		public InputContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_input; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterInput(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitInput(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitInput(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InputContext input() throws RecognitionException {
		InputContext _localctx = new InputContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_input);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(43);
				name();
				}
				}
				setState(46); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << NUMBER) | (1L << BOOL) | (1L << IDENTIFIER))) != 0) );
			setState(49);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(48);
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

	public static class OutputContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public SchedulingHintsContext schedulingHints() {
			return getRuleContext(SchedulingHintsContext.class,0);
		}
		public OutputContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_output; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterOutput(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitOutput(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitOutput(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OutputContext output() throws RecognitionException {
		OutputContext _localctx = new OutputContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_output);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			name();
			setState(53);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(52);
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
		public TerminalNode NUMBER() { return getToken(BitflowParser.NUMBER, 0); }
		public TerminalNode BOOL() { return getToken(BitflowParser.BOOL, 0); }
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
			setState(55);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << NUMBER) | (1L << BOOL) | (1L << IDENTIFIER))) != 0)) ) {
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

	public static class ValContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(BitflowParser.NUMBER, 0); }
		public TerminalNode BOOL() { return getToken(BitflowParser.BOOL, 0); }
		public TerminalNode STRING() { return getToken(BitflowParser.STRING, 0); }
		public ValContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_val; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterVal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitVal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitVal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValContext val() throws RecognitionException {
		ValContext _localctx = new ValContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_val);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(57);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << NUMBER) | (1L << BOOL))) != 0)) ) {
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
		public ValContext val() {
			return getRuleContext(ValContext.class,0);
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
		enterRule(_localctx, 10, RULE_parameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			name();
			setState(60);
			match(T__0);
			setState(61);
			val();
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

	public static class TransformParametersContext extends ParserRuleContext {
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public TransformParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_transformParameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterTransformParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitTransformParameters(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitTransformParameters(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TransformParametersContext transformParameters() throws RecognitionException {
		TransformParametersContext _localctx = new TransformParametersContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_transformParameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			match(T__1);
			setState(72);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << NUMBER) | (1L << BOOL) | (1L << IDENTIFIER))) != 0)) {
				{
				setState(64);
				parameter();
				setState(69);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__2) {
					{
					{
					setState(65);
					match(T__2);
					setState(66);
					parameter();
					}
					}
					setState(71);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(74);
			match(T__3);
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
		public InputContext input() {
			return getRuleContext(InputContext.class,0);
		}
		public MultiInputPipelineContext multiInputPipeline() {
			return getRuleContext(MultiInputPipelineContext.class,0);
		}
		public List<TerminalNode> NEXT() { return getTokens(BitflowParser.NEXT); }
		public TerminalNode NEXT(int i) {
			return getToken(BitflowParser.NEXT, i);
		}
		public List<PipelineElementContext> pipelineElement() {
			return getRuleContexts(PipelineElementContext.class);
		}
		public PipelineElementContext pipelineElement(int i) {
			return getRuleContext(PipelineElementContext.class,i);
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
		enterRule(_localctx, 14, RULE_pipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(81);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
			case NUMBER:
			case BOOL:
			case IDENTIFIER:
				{
				setState(76);
				input();
				}
				break;
			case T__4:
				{
				setState(77);
				match(T__4);
				setState(78);
				multiInputPipeline();
				setState(79);
				match(T__5);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEXT) {
				{
				{
				setState(83);
				match(NEXT);
				setState(84);
				pipelineElement();
				}
				}
				setState(89);
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

	public static class MultiInputPipelineContext extends ParserRuleContext {
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
		public MultiInputPipelineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiInputPipeline; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterMultiInputPipeline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitMultiInputPipeline(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitMultiInputPipeline(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiInputPipelineContext multiInputPipeline() throws RecognitionException {
		MultiInputPipelineContext _localctx = new MultiInputPipelineContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_multiInputPipeline);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			pipeline();
			setState(95);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(91);
					match(EOP);
					setState(92);
					pipeline();
					}
					} 
				}
				setState(97);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			}
			setState(99);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOP) {
				{
				setState(98);
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

	public static class PipelineElementContext extends ParserRuleContext {
		public TransformContext transform() {
			return getRuleContext(TransformContext.class,0);
		}
		public ForkContext fork() {
			return getRuleContext(ForkContext.class,0);
		}
		public MultiplexForkContext multiplexFork() {
			return getRuleContext(MultiplexForkContext.class,0);
		}
		public WindowContext window() {
			return getRuleContext(WindowContext.class,0);
		}
		public OutputContext output() {
			return getRuleContext(OutputContext.class,0);
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
		enterRule(_localctx, 18, RULE_pipelineElement);
		try {
			setState(106);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(101);
				transform();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(102);
				fork();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(103);
				multiplexFork();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(104);
				window();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(105);
				output();
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

	public static class TransformContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TransformParametersContext transformParameters() {
			return getRuleContext(TransformParametersContext.class,0);
		}
		public SchedulingHintsContext schedulingHints() {
			return getRuleContext(SchedulingHintsContext.class,0);
		}
		public TransformContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_transform; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterTransform(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitTransform(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitTransform(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TransformContext transform() throws RecognitionException {
		TransformContext _localctx = new TransformContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_transform);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			name();
			setState(109);
			transformParameters();
			setState(111);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(110);
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
		public TransformParametersContext transformParameters() {
			return getRuleContext(TransformParametersContext.class,0);
		}
		public List<NamedSubPipelineContext> namedSubPipeline() {
			return getRuleContexts(NamedSubPipelineContext.class);
		}
		public NamedSubPipelineContext namedSubPipeline(int i) {
			return getRuleContext(NamedSubPipelineContext.class,i);
		}
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
		enterRule(_localctx, 22, RULE_fork);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(113);
			name();
			setState(114);
			transformParameters();
			setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(115);
				schedulingHints();
				}
			}

			setState(118);
			match(T__4);
			setState(119);
			namedSubPipeline();
			setState(124);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(120);
					match(EOP);
					setState(121);
					namedSubPipeline();
					}
					} 
				}
				setState(126);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			}
			setState(128);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOP) {
				{
				setState(127);
				match(EOP);
				}
			}

			setState(130);
			match(T__5);
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
		enterRule(_localctx, 24, RULE_namedSubPipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(133); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(132);
				name();
				}
				}
				setState(135); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << NUMBER) | (1L << BOOL) | (1L << IDENTIFIER))) != 0) );
			setState(137);
			match(NEXT);
			setState(138);
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
		public List<PipelineElementContext> pipelineElement() {
			return getRuleContexts(PipelineElementContext.class);
		}
		public PipelineElementContext pipelineElement(int i) {
			return getRuleContext(PipelineElementContext.class,i);
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
		enterRule(_localctx, 26, RULE_subPipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(140);
			pipelineElement();
			setState(145);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEXT) {
				{
				{
				setState(141);
				match(NEXT);
				setState(142);
				pipelineElement();
				}
				}
				setState(147);
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
		public List<MultiplexSubPipelineContext> multiplexSubPipeline() {
			return getRuleContexts(MultiplexSubPipelineContext.class);
		}
		public MultiplexSubPipelineContext multiplexSubPipeline(int i) {
			return getRuleContext(MultiplexSubPipelineContext.class,i);
		}
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
		enterRule(_localctx, 28, RULE_multiplexFork);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(148);
			match(T__4);
			setState(149);
			multiplexSubPipeline();
			setState(154);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(150);
					match(EOP);
					setState(151);
					multiplexSubPipeline();
					}
					} 
				}
				setState(156);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			}
			setState(158);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOP) {
				{
				setState(157);
				match(EOP);
				}
			}

			setState(160);
			match(T__5);
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

	public static class MultiplexSubPipelineContext extends ParserRuleContext {
		public SubPipelineContext subPipeline() {
			return getRuleContext(SubPipelineContext.class,0);
		}
		public MultiplexSubPipelineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplexSubPipeline; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterMultiplexSubPipeline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitMultiplexSubPipeline(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitMultiplexSubPipeline(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiplexSubPipelineContext multiplexSubPipeline() throws RecognitionException {
		MultiplexSubPipelineContext _localctx = new MultiplexSubPipelineContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_multiplexSubPipeline);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(162);
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

	public static class WindowContext extends ParserRuleContext {
		public TransformParametersContext transformParameters() {
			return getRuleContext(TransformParametersContext.class,0);
		}
		public WindowPipelineContext windowPipeline() {
			return getRuleContext(WindowPipelineContext.class,0);
		}
		public SchedulingHintsContext schedulingHints() {
			return getRuleContext(SchedulingHintsContext.class,0);
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
		enterRule(_localctx, 32, RULE_window);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164);
			match(T__6);
			setState(165);
			transformParameters();
			setState(167);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(166);
				schedulingHints();
				}
			}

			setState(169);
			match(T__4);
			setState(170);
			windowPipeline();
			setState(171);
			match(T__5);
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

	public static class WindowPipelineContext extends ParserRuleContext {
		public List<TransformContext> transform() {
			return getRuleContexts(TransformContext.class);
		}
		public TransformContext transform(int i) {
			return getRuleContext(TransformContext.class,i);
		}
		public List<TerminalNode> NEXT() { return getTokens(BitflowParser.NEXT); }
		public TerminalNode NEXT(int i) {
			return getToken(BitflowParser.NEXT, i);
		}
		public WindowPipelineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_windowPipeline; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterWindowPipeline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitWindowPipeline(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitWindowPipeline(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WindowPipelineContext windowPipeline() throws RecognitionException {
		WindowPipelineContext _localctx = new WindowPipelineContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_windowPipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
			transform();
			setState(178);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEXT) {
				{
				{
				setState(174);
				match(NEXT);
				setState(175);
				transform();
				}
				}
				setState(180);
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

	public static class SchedulingHintsContext extends ParserRuleContext {
		public List<SchedulingParameterContext> schedulingParameter() {
			return getRuleContexts(SchedulingParameterContext.class);
		}
		public SchedulingParameterContext schedulingParameter(int i) {
			return getRuleContext(SchedulingParameterContext.class,i);
		}
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
		enterRule(_localctx, 36, RULE_schedulingHints);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			match(T__7);
			setState(190);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << NUMBER) | (1L << BOOL) | (1L << IDENTIFIER))) != 0)) {
				{
				setState(182);
				schedulingParameter();
				setState(187);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__2) {
					{
					{
					setState(183);
					match(T__2);
					setState(184);
					schedulingParameter();
					}
					}
					setState(189);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(192);
			match(T__8);
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

	public static class SchedulingParameterContext extends ParserRuleContext {
		public ParameterContext parameter() {
			return getRuleContext(ParameterContext.class,0);
		}
		public SchedulingParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schedulingParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterSchedulingParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitSchedulingParameter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitSchedulingParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SchedulingParameterContext schedulingParameter() throws RecognitionException {
		SchedulingParameterContext _localctx = new SchedulingParameterContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_schedulingParameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(194);
			parameter();
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\25\u00c7\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\3\2\3\2\3\2\3\3\6\3/\n\3\r\3\16\3\60\3"+
		"\3\5\3\64\n\3\3\4\3\4\5\48\n\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\3\b\3\b"+
		"\3\b\3\b\7\bF\n\b\f\b\16\bI\13\b\5\bK\n\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t"+
		"\5\tT\n\t\3\t\3\t\7\tX\n\t\f\t\16\t[\13\t\3\n\3\n\3\n\7\n`\n\n\f\n\16"+
		"\nc\13\n\3\n\5\nf\n\n\3\13\3\13\3\13\3\13\3\13\5\13m\n\13\3\f\3\f\3\f"+
		"\5\fr\n\f\3\r\3\r\3\r\5\rw\n\r\3\r\3\r\3\r\3\r\7\r}\n\r\f\r\16\r\u0080"+
		"\13\r\3\r\5\r\u0083\n\r\3\r\3\r\3\16\6\16\u0088\n\16\r\16\16\16\u0089"+
		"\3\16\3\16\3\16\3\17\3\17\3\17\7\17\u0092\n\17\f\17\16\17\u0095\13\17"+
		"\3\20\3\20\3\20\3\20\7\20\u009b\n\20\f\20\16\20\u009e\13\20\3\20\5\20"+
		"\u00a1\n\20\3\20\3\20\3\21\3\21\3\22\3\22\3\22\5\22\u00aa\n\22\3\22\3"+
		"\22\3\22\3\22\3\23\3\23\3\23\7\23\u00b3\n\23\f\23\16\23\u00b6\13\23\3"+
		"\24\3\24\3\24\3\24\7\24\u00bc\n\24\f\24\16\24\u00bf\13\24\5\24\u00c1\n"+
		"\24\3\24\3\24\3\25\3\25\3\25\2\2\26\2\4\6\b\n\f\16\20\22\24\26\30\32\34"+
		"\36 \"$&(\2\4\3\2\16\21\3\2\16\20\2\u00cb\2*\3\2\2\2\4.\3\2\2\2\6\65\3"+
		"\2\2\2\b9\3\2\2\2\n;\3\2\2\2\f=\3\2\2\2\16A\3\2\2\2\20S\3\2\2\2\22\\\3"+
		"\2\2\2\24l\3\2\2\2\26n\3\2\2\2\30s\3\2\2\2\32\u0087\3\2\2\2\34\u008e\3"+
		"\2\2\2\36\u0096\3\2\2\2 \u00a4\3\2\2\2\"\u00a6\3\2\2\2$\u00af\3\2\2\2"+
		"&\u00b7\3\2\2\2(\u00c4\3\2\2\2*+\5\22\n\2+,\7\2\2\3,\3\3\2\2\2-/\5\b\5"+
		"\2.-\3\2\2\2/\60\3\2\2\2\60.\3\2\2\2\60\61\3\2\2\2\61\63\3\2\2\2\62\64"+
		"\5&\24\2\63\62\3\2\2\2\63\64\3\2\2\2\64\5\3\2\2\2\65\67\5\b\5\2\668\5"+
		"&\24\2\67\66\3\2\2\2\678\3\2\2\28\7\3\2\2\29:\t\2\2\2:\t\3\2\2\2;<\t\3"+
		"\2\2<\13\3\2\2\2=>\5\b\5\2>?\7\3\2\2?@\5\n\6\2@\r\3\2\2\2AJ\7\4\2\2BG"+
		"\5\f\7\2CD\7\5\2\2DF\5\f\7\2EC\3\2\2\2FI\3\2\2\2GE\3\2\2\2GH\3\2\2\2H"+
		"K\3\2\2\2IG\3\2\2\2JB\3\2\2\2JK\3\2\2\2KL\3\2\2\2LM\7\6\2\2M\17\3\2\2"+
		"\2NT\5\4\3\2OP\7\7\2\2PQ\5\22\n\2QR\7\b\2\2RT\3\2\2\2SN\3\2\2\2SO\3\2"+
		"\2\2TY\3\2\2\2UV\7\r\2\2VX\5\24\13\2WU\3\2\2\2X[\3\2\2\2YW\3\2\2\2YZ\3"+
		"\2\2\2Z\21\3\2\2\2[Y\3\2\2\2\\a\5\20\t\2]^\7\f\2\2^`\5\20\t\2_]\3\2\2"+
		"\2`c\3\2\2\2a_\3\2\2\2ab\3\2\2\2be\3\2\2\2ca\3\2\2\2df\7\f\2\2ed\3\2\2"+
		"\2ef\3\2\2\2f\23\3\2\2\2gm\5\26\f\2hm\5\30\r\2im\5\36\20\2jm\5\"\22\2"+
		"km\5\6\4\2lg\3\2\2\2lh\3\2\2\2li\3\2\2\2lj\3\2\2\2lk\3\2\2\2m\25\3\2\2"+
		"\2no\5\b\5\2oq\5\16\b\2pr\5&\24\2qp\3\2\2\2qr\3\2\2\2r\27\3\2\2\2st\5"+
		"\b\5\2tv\5\16\b\2uw\5&\24\2vu\3\2\2\2vw\3\2\2\2wx\3\2\2\2xy\7\7\2\2y~"+
		"\5\32\16\2z{\7\f\2\2{}\5\32\16\2|z\3\2\2\2}\u0080\3\2\2\2~|\3\2\2\2~\177"+
		"\3\2\2\2\177\u0082\3\2\2\2\u0080~\3\2\2\2\u0081\u0083\7\f\2\2\u0082\u0081"+
		"\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0085\7\b\2\2\u0085"+
		"\31\3\2\2\2\u0086\u0088\5\b\5\2\u0087\u0086\3\2\2\2\u0088\u0089\3\2\2"+
		"\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u008c"+
		"\7\r\2\2\u008c\u008d\5\34\17\2\u008d\33\3\2\2\2\u008e\u0093\5\24\13\2"+
		"\u008f\u0090\7\r\2\2\u0090\u0092\5\24\13\2\u0091\u008f\3\2\2\2\u0092\u0095"+
		"\3\2\2\2\u0093\u0091\3\2\2\2\u0093\u0094\3\2\2\2\u0094\35\3\2\2\2\u0095"+
		"\u0093\3\2\2\2\u0096\u0097\7\7\2\2\u0097\u009c\5 \21\2\u0098\u0099\7\f"+
		"\2\2\u0099\u009b\5 \21\2\u009a\u0098\3\2\2\2\u009b\u009e\3\2\2\2\u009c"+
		"\u009a\3\2\2\2\u009c\u009d\3\2\2\2\u009d\u00a0\3\2\2\2\u009e\u009c\3\2"+
		"\2\2\u009f\u00a1\7\f\2\2\u00a0\u009f\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1"+
		"\u00a2\3\2\2\2\u00a2\u00a3\7\b\2\2\u00a3\37\3\2\2\2\u00a4\u00a5\5\34\17"+
		"\2\u00a5!\3\2\2\2\u00a6\u00a7\7\t\2\2\u00a7\u00a9\5\16\b\2\u00a8\u00aa"+
		"\5&\24\2\u00a9\u00a8\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab"+
		"\u00ac\7\7\2\2\u00ac\u00ad\5$\23\2\u00ad\u00ae\7\b\2\2\u00ae#\3\2\2\2"+
		"\u00af\u00b4\5\26\f\2\u00b0\u00b1\7\r\2\2\u00b1\u00b3\5\26\f\2\u00b2\u00b0"+
		"\3\2\2\2\u00b3\u00b6\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5"+
		"%\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b7\u00c0\7\n\2\2\u00b8\u00bd\5(\25\2"+
		"\u00b9\u00ba\7\5\2\2\u00ba\u00bc\5(\25\2\u00bb\u00b9\3\2\2\2\u00bc\u00bf"+
		"\3\2\2\2\u00bd\u00bb\3\2\2\2\u00bd\u00be\3\2\2\2\u00be\u00c1\3\2\2\2\u00bf"+
		"\u00bd\3\2\2\2\u00c0\u00b8\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1\u00c2\3\2"+
		"\2\2\u00c2\u00c3\7\13\2\2\u00c3\'\3\2\2\2\u00c4\u00c5\5\f\7\2\u00c5)\3"+
		"\2\2\2\30\60\63\67GJSYaelqv~\u0082\u0089\u0093\u009c\u00a0\u00a9\u00b4"+
		"\u00bd\u00c0";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}