// Generated from Bitflow.g4 by ANTLR 4.7.1
package bitflow4j.script.generated;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class BitflowLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		OPEN=1, CLOSE=2, EOP=3, NEXT=4, OPEN_PARAMS=5, CLOSE_PARAMS=6, EQ=7, SEP=8, 
		OPEN_HINTS=9, CLOSE_HINTS=10, BATCH=11, STRING=12, IDENTIFIER=13, COMMENT=14, 
		NEWLINE=15, WHITESPACE=16, TAB=17;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"OPEN", "CLOSE", "EOP", "NEXT", "OPEN_PARAMS", "CLOSE_PARAMS", "EQ", "SEP", 
		"OPEN_HINTS", "CLOSE_HINTS", "BATCH", "STRING", "IDENTIFIER", "COMMENT", 
		"NEWLINE", "WHITESPACE", "TAB"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'{'", "'}'", "';'", "'->'", "'('", "')'", "'='", "','", "'['", 
		"']'", "'batch'", null, null, null, null, null, "'\t'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "OPEN", "CLOSE", "EOP", "NEXT", "OPEN_PARAMS", "CLOSE_PARAMS", "EQ", 
		"SEP", "OPEN_HINTS", "CLOSE_HINTS", "BATCH", "STRING", "IDENTIFIER", "COMMENT", 
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


	public BitflowLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Bitflow.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\23|\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3"+
		"\n\3\n\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\7\rC\n\r\f\r\16\rF\13"+
		"\r\3\r\3\r\3\r\7\rK\n\r\f\r\16\rN\13\r\3\r\3\r\3\r\7\rS\n\r\f\r\16\rV"+
		"\13\r\3\r\5\rY\n\r\3\16\6\16\\\n\16\r\16\16\16]\3\17\3\17\7\17b\n\17\f"+
		"\17\16\17e\13\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\5\20n\n\20\3\20\3"+
		"\20\3\21\3\21\3\21\5\21u\n\21\3\21\3\21\3\22\3\22\3\22\3\22\5DLT\2\23"+
		"\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20"+
		"\37\21!\22#\23\3\2\4\n\2\'(,-/<AAC\\^^aac|\4\2\f\f\17\17\2\u0084\2\3\3"+
		"\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2"+
		"\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3"+
		"\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\3"+
		"%\3\2\2\2\5\'\3\2\2\2\7)\3\2\2\2\t+\3\2\2\2\13.\3\2\2\2\r\60\3\2\2\2\17"+
		"\62\3\2\2\2\21\64\3\2\2\2\23\66\3\2\2\2\258\3\2\2\2\27:\3\2\2\2\31X\3"+
		"\2\2\2\33[\3\2\2\2\35_\3\2\2\2\37m\3\2\2\2!t\3\2\2\2#x\3\2\2\2%&\7}\2"+
		"\2&\4\3\2\2\2\'(\7\177\2\2(\6\3\2\2\2)*\7=\2\2*\b\3\2\2\2+,\7/\2\2,-\7"+
		"@\2\2-\n\3\2\2\2./\7*\2\2/\f\3\2\2\2\60\61\7+\2\2\61\16\3\2\2\2\62\63"+
		"\7?\2\2\63\20\3\2\2\2\64\65\7.\2\2\65\22\3\2\2\2\66\67\7]\2\2\67\24\3"+
		"\2\2\289\7_\2\29\26\3\2\2\2:;\7d\2\2;<\7c\2\2<=\7v\2\2=>\7e\2\2>?\7j\2"+
		"\2?\30\3\2\2\2@D\7$\2\2AC\13\2\2\2BA\3\2\2\2CF\3\2\2\2DE\3\2\2\2DB\3\2"+
		"\2\2EG\3\2\2\2FD\3\2\2\2GY\7$\2\2HL\7)\2\2IK\13\2\2\2JI\3\2\2\2KN\3\2"+
		"\2\2LM\3\2\2\2LJ\3\2\2\2MO\3\2\2\2NL\3\2\2\2OY\7)\2\2PT\7b\2\2QS\13\2"+
		"\2\2RQ\3\2\2\2SV\3\2\2\2TU\3\2\2\2TR\3\2\2\2UW\3\2\2\2VT\3\2\2\2WY\7b"+
		"\2\2X@\3\2\2\2XH\3\2\2\2XP\3\2\2\2Y\32\3\2\2\2Z\\\t\2\2\2[Z\3\2\2\2\\"+
		"]\3\2\2\2][\3\2\2\2]^\3\2\2\2^\34\3\2\2\2_c\7%\2\2`b\n\3\2\2a`\3\2\2\2"+
		"be\3\2\2\2ca\3\2\2\2cd\3\2\2\2df\3\2\2\2ec\3\2\2\2fg\5\37\20\2gh\3\2\2"+
		"\2hi\b\17\2\2i\36\3\2\2\2jn\t\3\2\2kl\7\17\2\2ln\7\f\2\2mj\3\2\2\2mk\3"+
		"\2\2\2no\3\2\2\2op\b\20\2\2p \3\2\2\2qu\7\"\2\2rs\7^\2\2su\7u\2\2tq\3"+
		"\2\2\2tr\3\2\2\2uv\3\2\2\2vw\b\21\2\2w\"\3\2\2\2xy\7\13\2\2yz\3\2\2\2"+
		"z{\b\22\2\2{$\3\2\2\2\13\2DLTX]cmt\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}