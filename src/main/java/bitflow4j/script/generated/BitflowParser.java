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
		EOP=10, NEXT=11, STRING=12, NUMBER=13, NAME=14, COMMENT=15, NEWLINE=16, 
		WHITESPACE=17, TAB=18;
	public static final int
		RULE_script = 0, RULE_pipeline = 1, RULE_multiInputPipeline = 2, RULE_input = 3, 
		RULE_output = 4, RULE_name = 5, RULE_namedSubPipelineKey = 6, RULE_endpoint = 7, 
		RULE_val = 8, RULE_parameter = 9, RULE_transformParameters = 10, RULE_intermediateTransform = 11, 
		RULE_transform = 12, RULE_fork = 13, RULE_namedSubPipeline = 14, RULE_subPipeline = 15, 
		RULE_multiplexFork = 16, RULE_multiplexSubPipeline = 17, RULE_window = 18, 
		RULE_windowPipeline = 19, RULE_schedulingHints = 20;
	public static final String[] ruleNames = {
		"script", "pipeline", "multiInputPipeline", "input", "output", "name", 
		"namedSubPipelineKey", "endpoint", "val", "parameter", "transformParameters", 
		"intermediateTransform", "transform", "fork", "namedSubPipeline", "subPipeline", 
		"multiplexFork", "multiplexSubPipeline", "window", "windowPipeline", "schedulingHints"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'{'", "'}'", "'='", "'('", "','", "')'", "'window'", "'['", "']'", 
		"';'", "'->'", null, null, null, null, null, null, "'\t'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, "EOP", "NEXT", 
		"STRING", "NUMBER", "NAME", "COMMENT", "NEWLINE", "WHITESPACE", "TAB"
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
		public List<PipelineContext> pipeline() {
			return getRuleContexts(PipelineContext.class);
		}
		public PipelineContext pipeline(int i) {
			return getRuleContext(PipelineContext.class,i);
		}
		public TerminalNode EOF() { return getToken(BitflowParser.EOF, 0); }
		public List<TerminalNode> EOP() { return getTokens(BitflowParser.EOP); }
		public TerminalNode EOP(int i) {
			return getToken(BitflowParser.EOP, i);
		}
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
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			pipeline();
			setState(47);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(43);
					match(EOP);
					setState(44);
					pipeline();
					}
					} 
				}
				setState(49);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(51);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOP) {
				{
				setState(50);
				match(EOP);
				}
			}

			setState(53);
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
		public List<IntermediateTransformContext> intermediateTransform() {
			return getRuleContexts(IntermediateTransformContext.class);
		}
		public IntermediateTransformContext intermediateTransform(int i) {
			return getRuleContext(IntermediateTransformContext.class,i);
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
		enterRule(_localctx, 2, RULE_pipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(57);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
			case NAME:
				{
				setState(55);
				input();
				}
				break;
			case T__0:
				{
				setState(56);
				multiInputPipeline();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(63);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEXT) {
				{
				{
				setState(59);
				match(NEXT);
				setState(60);
				intermediateTransform();
				}
				}
				setState(65);
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
		enterRule(_localctx, 4, RULE_multiInputPipeline);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(66);
			match(T__0);
			setState(67);
			pipeline();
			setState(72);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(68);
					match(EOP);
					setState(69);
					pipeline();
					}
					} 
				}
				setState(74);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			setState(76);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOP) {
				{
				setState(75);
				match(EOP);
				}
			}

			setState(78);
			match(T__1);
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
		public EndpointContext endpoint() {
			return getRuleContext(EndpointContext.class,0);
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
		enterRule(_localctx, 6, RULE_input);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			endpoint();
			setState(82);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(81);
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
		public EndpointContext endpoint() {
			return getRuleContext(EndpointContext.class,0);
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
		enterRule(_localctx, 8, RULE_output);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			endpoint();
			setState(86);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(85);
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
		public TerminalNode NAME() { return getToken(BitflowParser.NAME, 0); }
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
		enterRule(_localctx, 10, RULE_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			_la = _input.LA(1);
			if ( !(_la==STRING || _la==NAME) ) {
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

	public static class NamedSubPipelineKeyContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(BitflowParser.NAME, 0); }
		public TerminalNode STRING() { return getToken(BitflowParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(BitflowParser.NUMBER, 0); }
		public NamedSubPipelineKeyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_namedSubPipelineKey; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterNamedSubPipelineKey(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitNamedSubPipelineKey(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitNamedSubPipelineKey(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NamedSubPipelineKeyContext namedSubPipelineKey() throws RecognitionException {
		NamedSubPipelineKeyContext _localctx = new NamedSubPipelineKeyContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_namedSubPipelineKey);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << NUMBER) | (1L << NAME))) != 0)) ) {
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

	public static class EndpointContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(BitflowParser.STRING, 0); }
		public TerminalNode NAME() { return getToken(BitflowParser.NAME, 0); }
		public EndpointContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_endpoint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterEndpoint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitEndpoint(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitEndpoint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EndpointContext endpoint() throws RecognitionException {
		EndpointContext _localctx = new EndpointContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_endpoint);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(92);
			_la = _input.LA(1);
			if ( !(_la==STRING || _la==NAME) ) {
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
		public TerminalNode STRING() { return getToken(BitflowParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(BitflowParser.NUMBER, 0); }
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
		enterRule(_localctx, 16, RULE_val);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			_la = _input.LA(1);
			if ( !(_la==STRING || _la==NUMBER) ) {
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
		enterRule(_localctx, 18, RULE_parameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(96);
			name();
			setState(97);
			match(T__2);
			setState(98);
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
		enterRule(_localctx, 20, RULE_transformParameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100);
			match(T__3);
			setState(109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING || _la==NAME) {
				{
				setState(101);
				parameter();
				setState(106);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(102);
					match(T__4);
					setState(103);
					parameter();
					}
					}
					setState(108);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(111);
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

	public static class IntermediateTransformContext extends ParserRuleContext {
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
		public IntermediateTransformContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intermediateTransform; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterIntermediateTransform(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitIntermediateTransform(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitIntermediateTransform(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntermediateTransformContext intermediateTransform() throws RecognitionException {
		IntermediateTransformContext _localctx = new IntermediateTransformContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_intermediateTransform);
		try {
			setState(118);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(113);
				transform();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(114);
				fork();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(115);
				multiplexFork();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(116);
				window();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(117);
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
		enterRule(_localctx, 24, RULE_transform);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120);
			name();
			setState(121);
			transformParameters();
			setState(123);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(122);
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
		enterRule(_localctx, 26, RULE_fork);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(125);
			name();
			setState(126);
			transformParameters();
			setState(128);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(127);
				schedulingHints();
				}
			}

			setState(130);
			match(T__0);
			setState(131);
			namedSubPipeline();
			setState(136);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(132);
					match(EOP);
					setState(133);
					namedSubPipeline();
					}
					} 
				}
				setState(138);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			}
			setState(140);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOP) {
				{
				setState(139);
				match(EOP);
				}
			}

			setState(142);
			match(T__1);
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
		public NamedSubPipelineKeyContext namedSubPipelineKey() {
			return getRuleContext(NamedSubPipelineKeyContext.class,0);
		}
		public TerminalNode NEXT() { return getToken(BitflowParser.NEXT, 0); }
		public SubPipelineContext subPipeline() {
			return getRuleContext(SubPipelineContext.class,0);
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
		enterRule(_localctx, 28, RULE_namedSubPipeline);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			namedSubPipelineKey();
			setState(145);
			match(NEXT);
			setState(146);
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
		public List<IntermediateTransformContext> intermediateTransform() {
			return getRuleContexts(IntermediateTransformContext.class);
		}
		public IntermediateTransformContext intermediateTransform(int i) {
			return getRuleContext(IntermediateTransformContext.class,i);
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
		enterRule(_localctx, 30, RULE_subPipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(148);
			intermediateTransform();
			setState(153);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEXT) {
				{
				{
				setState(149);
				match(NEXT);
				setState(150);
				intermediateTransform();
				}
				}
				setState(155);
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
		enterRule(_localctx, 32, RULE_multiplexFork);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			match(T__0);
			setState(157);
			multiplexSubPipeline();
			setState(162);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(158);
					match(EOP);
					setState(159);
					multiplexSubPipeline();
					}
					} 
				}
				setState(164);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			}
			setState(166);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOP) {
				{
				setState(165);
				match(EOP);
				}
			}

			setState(168);
			match(T__1);
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
		enterRule(_localctx, 34, RULE_multiplexSubPipeline);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
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
		enterRule(_localctx, 36, RULE_window);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(172);
			match(T__6);
			setState(173);
			transformParameters();
			setState(175);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(174);
				schedulingHints();
				}
			}

			setState(177);
			match(T__0);
			setState(178);
			windowPipeline();
			setState(179);
			match(T__1);
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
		enterRule(_localctx, 38, RULE_windowPipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			transform();
			setState(186);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEXT) {
				{
				{
				setState(182);
				match(NEXT);
				setState(183);
				transform();
				}
				}
				setState(188);
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
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
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
		enterRule(_localctx, 40, RULE_schedulingHints);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(189);
			match(T__7);
			setState(198);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING || _la==NAME) {
				{
				setState(190);
				parameter();
				setState(195);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(191);
					match(T__4);
					setState(192);
					parameter();
					}
					}
					setState(197);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(200);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\24\u00cd\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\3\2\3\2\3\2\7\2\60\n\2\f\2\16"+
		"\2\63\13\2\3\2\5\2\66\n\2\3\2\3\2\3\3\3\3\5\3<\n\3\3\3\3\3\7\3@\n\3\f"+
		"\3\16\3C\13\3\3\4\3\4\3\4\3\4\7\4I\n\4\f\4\16\4L\13\4\3\4\5\4O\n\4\3\4"+
		"\3\4\3\5\3\5\5\5U\n\5\3\6\3\6\5\6Y\n\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n"+
		"\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\7\fk\n\f\f\f\16\fn\13\f\5\fp\n\f"+
		"\3\f\3\f\3\r\3\r\3\r\3\r\3\r\5\ry\n\r\3\16\3\16\3\16\5\16~\n\16\3\17\3"+
		"\17\3\17\5\17\u0083\n\17\3\17\3\17\3\17\3\17\7\17\u0089\n\17\f\17\16\17"+
		"\u008c\13\17\3\17\5\17\u008f\n\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3"+
		"\21\3\21\7\21\u009a\n\21\f\21\16\21\u009d\13\21\3\22\3\22\3\22\3\22\7"+
		"\22\u00a3\n\22\f\22\16\22\u00a6\13\22\3\22\5\22\u00a9\n\22\3\22\3\22\3"+
		"\23\3\23\3\24\3\24\3\24\5\24\u00b2\n\24\3\24\3\24\3\24\3\24\3\25\3\25"+
		"\3\25\7\25\u00bb\n\25\f\25\16\25\u00be\13\25\3\26\3\26\3\26\3\26\7\26"+
		"\u00c4\n\26\f\26\16\26\u00c7\13\26\5\26\u00c9\n\26\3\26\3\26\3\26\2\2"+
		"\27\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*\2\5\4\2\16\16\20\20"+
		"\3\2\16\20\3\2\16\17\2\u00d0\2,\3\2\2\2\4;\3\2\2\2\6D\3\2\2\2\bR\3\2\2"+
		"\2\nV\3\2\2\2\fZ\3\2\2\2\16\\\3\2\2\2\20^\3\2\2\2\22`\3\2\2\2\24b\3\2"+
		"\2\2\26f\3\2\2\2\30x\3\2\2\2\32z\3\2\2\2\34\177\3\2\2\2\36\u0092\3\2\2"+
		"\2 \u0096\3\2\2\2\"\u009e\3\2\2\2$\u00ac\3\2\2\2&\u00ae\3\2\2\2(\u00b7"+
		"\3\2\2\2*\u00bf\3\2\2\2,\61\5\4\3\2-.\7\f\2\2.\60\5\4\3\2/-\3\2\2\2\60"+
		"\63\3\2\2\2\61/\3\2\2\2\61\62\3\2\2\2\62\65\3\2\2\2\63\61\3\2\2\2\64\66"+
		"\7\f\2\2\65\64\3\2\2\2\65\66\3\2\2\2\66\67\3\2\2\2\678\7\2\2\38\3\3\2"+
		"\2\29<\5\b\5\2:<\5\6\4\2;9\3\2\2\2;:\3\2\2\2<A\3\2\2\2=>\7\r\2\2>@\5\30"+
		"\r\2?=\3\2\2\2@C\3\2\2\2A?\3\2\2\2AB\3\2\2\2B\5\3\2\2\2CA\3\2\2\2DE\7"+
		"\3\2\2EJ\5\4\3\2FG\7\f\2\2GI\5\4\3\2HF\3\2\2\2IL\3\2\2\2JH\3\2\2\2JK\3"+
		"\2\2\2KN\3\2\2\2LJ\3\2\2\2MO\7\f\2\2NM\3\2\2\2NO\3\2\2\2OP\3\2\2\2PQ\7"+
		"\4\2\2Q\7\3\2\2\2RT\5\20\t\2SU\5*\26\2TS\3\2\2\2TU\3\2\2\2U\t\3\2\2\2"+
		"VX\5\20\t\2WY\5*\26\2XW\3\2\2\2XY\3\2\2\2Y\13\3\2\2\2Z[\t\2\2\2[\r\3\2"+
		"\2\2\\]\t\3\2\2]\17\3\2\2\2^_\t\2\2\2_\21\3\2\2\2`a\t\4\2\2a\23\3\2\2"+
		"\2bc\5\f\7\2cd\7\5\2\2de\5\22\n\2e\25\3\2\2\2fo\7\6\2\2gl\5\24\13\2hi"+
		"\7\7\2\2ik\5\24\13\2jh\3\2\2\2kn\3\2\2\2lj\3\2\2\2lm\3\2\2\2mp\3\2\2\2"+
		"nl\3\2\2\2og\3\2\2\2op\3\2\2\2pq\3\2\2\2qr\7\b\2\2r\27\3\2\2\2sy\5\32"+
		"\16\2ty\5\34\17\2uy\5\"\22\2vy\5&\24\2wy\5\n\6\2xs\3\2\2\2xt\3\2\2\2x"+
		"u\3\2\2\2xv\3\2\2\2xw\3\2\2\2y\31\3\2\2\2z{\5\f\7\2{}\5\26\f\2|~\5*\26"+
		"\2}|\3\2\2\2}~\3\2\2\2~\33\3\2\2\2\177\u0080\5\f\7\2\u0080\u0082\5\26"+
		"\f\2\u0081\u0083\5*\26\2\u0082\u0081\3\2\2\2\u0082\u0083\3\2\2\2\u0083"+
		"\u0084\3\2\2\2\u0084\u0085\7\3\2\2\u0085\u008a\5\36\20\2\u0086\u0087\7"+
		"\f\2\2\u0087\u0089\5\36\20\2\u0088\u0086\3\2\2\2\u0089\u008c\3\2\2\2\u008a"+
		"\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u008e\3\2\2\2\u008c\u008a\3\2"+
		"\2\2\u008d\u008f\7\f\2\2\u008e\u008d\3\2\2\2\u008e\u008f\3\2\2\2\u008f"+
		"\u0090\3\2\2\2\u0090\u0091\7\4\2\2\u0091\35\3\2\2\2\u0092\u0093\5\16\b"+
		"\2\u0093\u0094\7\r\2\2\u0094\u0095\5 \21\2\u0095\37\3\2\2\2\u0096\u009b"+
		"\5\30\r\2\u0097\u0098\7\r\2\2\u0098\u009a\5\30\r\2\u0099\u0097\3\2\2\2"+
		"\u009a\u009d\3\2\2\2\u009b\u0099\3\2\2\2\u009b\u009c\3\2\2\2\u009c!\3"+
		"\2\2\2\u009d\u009b\3\2\2\2\u009e\u009f\7\3\2\2\u009f\u00a4\5$\23\2\u00a0"+
		"\u00a1\7\f\2\2\u00a1\u00a3\5$\23\2\u00a2\u00a0\3\2\2\2\u00a3\u00a6\3\2"+
		"\2\2\u00a4\u00a2\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a8\3\2\2\2\u00a6"+
		"\u00a4\3\2\2\2\u00a7\u00a9\7\f\2\2\u00a8\u00a7\3\2\2\2\u00a8\u00a9\3\2"+
		"\2\2\u00a9\u00aa\3\2\2\2\u00aa\u00ab\7\4\2\2\u00ab#\3\2\2\2\u00ac\u00ad"+
		"\5 \21\2\u00ad%\3\2\2\2\u00ae\u00af\7\t\2\2\u00af\u00b1\5\26\f\2\u00b0"+
		"\u00b2\5*\26\2\u00b1\u00b0\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2\u00b3\3\2"+
		"\2\2\u00b3\u00b4\7\3\2\2\u00b4\u00b5\5(\25\2\u00b5\u00b6\7\4\2\2\u00b6"+
		"\'\3\2\2\2\u00b7\u00bc\5\32\16\2\u00b8\u00b9\7\r\2\2\u00b9\u00bb\5\32"+
		"\16\2\u00ba\u00b8\3\2\2\2\u00bb\u00be\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bc"+
		"\u00bd\3\2\2\2\u00bd)\3\2\2\2\u00be\u00bc\3\2\2\2\u00bf\u00c8\7\n\2\2"+
		"\u00c0\u00c5\5\24\13\2\u00c1\u00c2\7\7\2\2\u00c2\u00c4\5\24\13\2\u00c3"+
		"\u00c1\3\2\2\2\u00c4\u00c7\3\2\2\2\u00c5\u00c3\3\2\2\2\u00c5\u00c6\3\2"+
		"\2\2\u00c6\u00c9\3\2\2\2\u00c7\u00c5\3\2\2\2\u00c8\u00c0\3\2\2\2\u00c8"+
		"\u00c9\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00cb\7\13\2\2\u00cb+\3\2\2\2"+
		"\30\61\65;AJNTXlox}\u0082\u008a\u008e\u009b\u00a4\u00a8\u00b1\u00bc\u00c5"+
		"\u00c8";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}