// Generated from Bitflow.g4 by ANTLR 4.7.1
package bitflow4j.script.generated;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class BitflowParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		CASE=10, EOP=11, PIPE=12, PIPE_NAME=13, STRING=14, NUMBER=15, NAME=16, 
		COMMENT=17, MULTILINE_COMMENT=18, NEWLINE=19, WHITESPACE=20, TAB=21;
	public static final int
		RULE_script = 0, RULE_outputFork = 1, RULE_fork = 2, RULE_window = 3, 
		RULE_multiinput = 4, RULE_input = 5, RULE_output = 6, RULE_transform = 7, 
		RULE_subPipeline = 8, RULE_windowSubPipeline = 9, RULE_pipeline = 10, 
		RULE_parameter = 11, RULE_transformParameters = 12, RULE_name = 13, RULE_pipelineName = 14, 
		RULE_schedulingHints = 15;
	public static final String[] ruleNames = {
		"script", "outputFork", "fork", "window", "multiinput", "input", "output", 
		"transform", "subPipeline", "windowSubPipeline", "pipeline", "parameter", 
		"transformParameters", "name", "pipelineName", "schedulingHints"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'{'", "'}'", "'window'", "'='", "'('", "','", "')'", "'['", "']'", 
		null, "';'", "'->'", null, null, null, null, null, null, null, null, "'\t'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, "CASE", "EOP", 
		"PIPE", "PIPE_NAME", "STRING", "NUMBER", "NAME", "COMMENT", "MULTILINE_COMMENT", 
		"NEWLINE", "WHITESPACE", "TAB"
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
		public PipelineContext pipeline() {
			return getRuleContext(PipelineContext.class,0);
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
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			pipeline();
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

	public static class OutputForkContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TransformParametersContext transformParameters() {
			return getRuleContext(TransformParametersContext.class,0);
		}
		public SchedulingHintsContext schedulingHints() {
			return getRuleContext(SchedulingHintsContext.class,0);
		}
		public List<OutputContext> output() {
			return getRuleContexts(OutputContext.class);
		}
		public OutputContext output(int i) {
			return getRuleContext(OutputContext.class,i);
		}
		public OutputForkContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputFork; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterOutputFork(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitOutputFork(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitOutputFork(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OutputForkContext outputFork() throws RecognitionException {
		OutputForkContext _localctx = new OutputForkContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_outputFork);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(35);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << NUMBER) | (1L << NAME))) != 0)) {
				{
				setState(34);
				name();
				}
			}

			setState(38);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(37);
				transformParameters();
				}
			}

			setState(41);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(40);
				schedulingHints();
				}
			}

			setState(43);
			match(T__0);
			setState(47); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(44);
				output();
				setState(45);
				match(EOP);
				}
				}
				setState(49); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << NUMBER) | (1L << NAME))) != 0) );
			setState(51);
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

	public static class ForkContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TransformParametersContext transformParameters() {
			return getRuleContext(TransformParametersContext.class,0);
		}
		public SchedulingHintsContext schedulingHints() {
			return getRuleContext(SchedulingHintsContext.class,0);
		}
		public List<SubPipelineContext> subPipeline() {
			return getRuleContexts(SubPipelineContext.class);
		}
		public SubPipelineContext subPipeline(int i) {
			return getRuleContext(SubPipelineContext.class,i);
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
		enterRule(_localctx, 4, RULE_fork);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STRING) | (1L << NUMBER) | (1L << NAME))) != 0)) {
				{
				setState(53);
				name();
				}
			}

			setState(57);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(56);
				transformParameters();
				}
			}

			setState(60);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(59);
				schedulingHints();
				}
			}

			setState(62);
			match(T__0);
			setState(64); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(63);
				subPipeline();
				}
				}
				setState(66); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__2) | (1L << T__4) | (1L << T__7) | (1L << CASE) | (1L << STRING) | (1L << NUMBER) | (1L << NAME))) != 0) );
			setState(68);
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

	public static class WindowContext extends ParserRuleContext {
		public WindowSubPipelineContext windowSubPipeline() {
			return getRuleContext(WindowSubPipelineContext.class,0);
		}
		public TransformParametersContext transformParameters() {
			return getRuleContext(TransformParametersContext.class,0);
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
		enterRule(_localctx, 6, RULE_window);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(T__2);
			setState(72);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(71);
				transformParameters();
				}
			}

			setState(75);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(74);
				schedulingHints();
				}
			}

			setState(77);
			match(T__0);
			setState(78);
			windowSubPipeline();
			setState(79);
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

	public static class MultiinputContext extends ParserRuleContext {
		public List<InputContext> input() {
			return getRuleContexts(InputContext.class);
		}
		public InputContext input(int i) {
			return getRuleContext(InputContext.class,i);
		}
		public MultiinputContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiinput; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterMultiinput(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitMultiinput(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitMultiinput(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiinputContext multiinput() throws RecognitionException {
		MultiinputContext _localctx = new MultiinputContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_multiinput);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(81);
			input();
			setState(86);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(82);
					match(EOP);
					setState(83);
					input();
					}
					} 
				}
				setState(88);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			}
			setState(90);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOP) {
				{
				setState(89);
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

	public static class InputContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TransformParametersContext transformParameters() {
			return getRuleContext(TransformParametersContext.class,0);
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
		enterRule(_localctx, 10, RULE_input);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(92);
			name();
			setState(94);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(93);
				transformParameters();
				}
			}

			setState(97);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(96);
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
		public TransformParametersContext transformParameters() {
			return getRuleContext(TransformParametersContext.class,0);
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
		enterRule(_localctx, 12, RULE_output);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			name();
			setState(101);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(100);
				transformParameters();
				}
			}

			setState(104);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(103);
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
		enterRule(_localctx, 14, RULE_transform);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			name();
			setState(108);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				{
				setState(107);
				transformParameters();
				}
				break;
			}
			setState(111);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				{
				setState(110);
				schedulingHints();
				}
				break;
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

	public static class SubPipelineContext extends ParserRuleContext {
		public List<TransformContext> transform() {
			return getRuleContexts(TransformContext.class);
		}
		public TransformContext transform(int i) {
			return getRuleContext(TransformContext.class,i);
		}
		public List<ForkContext> fork() {
			return getRuleContexts(ForkContext.class);
		}
		public ForkContext fork(int i) {
			return getRuleContext(ForkContext.class,i);
		}
		public List<WindowContext> window() {
			return getRuleContexts(WindowContext.class);
		}
		public WindowContext window(int i) {
			return getRuleContext(WindowContext.class,i);
		}
		public PipelineNameContext pipelineName() {
			return getRuleContext(PipelineNameContext.class,0);
		}
		public List<TerminalNode> PIPE() { return getTokens(BitflowParser.PIPE); }
		public TerminalNode PIPE(int i) {
			return getToken(BitflowParser.PIPE, i);
		}
		public TerminalNode EOP() { return getToken(BitflowParser.EOP, 0); }
		public TerminalNode CASE() { return getToken(BitflowParser.CASE, 0); }
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
		enterRule(_localctx, 16, RULE_subPipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				setState(114);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CASE) {
					{
					setState(113);
					match(CASE);
					}
				}

				setState(116);
				pipelineName();
				setState(117);
				match(PIPE);
				}
				break;
			}
			setState(124);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				{
				setState(121);
				transform();
				}
				break;
			case 2:
				{
				setState(122);
				fork();
				}
				break;
			case 3:
				{
				setState(123);
				window();
				}
				break;
			}
			setState(134);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PIPE) {
				{
				{
				setState(126);
				match(PIPE);
				setState(130);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
				case 1:
					{
					setState(127);
					transform();
					}
					break;
				case 2:
					{
					setState(128);
					fork();
					}
					break;
				case 3:
					{
					setState(129);
					window();
					}
					break;
				}
				}
				}
				setState(136);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(138);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOP) {
				{
				setState(137);
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

	public static class WindowSubPipelineContext extends ParserRuleContext {
		public List<TransformContext> transform() {
			return getRuleContexts(TransformContext.class);
		}
		public TransformContext transform(int i) {
			return getRuleContext(TransformContext.class,i);
		}
		public List<ForkContext> fork() {
			return getRuleContexts(ForkContext.class);
		}
		public ForkContext fork(int i) {
			return getRuleContext(ForkContext.class,i);
		}
		public List<TerminalNode> PIPE() { return getTokens(BitflowParser.PIPE); }
		public TerminalNode PIPE(int i) {
			return getToken(BitflowParser.PIPE, i);
		}
		public TerminalNode EOP() { return getToken(BitflowParser.EOP, 0); }
		public WindowSubPipelineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_windowSubPipeline; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterWindowSubPipeline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitWindowSubPipeline(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitWindowSubPipeline(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WindowSubPipelineContext windowSubPipeline() throws RecognitionException {
		WindowSubPipelineContext _localctx = new WindowSubPipelineContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_windowSubPipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(142);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				{
				setState(140);
				transform();
				}
				break;
			case 2:
				{
				setState(141);
				fork();
				}
				break;
			}
			setState(151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PIPE) {
				{
				{
				setState(144);
				match(PIPE);
				setState(147);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
				case 1:
					{
					setState(145);
					transform();
					}
					break;
				case 2:
					{
					setState(146);
					fork();
					}
					break;
				}
				}
				}
				setState(153);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(155);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOP) {
				{
				setState(154);
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
		public List<TerminalNode> PIPE() { return getTokens(BitflowParser.PIPE); }
		public TerminalNode PIPE(int i) {
			return getToken(BitflowParser.PIPE, i);
		}
		public MultiinputContext multiinput() {
			return getRuleContext(MultiinputContext.class,0);
		}
		public InputContext input() {
			return getRuleContext(InputContext.class,0);
		}
		public OutputContext output() {
			return getRuleContext(OutputContext.class,0);
		}
		public OutputForkContext outputFork() {
			return getRuleContext(OutputForkContext.class,0);
		}
		public TerminalNode EOP() { return getToken(BitflowParser.EOP, 0); }
		public TerminalNode EOF() { return getToken(BitflowParser.EOF, 0); }
		public List<TransformContext> transform() {
			return getRuleContexts(TransformContext.class);
		}
		public TransformContext transform(int i) {
			return getRuleContext(TransformContext.class,i);
		}
		public List<ForkContext> fork() {
			return getRuleContexts(ForkContext.class);
		}
		public ForkContext fork(int i) {
			return getRuleContext(ForkContext.class,i);
		}
		public List<WindowContext> window() {
			return getRuleContexts(WindowContext.class);
		}
		public WindowContext window(int i) {
			return getRuleContext(WindowContext.class,i);
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
		enterRule(_localctx, 20, RULE_pipeline);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				{
				setState(157);
				multiinput();
				}
				break;
			case 2:
				{
				setState(158);
				input();
				}
				break;
			}
			setState(169);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(161);
					match(PIPE);
					setState(165);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
					case 1:
						{
						setState(162);
						transform();
						}
						break;
					case 2:
						{
						setState(163);
						fork();
						}
						break;
					case 3:
						{
						setState(164);
						window();
						}
						break;
					}
					}
					} 
				}
				setState(171);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			}
			setState(172);
			match(PIPE);
			setState(175);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				{
				setState(173);
				output();
				}
				break;
			case 2:
				{
				setState(174);
				outputFork();
				}
				break;
			}
			setState(178);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				{
				setState(177);
				_la = _input.LA(1);
				if ( !(_la==EOF || _la==EOP) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
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
		public TerminalNode NAME() { return getToken(BitflowParser.NAME, 0); }
		public TerminalNode STRING() { return getToken(BitflowParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(BitflowParser.NUMBER, 0); }
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
		enterRule(_localctx, 22, RULE_parameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			match(NAME);
			setState(181);
			match(T__3);
			setState(182);
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
		enterRule(_localctx, 24, RULE_transformParameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(184);
			match(T__4);
			setState(193);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NAME) {
				{
				setState(185);
				parameter();
				setState(190);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(186);
					match(T__5);
					setState(187);
					parameter();
					}
					}
					setState(192);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(195);
			match(T__6);
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
		public TerminalNode STRING() { return getToken(BitflowParser.STRING, 0); }
		public TerminalNode NAME() { return getToken(BitflowParser.NAME, 0); }
		public TerminalNode NUMBER() { return getToken(BitflowParser.NUMBER, 0); }
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
		enterRule(_localctx, 26, RULE_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
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

	public static class PipelineNameContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(BitflowParser.STRING, 0); }
		public TerminalNode NAME() { return getToken(BitflowParser.NAME, 0); }
		public TerminalNode NUMBER() { return getToken(BitflowParser.NUMBER, 0); }
		public PipelineNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pipelineName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).enterPipelineName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BitflowListener ) ((BitflowListener)listener).exitPipelineName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BitflowVisitor ) return ((BitflowVisitor<? extends T>)visitor).visitPipelineName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PipelineNameContext pipelineName() throws RecognitionException {
		PipelineNameContext _localctx = new PipelineNameContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_pipelineName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
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
		enterRule(_localctx, 30, RULE_schedulingHints);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(201);
			match(T__7);
			setState(210);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NAME) {
				{
				setState(202);
				parameter();
				setState(207);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(203);
					match(T__5);
					setState(204);
					parameter();
					}
					}
					setState(209);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(212);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\27\u00d9\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\3\2\3\2"+
		"\3\3\5\3&\n\3\3\3\5\3)\n\3\3\3\5\3,\n\3\3\3\3\3\3\3\3\3\6\3\62\n\3\r\3"+
		"\16\3\63\3\3\3\3\3\4\5\49\n\4\3\4\5\4<\n\4\3\4\5\4?\n\4\3\4\3\4\6\4C\n"+
		"\4\r\4\16\4D\3\4\3\4\3\5\3\5\5\5K\n\5\3\5\5\5N\n\5\3\5\3\5\3\5\3\5\3\6"+
		"\3\6\3\6\7\6W\n\6\f\6\16\6Z\13\6\3\6\5\6]\n\6\3\7\3\7\5\7a\n\7\3\7\5\7"+
		"d\n\7\3\b\3\b\5\bh\n\b\3\b\5\bk\n\b\3\t\3\t\5\to\n\t\3\t\5\tr\n\t\3\n"+
		"\5\nu\n\n\3\n\3\n\3\n\5\nz\n\n\3\n\3\n\3\n\5\n\177\n\n\3\n\3\n\3\n\3\n"+
		"\5\n\u0085\n\n\7\n\u0087\n\n\f\n\16\n\u008a\13\n\3\n\5\n\u008d\n\n\3\13"+
		"\3\13\5\13\u0091\n\13\3\13\3\13\3\13\5\13\u0096\n\13\7\13\u0098\n\13\f"+
		"\13\16\13\u009b\13\13\3\13\5\13\u009e\n\13\3\f\3\f\5\f\u00a2\n\f\3\f\3"+
		"\f\3\f\3\f\5\f\u00a8\n\f\7\f\u00aa\n\f\f\f\16\f\u00ad\13\f\3\f\3\f\3\f"+
		"\5\f\u00b2\n\f\3\f\5\f\u00b5\n\f\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\7"+
		"\16\u00bf\n\16\f\16\16\16\u00c2\13\16\5\16\u00c4\n\16\3\16\3\16\3\17\3"+
		"\17\3\20\3\20\3\21\3\21\3\21\3\21\7\21\u00d0\n\21\f\21\16\21\u00d3\13"+
		"\21\5\21\u00d5\n\21\3\21\3\21\3\21\2\2\22\2\4\6\b\n\f\16\20\22\24\26\30"+
		"\32\34\36 \2\5\3\3\r\r\3\2\20\21\3\2\20\22\2\u00f0\2\"\3\2\2\2\4%\3\2"+
		"\2\2\68\3\2\2\2\bH\3\2\2\2\nS\3\2\2\2\f^\3\2\2\2\16e\3\2\2\2\20l\3\2\2"+
		"\2\22y\3\2\2\2\24\u0090\3\2\2\2\26\u00a1\3\2\2\2\30\u00b6\3\2\2\2\32\u00ba"+
		"\3\2\2\2\34\u00c7\3\2\2\2\36\u00c9\3\2\2\2 \u00cb\3\2\2\2\"#\5\26\f\2"+
		"#\3\3\2\2\2$&\5\34\17\2%$\3\2\2\2%&\3\2\2\2&(\3\2\2\2\')\5\32\16\2(\'"+
		"\3\2\2\2()\3\2\2\2)+\3\2\2\2*,\5 \21\2+*\3\2\2\2+,\3\2\2\2,-\3\2\2\2-"+
		"\61\7\3\2\2./\5\16\b\2/\60\7\r\2\2\60\62\3\2\2\2\61.\3\2\2\2\62\63\3\2"+
		"\2\2\63\61\3\2\2\2\63\64\3\2\2\2\64\65\3\2\2\2\65\66\7\4\2\2\66\5\3\2"+
		"\2\2\679\5\34\17\28\67\3\2\2\289\3\2\2\29;\3\2\2\2:<\5\32\16\2;:\3\2\2"+
		"\2;<\3\2\2\2<>\3\2\2\2=?\5 \21\2>=\3\2\2\2>?\3\2\2\2?@\3\2\2\2@B\7\3\2"+
		"\2AC\5\22\n\2BA\3\2\2\2CD\3\2\2\2DB\3\2\2\2DE\3\2\2\2EF\3\2\2\2FG\7\4"+
		"\2\2G\7\3\2\2\2HJ\7\5\2\2IK\5\32\16\2JI\3\2\2\2JK\3\2\2\2KM\3\2\2\2LN"+
		"\5 \21\2ML\3\2\2\2MN\3\2\2\2NO\3\2\2\2OP\7\3\2\2PQ\5\24\13\2QR\7\4\2\2"+
		"R\t\3\2\2\2SX\5\f\7\2TU\7\r\2\2UW\5\f\7\2VT\3\2\2\2WZ\3\2\2\2XV\3\2\2"+
		"\2XY\3\2\2\2Y\\\3\2\2\2ZX\3\2\2\2[]\7\r\2\2\\[\3\2\2\2\\]\3\2\2\2]\13"+
		"\3\2\2\2^`\5\34\17\2_a\5\32\16\2`_\3\2\2\2`a\3\2\2\2ac\3\2\2\2bd\5 \21"+
		"\2cb\3\2\2\2cd\3\2\2\2d\r\3\2\2\2eg\5\34\17\2fh\5\32\16\2gf\3\2\2\2gh"+
		"\3\2\2\2hj\3\2\2\2ik\5 \21\2ji\3\2\2\2jk\3\2\2\2k\17\3\2\2\2ln\5\34\17"+
		"\2mo\5\32\16\2nm\3\2\2\2no\3\2\2\2oq\3\2\2\2pr\5 \21\2qp\3\2\2\2qr\3\2"+
		"\2\2r\21\3\2\2\2su\7\f\2\2ts\3\2\2\2tu\3\2\2\2uv\3\2\2\2vw\5\36\20\2w"+
		"x\7\16\2\2xz\3\2\2\2yt\3\2\2\2yz\3\2\2\2z~\3\2\2\2{\177\5\20\t\2|\177"+
		"\5\6\4\2}\177\5\b\5\2~{\3\2\2\2~|\3\2\2\2~}\3\2\2\2\177\u0088\3\2\2\2"+
		"\u0080\u0084\7\16\2\2\u0081\u0085\5\20\t\2\u0082\u0085\5\6\4\2\u0083\u0085"+
		"\5\b\5\2\u0084\u0081\3\2\2\2\u0084\u0082\3\2\2\2\u0084\u0083\3\2\2\2\u0085"+
		"\u0087\3\2\2\2\u0086\u0080\3\2\2\2\u0087\u008a\3\2\2\2\u0088\u0086\3\2"+
		"\2\2\u0088\u0089\3\2\2\2\u0089\u008c\3\2\2\2\u008a\u0088\3\2\2\2\u008b"+
		"\u008d\7\r\2\2\u008c\u008b\3\2\2\2\u008c\u008d\3\2\2\2\u008d\23\3\2\2"+
		"\2\u008e\u0091\5\20\t\2\u008f\u0091\5\6\4\2\u0090\u008e\3\2\2\2\u0090"+
		"\u008f\3\2\2\2\u0091\u0099\3\2\2\2\u0092\u0095\7\16\2\2\u0093\u0096\5"+
		"\20\t\2\u0094\u0096\5\6\4\2\u0095\u0093\3\2\2\2\u0095\u0094\3\2\2\2\u0096"+
		"\u0098\3\2\2\2\u0097\u0092\3\2\2\2\u0098\u009b\3\2\2\2\u0099\u0097\3\2"+
		"\2\2\u0099\u009a\3\2\2\2\u009a\u009d\3\2\2\2\u009b\u0099\3\2\2\2\u009c"+
		"\u009e\7\r\2\2\u009d\u009c\3\2\2\2\u009d\u009e\3\2\2\2\u009e\25\3\2\2"+
		"\2\u009f\u00a2\5\n\6\2\u00a0\u00a2\5\f\7\2\u00a1\u009f\3\2\2\2\u00a1\u00a0"+
		"\3\2\2\2\u00a2\u00ab\3\2\2\2\u00a3\u00a7\7\16\2\2\u00a4\u00a8\5\20\t\2"+
		"\u00a5\u00a8\5\6\4\2\u00a6\u00a8\5\b\5\2\u00a7\u00a4\3\2\2\2\u00a7\u00a5"+
		"\3\2\2\2\u00a7\u00a6\3\2\2\2\u00a8\u00aa\3\2\2\2\u00a9\u00a3\3\2\2\2\u00aa"+
		"\u00ad\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00ae\3\2"+
		"\2\2\u00ad\u00ab\3\2\2\2\u00ae\u00b1\7\16\2\2\u00af\u00b2\5\16\b\2\u00b0"+
		"\u00b2\5\4\3\2\u00b1\u00af\3\2\2\2\u00b1\u00b0\3\2\2\2\u00b2\u00b4\3\2"+
		"\2\2\u00b3\u00b5\t\2\2\2\u00b4\u00b3\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5"+
		"\27\3\2\2\2\u00b6\u00b7\7\22\2\2\u00b7\u00b8\7\6\2\2\u00b8\u00b9\t\3\2"+
		"\2\u00b9\31\3\2\2\2\u00ba\u00c3\7\7\2\2\u00bb\u00c0\5\30\r\2\u00bc\u00bd"+
		"\7\b\2\2\u00bd\u00bf\5\30\r\2\u00be\u00bc\3\2\2\2\u00bf\u00c2\3\2\2\2"+
		"\u00c0\u00be\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1\u00c4\3\2\2\2\u00c2\u00c0"+
		"\3\2\2\2\u00c3\u00bb\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5"+
		"\u00c6\7\t\2\2\u00c6\33\3\2\2\2\u00c7\u00c8\t\4\2\2\u00c8\35\3\2\2\2\u00c9"+
		"\u00ca\t\4\2\2\u00ca\37\3\2\2\2\u00cb\u00d4\7\n\2\2\u00cc\u00d1\5\30\r"+
		"\2\u00cd\u00ce\7\b\2\2\u00ce\u00d0\5\30\r\2\u00cf\u00cd\3\2\2\2\u00d0"+
		"\u00d3\3\2\2\2\u00d1\u00cf\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00d5\3\2"+
		"\2\2\u00d3\u00d1\3\2\2\2\u00d4\u00cc\3\2\2\2\u00d4\u00d5\3\2\2\2\u00d5"+
		"\u00d6\3\2\2\2\u00d6\u00d7\7\13\2\2\u00d7!\3\2\2\2\'%(+\638;>DJMX\\`c"+
		"gjnqty~\u0084\u0088\u008c\u0090\u0095\u0099\u009d\u00a1\u00a7\u00ab\u00b1"+
		"\u00b4\u00c0\u00c3\u00d1\u00d4";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}