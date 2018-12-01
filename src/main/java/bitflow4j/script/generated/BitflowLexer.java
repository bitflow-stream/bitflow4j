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
		EOP=10, PIPE=11, STRING=12, NUMBER=13, NAME=14, COMMENT=15, NEWLINE=16, 
		WHITESPACE=17, TAB=18;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"START_LETTER", "END_LETTER", "SINGLE_DASH", "EOP", "PIPE", "STRING", 
		"NUMBER", "NAME", "COMMENT", "NEWLINE", "WHITESPACE", "TAB"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'{'", "'}'", "'='", "'('", "','", "')'", "'window'", "'['", "']'", 
		"';'", "'->'", null, null, null, null, null, null, "'\t'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, "EOP", "PIPE", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\24\u0098\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\3\2\3\2\3\3\3\3\3\4\3\4"+
		"\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3"+
		"\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\17\3\20\3\20\7\20R\n\20"+
		"\f\20\16\20U\13\20\3\20\3\20\3\20\7\20Z\n\20\f\20\16\20]\13\20\3\20\3"+
		"\20\3\20\7\20b\n\20\f\20\16\20e\13\20\3\20\5\20h\n\20\3\21\6\21k\n\21"+
		"\r\21\16\21l\3\22\3\22\7\22q\n\22\f\22\16\22t\13\22\3\22\5\22w\n\22\3"+
		"\22\5\22z\n\22\3\23\3\23\7\23~\n\23\f\23\16\23\u0081\13\23\3\23\3\23\3"+
		"\23\3\23\3\24\3\24\3\24\5\24\u008a\n\24\3\24\3\24\3\25\3\25\3\25\5\25"+
		"\u0091\n\25\3\25\3\25\3\26\3\26\3\26\3\26\5S[c\2\27\3\3\5\4\7\5\t\6\13"+
		"\7\r\b\17\t\21\n\23\13\25\2\27\2\31\2\33\f\35\r\37\16!\17#\20%\21\'\22"+
		")\23+\24\3\2\6\7\2/<C\\^^aac|\7\2\60<C\\^^aac|\3\2\62;\4\2\f\f\17\17\2"+
		"\u00a0\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2"+
		"\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\33\3\2\2\2\2\35\3"+
		"\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3"+
		"\2\2\2\2+\3\2\2\2\3-\3\2\2\2\5/\3\2\2\2\7\61\3\2\2\2\t\63\3\2\2\2\13\65"+
		"\3\2\2\2\r\67\3\2\2\2\179\3\2\2\2\21@\3\2\2\2\23B\3\2\2\2\25D\3\2\2\2"+
		"\27F\3\2\2\2\31H\3\2\2\2\33J\3\2\2\2\35L\3\2\2\2\37g\3\2\2\2!j\3\2\2\2"+
		"#y\3\2\2\2%{\3\2\2\2\'\u0089\3\2\2\2)\u0090\3\2\2\2+\u0094\3\2\2\2-.\7"+
		"}\2\2.\4\3\2\2\2/\60\7\177\2\2\60\6\3\2\2\2\61\62\7?\2\2\62\b\3\2\2\2"+
		"\63\64\7*\2\2\64\n\3\2\2\2\65\66\7.\2\2\66\f\3\2\2\2\678\7+\2\28\16\3"+
		"\2\2\29:\7y\2\2:;\7k\2\2;<\7p\2\2<=\7f\2\2=>\7q\2\2>?\7y\2\2?\20\3\2\2"+
		"\2@A\7]\2\2A\22\3\2\2\2BC\7_\2\2C\24\3\2\2\2DE\t\2\2\2E\26\3\2\2\2FG\t"+
		"\3\2\2G\30\3\2\2\2HI\7/\2\2I\32\3\2\2\2JK\7=\2\2K\34\3\2\2\2LM\7/\2\2"+
		"MN\7@\2\2N\36\3\2\2\2OS\7$\2\2PR\13\2\2\2QP\3\2\2\2RU\3\2\2\2ST\3\2\2"+
		"\2SQ\3\2\2\2TV\3\2\2\2US\3\2\2\2Vh\7$\2\2W[\7)\2\2XZ\13\2\2\2YX\3\2\2"+
		"\2Z]\3\2\2\2[\\\3\2\2\2[Y\3\2\2\2\\^\3\2\2\2][\3\2\2\2^h\7)\2\2_c\7b\2"+
		"\2`b\13\2\2\2a`\3\2\2\2be\3\2\2\2cd\3\2\2\2ca\3\2\2\2df\3\2\2\2ec\3\2"+
		"\2\2fh\7b\2\2gO\3\2\2\2gW\3\2\2\2g_\3\2\2\2h \3\2\2\2ik\t\4\2\2ji\3\2"+
		"\2\2kl\3\2\2\2lj\3\2\2\2lm\3\2\2\2m\"\3\2\2\2nv\5\25\13\2oq\5\25\13\2"+
		"po\3\2\2\2qt\3\2\2\2rp\3\2\2\2rs\3\2\2\2su\3\2\2\2tr\3\2\2\2uw\5\27\f"+
		"\2vr\3\2\2\2vw\3\2\2\2wz\3\2\2\2xz\5\31\r\2yn\3\2\2\2yx\3\2\2\2z$\3\2"+
		"\2\2{\177\7%\2\2|~\n\5\2\2}|\3\2\2\2~\u0081\3\2\2\2\177}\3\2\2\2\177\u0080"+
		"\3\2\2\2\u0080\u0082\3\2\2\2\u0081\177\3\2\2\2\u0082\u0083\5\'\24\2\u0083"+
		"\u0084\3\2\2\2\u0084\u0085\b\23\2\2\u0085&\3\2\2\2\u0086\u008a\t\5\2\2"+
		"\u0087\u0088\7\17\2\2\u0088\u008a\7\f\2\2\u0089\u0086\3\2\2\2\u0089\u0087"+
		"\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u008c\b\24\2\2\u008c(\3\2\2\2\u008d"+
		"\u0091\7\"\2\2\u008e\u008f\7^\2\2\u008f\u0091\7u\2\2\u0090\u008d\3\2\2"+
		"\2\u0090\u008e\3\2\2\2\u0091\u0092\3\2\2\2\u0092\u0093\b\25\2\2\u0093"+
		"*\3\2\2\2\u0094\u0095\7\13\2\2\u0095\u0096\3\2\2\2\u0096\u0097\b\26\2"+
		"\2\u0097,\3\2\2\2\16\2S[cglrvy\177\u0089\u0090\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}