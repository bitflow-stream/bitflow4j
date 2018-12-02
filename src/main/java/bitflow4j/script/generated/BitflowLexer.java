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
		EOP=10, NEXT=11, STRING=12, NUMBER=13, NAME=14, COMMENT=15, NEWLINE=16, 
		WHITESPACE=17, TAB=18;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"EOP", "NEXT", "STRING", "NUMBER", "NAME", "COMMENT", "NEWLINE", "WHITESPACE", 
		"TAB"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\24\u0084\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\r\3\r\7"+
		"\rF\n\r\f\r\16\rI\13\r\3\r\3\r\3\r\7\rN\n\r\f\r\16\rQ\13\r\3\r\3\r\3\r"+
		"\7\rV\n\r\f\r\16\rY\13\r\3\r\5\r\\\n\r\3\16\6\16_\n\16\r\16\16\16`\3\17"+
		"\6\17d\n\17\r\17\16\17e\3\20\3\20\7\20j\n\20\f\20\16\20m\13\20\3\20\3"+
		"\20\3\20\3\20\3\21\3\21\3\21\5\21v\n\21\3\21\3\21\3\22\3\22\3\22\5\22"+
		"}\n\22\3\22\3\22\3\23\3\23\3\23\3\23\5GOW\2\24\3\3\5\4\7\5\t\6\13\7\r"+
		"\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\3\2\5"+
		"\3\2\62;\7\2/<C\\^^aac|\4\2\f\f\17\17\2\u008d\2\3\3\2\2\2\2\5\3\2\2\2"+
		"\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3"+
		"\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2"+
		"\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\3\'\3\2"+
		"\2\2\5)\3\2\2\2\7+\3\2\2\2\t-\3\2\2\2\13/\3\2\2\2\r\61\3\2\2\2\17\63\3"+
		"\2\2\2\21:\3\2\2\2\23<\3\2\2\2\25>\3\2\2\2\27@\3\2\2\2\31[\3\2\2\2\33"+
		"^\3\2\2\2\35c\3\2\2\2\37g\3\2\2\2!u\3\2\2\2#|\3\2\2\2%\u0080\3\2\2\2\'"+
		"(\7}\2\2(\4\3\2\2\2)*\7\177\2\2*\6\3\2\2\2+,\7?\2\2,\b\3\2\2\2-.\7*\2"+
		"\2.\n\3\2\2\2/\60\7.\2\2\60\f\3\2\2\2\61\62\7+\2\2\62\16\3\2\2\2\63\64"+
		"\7y\2\2\64\65\7k\2\2\65\66\7p\2\2\66\67\7f\2\2\678\7q\2\289\7y\2\29\20"+
		"\3\2\2\2:;\7]\2\2;\22\3\2\2\2<=\7_\2\2=\24\3\2\2\2>?\7=\2\2?\26\3\2\2"+
		"\2@A\7/\2\2AB\7@\2\2B\30\3\2\2\2CG\7$\2\2DF\13\2\2\2ED\3\2\2\2FI\3\2\2"+
		"\2GH\3\2\2\2GE\3\2\2\2HJ\3\2\2\2IG\3\2\2\2J\\\7$\2\2KO\7)\2\2LN\13\2\2"+
		"\2ML\3\2\2\2NQ\3\2\2\2OP\3\2\2\2OM\3\2\2\2PR\3\2\2\2QO\3\2\2\2R\\\7)\2"+
		"\2SW\7b\2\2TV\13\2\2\2UT\3\2\2\2VY\3\2\2\2WX\3\2\2\2WU\3\2\2\2XZ\3\2\2"+
		"\2YW\3\2\2\2Z\\\7b\2\2[C\3\2\2\2[K\3\2\2\2[S\3\2\2\2\\\32\3\2\2\2]_\t"+
		"\2\2\2^]\3\2\2\2_`\3\2\2\2`^\3\2\2\2`a\3\2\2\2a\34\3\2\2\2bd\t\3\2\2c"+
		"b\3\2\2\2de\3\2\2\2ec\3\2\2\2ef\3\2\2\2f\36\3\2\2\2gk\7%\2\2hj\n\4\2\2"+
		"ih\3\2\2\2jm\3\2\2\2ki\3\2\2\2kl\3\2\2\2ln\3\2\2\2mk\3\2\2\2no\5!\21\2"+
		"op\3\2\2\2pq\b\20\2\2q \3\2\2\2rv\t\4\2\2st\7\17\2\2tv\7\f\2\2ur\3\2\2"+
		"\2us\3\2\2\2vw\3\2\2\2wx\b\21\2\2x\"\3\2\2\2y}\7\"\2\2z{\7^\2\2{}\7u\2"+
		"\2|y\3\2\2\2|z\3\2\2\2}~\3\2\2\2~\177\b\22\2\2\177$\3\2\2\2\u0080\u0081"+
		"\7\13\2\2\u0081\u0082\3\2\2\2\u0082\u0083\b\23\2\2\u0083&\3\2\2\2\f\2"+
		"GOW[`eku|\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}