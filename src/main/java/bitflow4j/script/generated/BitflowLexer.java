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
		EOP=10, NEXT=11, STRING=12, NUMBER=13, BOOL=14, IDENTIFIER=15, COMMENT=16, 
		NEWLINE=17, WHITESPACE=18, TAB=19;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"EOP", "NEXT", "STRING", "NUMBER", "BOOL", "IDENTIFIER", "COMMENT", "NEWLINE", 
		"WHITESPACE", "TAB"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\25\u00a2\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7"+
		"\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f"+
		"\3\r\3\r\7\rH\n\r\f\r\16\rK\13\r\3\r\3\r\3\r\7\rP\n\r\f\r\16\rS\13\r\3"+
		"\r\3\r\3\r\7\rX\n\r\f\r\16\r[\13\r\3\r\5\r^\n\r\3\16\6\16a\n\16\r\16\16"+
		"\16b\3\16\3\16\6\16g\n\16\r\16\16\16h\5\16k\n\16\3\17\3\17\3\17\3\17\3"+
		"\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\5"+
		"\17\177\n\17\3\20\6\20\u0082\n\20\r\20\16\20\u0083\3\21\3\21\7\21\u0088"+
		"\n\21\f\21\16\21\u008b\13\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\5\22\u0094"+
		"\n\22\3\22\3\22\3\23\3\23\3\23\5\23\u009b\n\23\3\23\3\23\3\24\3\24\3\24"+
		"\3\24\5IQY\2\25\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31"+
		"\16\33\17\35\20\37\21!\22#\23%\24\'\25\3\2\5\3\2\62;\7\2/<C\\^^aac|\4"+
		"\2\f\f\17\17\2\u00b0\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2"+
		"\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3"+
		"\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2"+
		"\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\3)\3\2\2\2\5+\3\2\2\2"+
		"\7-\3\2\2\2\t/\3\2\2\2\13\61\3\2\2\2\r\63\3\2\2\2\17\65\3\2\2\2\21<\3"+
		"\2\2\2\23>\3\2\2\2\25@\3\2\2\2\27B\3\2\2\2\31]\3\2\2\2\33`\3\2\2\2\35"+
		"~\3\2\2\2\37\u0081\3\2\2\2!\u0085\3\2\2\2#\u0093\3\2\2\2%\u009a\3\2\2"+
		"\2\'\u009e\3\2\2\2)*\7?\2\2*\4\3\2\2\2+,\7*\2\2,\6\3\2\2\2-.\7.\2\2.\b"+
		"\3\2\2\2/\60\7+\2\2\60\n\3\2\2\2\61\62\7}\2\2\62\f\3\2\2\2\63\64\7\177"+
		"\2\2\64\16\3\2\2\2\65\66\7y\2\2\66\67\7k\2\2\678\7p\2\289\7f\2\29:\7q"+
		"\2\2:;\7y\2\2;\20\3\2\2\2<=\7]\2\2=\22\3\2\2\2>?\7_\2\2?\24\3\2\2\2@A"+
		"\7=\2\2A\26\3\2\2\2BC\7/\2\2CD\7@\2\2D\30\3\2\2\2EI\7$\2\2FH\13\2\2\2"+
		"GF\3\2\2\2HK\3\2\2\2IJ\3\2\2\2IG\3\2\2\2JL\3\2\2\2KI\3\2\2\2L^\7$\2\2"+
		"MQ\7)\2\2NP\13\2\2\2ON\3\2\2\2PS\3\2\2\2QR\3\2\2\2QO\3\2\2\2RT\3\2\2\2"+
		"SQ\3\2\2\2T^\7)\2\2UY\7b\2\2VX\13\2\2\2WV\3\2\2\2X[\3\2\2\2YZ\3\2\2\2"+
		"YW\3\2\2\2Z\\\3\2\2\2[Y\3\2\2\2\\^\7b\2\2]E\3\2\2\2]M\3\2\2\2]U\3\2\2"+
		"\2^\32\3\2\2\2_a\t\2\2\2`_\3\2\2\2ab\3\2\2\2b`\3\2\2\2bc\3\2\2\2cj\3\2"+
		"\2\2df\7\60\2\2eg\t\2\2\2fe\3\2\2\2gh\3\2\2\2hf\3\2\2\2hi\3\2\2\2ik\3"+
		"\2\2\2jd\3\2\2\2jk\3\2\2\2k\34\3\2\2\2lm\7v\2\2mn\7t\2\2no\7w\2\2o\177"+
		"\7g\2\2pq\7V\2\2qr\7t\2\2rs\7w\2\2s\177\7g\2\2tu\7h\2\2uv\7c\2\2vw\7n"+
		"\2\2wx\7u\2\2x\177\7g\2\2yz\7H\2\2z{\7c\2\2{|\7n\2\2|}\7u\2\2}\177\7g"+
		"\2\2~l\3\2\2\2~p\3\2\2\2~t\3\2\2\2~y\3\2\2\2\177\36\3\2\2\2\u0080\u0082"+
		"\t\3\2\2\u0081\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0081\3\2\2\2\u0083"+
		"\u0084\3\2\2\2\u0084 \3\2\2\2\u0085\u0089\7%\2\2\u0086\u0088\n\4\2\2\u0087"+
		"\u0086\3\2\2\2\u0088\u008b\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2"+
		"\2\2\u008a\u008c\3\2\2\2\u008b\u0089\3\2\2\2\u008c\u008d\5#\22\2\u008d"+
		"\u008e\3\2\2\2\u008e\u008f\b\21\2\2\u008f\"\3\2\2\2\u0090\u0094\t\4\2"+
		"\2\u0091\u0092\7\17\2\2\u0092\u0094\7\f\2\2\u0093\u0090\3\2\2\2\u0093"+
		"\u0091\3\2\2\2\u0094\u0095\3\2\2\2\u0095\u0096\b\22\2\2\u0096$\3\2\2\2"+
		"\u0097\u009b\7\"\2\2\u0098\u0099\7^\2\2\u0099\u009b\7u\2\2\u009a\u0097"+
		"\3\2\2\2\u009a\u0098\3\2\2\2\u009b\u009c\3\2\2\2\u009c\u009d\b\23\2\2"+
		"\u009d&\3\2\2\2\u009e\u009f\7\13\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a1\b"+
		"\24\2\2\u00a1(\3\2\2\2\17\2IQY]bhj~\u0083\u0089\u0093\u009a\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}