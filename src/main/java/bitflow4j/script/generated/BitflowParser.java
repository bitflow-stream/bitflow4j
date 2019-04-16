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
		RULE_parameter = 4, RULE_parameterList = 5, RULE_parameters = 6, RULE_pipelines = 7, 
		RULE_pipeline = 8, RULE_pipelineElement = 9, RULE_pipelineTailElement = 10, 
		RULE_processingStep = 11, RULE_fork = 12, RULE_namedSubPipeline = 13, 
		RULE_subPipeline = 14, RULE_multiplexFork = 15, RULE_window = 16, RULE_schedulingHints = 17;
	public static final String[] ruleNames = {
		"script", "dataInput", "dataOutput", "name", "parameter", "parameterList", 
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
			setState(36);
			pipelines();
			setState(37);
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
			setState(40); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(39);
				name();
				}
				}
				setState(42); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==STRING || _la==IDENTIFIER );
			setState(45);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_HINTS) {
				{
				setState(44);
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
			setState(47);
			name();
			setState(49);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_HINTS) {
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
			setState(51);
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
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public TerminalNode EQ() { return getToken(BitflowParser.EQ, 0); }
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
			setState(53);
			name();
			setState(54);
			match(EQ);
			setState(55);
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
		enterRule(_localctx, 10, RULE_parameterList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(57);
			parameter();
			setState(62);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(58);
					match(SEP);
					setState(59);
					parameter();
					}
					} 
				}
				setState(64);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
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
		enterRule(_localctx, 12, RULE_parameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			match(OPEN_PARAMS);
			setState(70);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING || _la==IDENTIFIER) {
				{
				setState(66);
				parameterList();
				setState(68);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEP) {
					{
					setState(67);
					match(SEP);
					}
				}

				}
			}

			setState(72);
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
		enterRule(_localctx, 14, RULE_pipelines);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			pipeline();
			setState(79);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(75);
					match(EOP);
					setState(76);
					pipeline();
					}
					} 
				}
				setState(81);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			setState(83);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOP) {
				{
				setState(82);
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
		enterRule(_localctx, 16, RULE_pipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(85);
				dataInput();
				}
				break;
			case 2:
				{
				setState(86);
				pipelineElement();
				}
				break;
			case 3:
				{
				setState(87);
				match(OPEN);
				setState(88);
				pipelines();
				setState(89);
				match(CLOSE);
				}
				break;
			}
			setState(97);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEXT) {
				{
				{
				setState(93);
				match(NEXT);
				setState(94);
				pipelineTailElement();
				}
				}
				setState(99);
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
		enterRule(_localctx, 18, RULE_pipelineElement);
		try {
			setState(103);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(100);
				processingStep();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(101);
				fork();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(102);
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
		enterRule(_localctx, 20, RULE_pipelineTailElement);
		try {
			setState(108);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(105);
				pipelineElement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(106);
				multiplexFork();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(107);
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
		enterRule(_localctx, 22, RULE_processingStep);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			name();
			setState(111);
			parameters();
			setState(113);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_HINTS) {
				{
				setState(112);
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
		enterRule(_localctx, 24, RULE_fork);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			name();
			setState(116);
			parameters();
			setState(118);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_HINTS) {
				{
				setState(117);
				schedulingHints();
				}
			}

			setState(120);
			match(OPEN);
			setState(121);
			namedSubPipeline();
			setState(126);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(122);
					match(EOP);
					setState(123);
					namedSubPipeline();
					}
					} 
				}
				setState(128);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
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

			setState(132);
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
		enterRule(_localctx, 26, RULE_namedSubPipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(134);
				name();
				}
				}
				setState(137); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==STRING || _la==IDENTIFIER );
			setState(139);
			match(NEXT);
			setState(140);
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
		enterRule(_localctx, 28, RULE_subPipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(142);
			pipelineTailElement();
			setState(147);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEXT) {
				{
				{
				setState(143);
				match(NEXT);
				setState(144);
				pipelineTailElement();
				}
				}
				setState(149);
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
		enterRule(_localctx, 30, RULE_multiplexFork);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			match(OPEN);
			setState(151);
			subPipeline();
			setState(156);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(152);
					match(EOP);
					setState(153);
					subPipeline();
					}
					} 
				}
				setState(158);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			}
			setState(160);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EOP) {
				{
				setState(159);
				match(EOP);
				}
			}

			setState(162);
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
		enterRule(_localctx, 32, RULE_window);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164);
			match(WINDOW);
			setState(165);
			parameters();
			setState(167);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_HINTS) {
				{
				setState(166);
				schedulingHints();
				}
			}

			setState(169);
			match(OPEN);
			setState(170);
			processingStep();
			setState(175);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEXT) {
				{
				{
				setState(171);
				match(NEXT);
				setState(172);
				processingStep();
				}
				}
				setState(177);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(178);
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
		enterRule(_localctx, 34, RULE_schedulingHints);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			match(OPEN_HINTS);
			setState(185);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING || _la==IDENTIFIER) {
				{
				setState(181);
				parameterList();
				setState(183);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==SEP) {
					{
					setState(182);
					match(SEP);
					}
				}

				}
			}

			setState(187);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\23\u00c0\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\3\2\3\2\3\2\3\3\6\3+\n\3\r\3\16\3,\3\3\5\3\60\n\3\3\4\3\4\5"+
		"\4\64\n\4\3\5\3\5\3\6\3\6\3\6\3\6\3\7\3\7\3\7\7\7?\n\7\f\7\16\7B\13\7"+
		"\3\b\3\b\3\b\5\bG\n\b\5\bI\n\b\3\b\3\b\3\t\3\t\3\t\7\tP\n\t\f\t\16\tS"+
		"\13\t\3\t\5\tV\n\t\3\n\3\n\3\n\3\n\3\n\3\n\5\n^\n\n\3\n\3\n\7\nb\n\n\f"+
		"\n\16\ne\13\n\3\13\3\13\3\13\5\13j\n\13\3\f\3\f\3\f\5\fo\n\f\3\r\3\r\3"+
		"\r\5\rt\n\r\3\16\3\16\3\16\5\16y\n\16\3\16\3\16\3\16\3\16\7\16\177\n\16"+
		"\f\16\16\16\u0082\13\16\3\16\5\16\u0085\n\16\3\16\3\16\3\17\6\17\u008a"+
		"\n\17\r\17\16\17\u008b\3\17\3\17\3\17\3\20\3\20\3\20\7\20\u0094\n\20\f"+
		"\20\16\20\u0097\13\20\3\21\3\21\3\21\3\21\7\21\u009d\n\21\f\21\16\21\u00a0"+
		"\13\21\3\21\5\21\u00a3\n\21\3\21\3\21\3\22\3\22\3\22\5\22\u00aa\n\22\3"+
		"\22\3\22\3\22\3\22\7\22\u00b0\n\22\f\22\16\22\u00b3\13\22\3\22\3\22\3"+
		"\23\3\23\3\23\5\23\u00ba\n\23\5\23\u00bc\n\23\3\23\3\23\3\23\2\2\24\2"+
		"\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$\2\3\3\2\16\17\2\u00c8\2&\3\2"+
		"\2\2\4*\3\2\2\2\6\61\3\2\2\2\b\65\3\2\2\2\n\67\3\2\2\2\f;\3\2\2\2\16C"+
		"\3\2\2\2\20L\3\2\2\2\22]\3\2\2\2\24i\3\2\2\2\26n\3\2\2\2\30p\3\2\2\2\32"+
		"u\3\2\2\2\34\u0089\3\2\2\2\36\u0090\3\2\2\2 \u0098\3\2\2\2\"\u00a6\3\2"+
		"\2\2$\u00b6\3\2\2\2&\'\5\20\t\2\'(\7\2\2\3(\3\3\2\2\2)+\5\b\5\2*)\3\2"+
		"\2\2+,\3\2\2\2,*\3\2\2\2,-\3\2\2\2-/\3\2\2\2.\60\5$\23\2/.\3\2\2\2/\60"+
		"\3\2\2\2\60\5\3\2\2\2\61\63\5\b\5\2\62\64\5$\23\2\63\62\3\2\2\2\63\64"+
		"\3\2\2\2\64\7\3\2\2\2\65\66\t\2\2\2\66\t\3\2\2\2\678\5\b\5\289\7\t\2\2"+
		"9:\5\b\5\2:\13\3\2\2\2;@\5\n\6\2<=\7\n\2\2=?\5\n\6\2><\3\2\2\2?B\3\2\2"+
		"\2@>\3\2\2\2@A\3\2\2\2A\r\3\2\2\2B@\3\2\2\2CH\7\7\2\2DF\5\f\7\2EG\7\n"+
		"\2\2FE\3\2\2\2FG\3\2\2\2GI\3\2\2\2HD\3\2\2\2HI\3\2\2\2IJ\3\2\2\2JK\7\b"+
		"\2\2K\17\3\2\2\2LQ\5\22\n\2MN\7\5\2\2NP\5\22\n\2OM\3\2\2\2PS\3\2\2\2Q"+
		"O\3\2\2\2QR\3\2\2\2RU\3\2\2\2SQ\3\2\2\2TV\7\5\2\2UT\3\2\2\2UV\3\2\2\2"+
		"V\21\3\2\2\2W^\5\4\3\2X^\5\24\13\2YZ\7\3\2\2Z[\5\20\t\2[\\\7\4\2\2\\^"+
		"\3\2\2\2]W\3\2\2\2]X\3\2\2\2]Y\3\2\2\2^c\3\2\2\2_`\7\6\2\2`b\5\26\f\2"+
		"a_\3\2\2\2be\3\2\2\2ca\3\2\2\2cd\3\2\2\2d\23\3\2\2\2ec\3\2\2\2fj\5\30"+
		"\r\2gj\5\32\16\2hj\5\"\22\2if\3\2\2\2ig\3\2\2\2ih\3\2\2\2j\25\3\2\2\2"+
		"ko\5\24\13\2lo\5 \21\2mo\5\6\4\2nk\3\2\2\2nl\3\2\2\2nm\3\2\2\2o\27\3\2"+
		"\2\2pq\5\b\5\2qs\5\16\b\2rt\5$\23\2sr\3\2\2\2st\3\2\2\2t\31\3\2\2\2uv"+
		"\5\b\5\2vx\5\16\b\2wy\5$\23\2xw\3\2\2\2xy\3\2\2\2yz\3\2\2\2z{\7\3\2\2"+
		"{\u0080\5\34\17\2|}\7\5\2\2}\177\5\34\17\2~|\3\2\2\2\177\u0082\3\2\2\2"+
		"\u0080~\3\2\2\2\u0080\u0081\3\2\2\2\u0081\u0084\3\2\2\2\u0082\u0080\3"+
		"\2\2\2\u0083\u0085\7\5\2\2\u0084\u0083\3\2\2\2\u0084\u0085\3\2\2\2\u0085"+
		"\u0086\3\2\2\2\u0086\u0087\7\4\2\2\u0087\33\3\2\2\2\u0088\u008a\5\b\5"+
		"\2\u0089\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u0089\3\2\2\2\u008b\u008c"+
		"\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008e\7\6\2\2\u008e\u008f\5\36\20\2"+
		"\u008f\35\3\2\2\2\u0090\u0095\5\26\f\2\u0091\u0092\7\6\2\2\u0092\u0094"+
		"\5\26\f\2\u0093\u0091\3\2\2\2\u0094\u0097\3\2\2\2\u0095\u0093\3\2\2\2"+
		"\u0095\u0096\3\2\2\2\u0096\37\3\2\2\2\u0097\u0095\3\2\2\2\u0098\u0099"+
		"\7\3\2\2\u0099\u009e\5\36\20\2\u009a\u009b\7\5\2\2\u009b\u009d\5\36\20"+
		"\2\u009c\u009a\3\2\2\2\u009d\u00a0\3\2\2\2\u009e\u009c\3\2\2\2\u009e\u009f"+
		"\3\2\2\2\u009f\u00a2\3\2\2\2\u00a0\u009e\3\2\2\2\u00a1\u00a3\7\5\2\2\u00a2"+
		"\u00a1\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a5\7\4"+
		"\2\2\u00a5!\3\2\2\2\u00a6\u00a7\7\r\2\2\u00a7\u00a9\5\16\b\2\u00a8\u00aa"+
		"\5$\23\2\u00a9\u00a8\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab"+
		"\u00ac\7\3\2\2\u00ac\u00b1\5\30\r\2\u00ad\u00ae\7\6\2\2\u00ae\u00b0\5"+
		"\30\r\2\u00af\u00ad\3\2\2\2\u00b0\u00b3\3\2\2\2\u00b1\u00af\3\2\2\2\u00b1"+
		"\u00b2\3\2\2\2\u00b2\u00b4\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b4\u00b5\7\4"+
		"\2\2\u00b5#\3\2\2\2\u00b6\u00bb\7\13\2\2\u00b7\u00b9\5\f\7\2\u00b8\u00ba"+
		"\7\n\2\2\u00b9\u00b8\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00bc\3\2\2\2\u00bb"+
		"\u00b7\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00be\7\f"+
		"\2\2\u00be%\3\2\2\2\32,/\63@FHQU]cinsx\u0080\u0084\u008b\u0095\u009e\u00a2"+
		"\u00a9\u00b1\u00b9\u00bb";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}