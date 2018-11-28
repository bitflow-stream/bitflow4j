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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		CASE=10, EOP=11, PIPE=12, PIPE_NAME=13, STRING=14, NUMBER=15, NAME=16, 
		COMMENT=17, MULTILINE_COMMENT=18, NEWLINE=19, WHITESPACE=20, TAB=21;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"LETTER", "LETTER_WITHOUT_DASH", "F", "I", "SINGLE_DASH", "CASE", "EOP", 
		"PIPE", "PIPE_NAME", "STRING", "NUMBER", "NAME", "COMMENT", "MULTILINE_COMMENT", 
		"NEWLINE", "WHITESPACE", "TAB"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\27\u00c0\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r"+
		"\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20"+
		"a\n\20\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23"+
		"\5\23p\n\23\3\24\3\24\7\24t\n\24\f\24\16\24w\13\24\3\24\3\24\3\25\6\25"+
		"|\n\25\r\25\16\25}\3\26\7\26\u0081\n\26\f\26\16\26\u0084\13\26\3\26\3"+
		"\26\3\26\7\26\u0089\n\26\f\26\16\26\u008c\13\26\3\26\3\26\3\26\3\26\5"+
		"\26\u0092\n\26\3\27\3\27\3\27\3\27\7\27\u0098\n\27\f\27\16\27\u009b\13"+
		"\27\3\27\5\27\u009e\n\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\7\30"+
		"\u00a8\n\30\f\30\16\30\u00ab\13\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31"+
		"\3\31\3\31\3\32\3\32\3\32\5\32\u00b9\n\32\3\32\3\32\3\33\3\33\3\33\3\33"+
		"\4u\u00a9\2\34\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\2\27\2\31\2"+
		"\33\2\35\2\37\f!\r#\16%\17\'\20)\21+\22-\23/\24\61\25\63\26\65\27\3\2"+
		"\b\7\2/<C\\^^aac|\b\2\60\60\62<C\\^^aac|\4\2HHhh\4\2KKkk\3\2\62;\4\2\f"+
		"\f\17\17\2\u00c6\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\37\3\2\2"+
		"\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2"+
		"\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\3\67\3\2"+
		"\2\2\59\3\2\2\2\7;\3\2\2\2\tB\3\2\2\2\13D\3\2\2\2\rF\3\2\2\2\17H\3\2\2"+
		"\2\21J\3\2\2\2\23L\3\2\2\2\25N\3\2\2\2\27P\3\2\2\2\31R\3\2\2\2\33T\3\2"+
		"\2\2\35V\3\2\2\2\37`\3\2\2\2!b\3\2\2\2#d\3\2\2\2%o\3\2\2\2\'q\3\2\2\2"+
		"){\3\2\2\2+\u0091\3\2\2\2-\u0093\3\2\2\2/\u00a3\3\2\2\2\61\u00b1\3\2\2"+
		"\2\63\u00b8\3\2\2\2\65\u00bc\3\2\2\2\678\7}\2\28\4\3\2\2\29:\7\177\2\2"+
		":\6\3\2\2\2;<\7y\2\2<=\7k\2\2=>\7p\2\2>?\7f\2\2?@\7q\2\2@A\7y\2\2A\b\3"+
		"\2\2\2BC\7?\2\2C\n\3\2\2\2DE\7*\2\2E\f\3\2\2\2FG\7.\2\2G\16\3\2\2\2HI"+
		"\7+\2\2I\20\3\2\2\2JK\7]\2\2K\22\3\2\2\2LM\7_\2\2M\24\3\2\2\2NO\t\2\2"+
		"\2O\26\3\2\2\2PQ\t\3\2\2Q\30\3\2\2\2RS\t\4\2\2S\32\3\2\2\2TU\t\5\2\2U"+
		"\34\3\2\2\2VW\7/\2\2W\36\3\2\2\2XY\7e\2\2YZ\7c\2\2Z[\7u\2\2[a\7g\2\2\\"+
		"]\7E\2\2]^\7C\2\2^_\7U\2\2_a\7G\2\2`X\3\2\2\2`\\\3\2\2\2a \3\2\2\2bc\7"+
		"=\2\2c\"\3\2\2\2de\7/\2\2ef\7@\2\2f$\3\2\2\2gh\7$\2\2hi\5+\26\2ij\7$\2"+
		"\2jk\7<\2\2kp\3\2\2\2lm\5+\26\2mn\7<\2\2np\3\2\2\2og\3\2\2\2ol\3\2\2\2"+
		"p&\3\2\2\2qu\7$\2\2rt\13\2\2\2sr\3\2\2\2tw\3\2\2\2uv\3\2\2\2us\3\2\2\2"+
		"vx\3\2\2\2wu\3\2\2\2xy\7$\2\2y(\3\2\2\2z|\t\6\2\2{z\3\2\2\2|}\3\2\2\2"+
		"}{\3\2\2\2}~\3\2\2\2~*\3\2\2\2\177\u0081\5\25\13\2\u0080\177\3\2\2\2\u0081"+
		"\u0084\3\2\2\2\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0085\3\2"+
		"\2\2\u0084\u0082\3\2\2\2\u0085\u0092\5\27\f\2\u0086\u008a\7$\2\2\u0087"+
		"\u0089\5\25\13\2\u0088\u0087\3\2\2\2\u0089\u008c\3\2\2\2\u008a\u0088\3"+
		"\2\2\2\u008a\u008b\3\2\2\2\u008b\u008d\3\2\2\2\u008c\u008a\3\2\2\2\u008d"+
		"\u008e\5\27\f\2\u008e\u008f\7$\2\2\u008f\u0092\3\2\2\2\u0090\u0092\5\35"+
		"\17\2\u0091\u0082\3\2\2\2\u0091\u0086\3\2\2\2\u0091\u0090\3\2\2\2\u0092"+
		",\3\2\2\2\u0093\u0094\7\61\2\2\u0094\u0095\7\61\2\2\u0095\u0099\3\2\2"+
		"\2\u0096\u0098\n\7\2\2\u0097\u0096\3\2\2\2\u0098\u009b\3\2\2\2\u0099\u0097"+
		"\3\2\2\2\u0099\u009a\3\2\2\2\u009a\u009d\3\2\2\2\u009b\u0099\3\2\2\2\u009c"+
		"\u009e\7\17\2\2\u009d\u009c\3\2\2\2\u009d\u009e\3\2\2\2\u009e\u009f\3"+
		"\2\2\2\u009f\u00a0\5\61\31\2\u00a0\u00a1\3\2\2\2\u00a1\u00a2\b\27\2\2"+
		"\u00a2.\3\2\2\2\u00a3\u00a4\7\61\2\2\u00a4\u00a5\7,\2\2\u00a5\u00a9\3"+
		"\2\2\2\u00a6\u00a8\13\2\2\2\u00a7\u00a6\3\2\2\2\u00a8\u00ab\3\2\2\2\u00a9"+
		"\u00aa\3\2\2\2\u00a9\u00a7\3\2\2\2\u00aa\u00ac\3\2\2\2\u00ab\u00a9\3\2"+
		"\2\2\u00ac\u00ad\7,\2\2\u00ad\u00ae\7\61\2\2\u00ae\u00af\3\2\2\2\u00af"+
		"\u00b0\b\30\2\2\u00b0\60\3\2\2\2\u00b1\u00b2\t\7\2\2\u00b2\u00b3\3\2\2"+
		"\2\u00b3\u00b4\b\31\2\2\u00b4\62\3\2\2\2\u00b5\u00b9\7\"\2\2\u00b6\u00b7"+
		"\7^\2\2\u00b7\u00b9\7u\2\2\u00b8\u00b5\3\2\2\2\u00b8\u00b6\3\2\2\2\u00b9"+
		"\u00ba\3\2\2\2\u00ba\u00bb\b\32\2\2\u00bb\64\3\2\2\2\u00bc\u00bd\7\13"+
		"\2\2\u00bd\u00be\3\2\2\2\u00be\u00bf\b\33\2\2\u00bf\66\3\2\2\2\16\2`o"+
		"u}\u0082\u008a\u0091\u0099\u009d\u00a9\u00b8\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}