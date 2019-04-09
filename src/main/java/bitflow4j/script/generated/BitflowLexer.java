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
		OPEN_HINTS=9, CLOSE_HINTS=10, WINDOW=11, STRING=12, IDENTIFIER=13, COMMENT=14, 
		NEWLINE=15, WHITESPACE=16, TAB=17;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"OPEN", "CLOSE", "EOP", "NEXT", "OPEN_PARAMS", "CLOSE_PARAMS", "EQ", "SEP", 
		"OPEN_HINTS", "CLOSE_HINTS", "WINDOW", "STRING", "IDENTIFIER", "COMMENT", 
		"NEWLINE", "WHITESPACE", "TAB"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'{'", "'}'", "';'", "'->'", "'('", "')'", "'='", "','", "'['", 
		"']'", "'window'", null, null, null, null, null, "'\t'"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\23}\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3"+
		"\n\3\n\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\7\rD\n\r\f\r\16\r"+
		"G\13\r\3\r\3\r\3\r\7\rL\n\r\f\r\16\rO\13\r\3\r\3\r\3\r\7\rT\n\r\f\r\16"+
		"\rW\13\r\3\r\5\rZ\n\r\3\16\6\16]\n\16\r\16\16\16^\3\17\3\17\7\17c\n\17"+
		"\f\17\16\17f\13\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\5\20o\n\20\3\20"+
		"\3\20\3\21\3\21\3\21\5\21v\n\21\3\21\3\21\3\22\3\22\3\22\3\22\5EMU\2\23"+
		"\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20"+
		"\37\21!\22#\23\3\2\4\n\2\'(,-/<AAC\\^^aac|\4\2\f\f\17\17\2\u0085\2\3\3"+
		"\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2"+
		"\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3"+
		"\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\3"+
		"%\3\2\2\2\5\'\3\2\2\2\7)\3\2\2\2\t+\3\2\2\2\13.\3\2\2\2\r\60\3\2\2\2\17"+
		"\62\3\2\2\2\21\64\3\2\2\2\23\66\3\2\2\2\258\3\2\2\2\27:\3\2\2\2\31Y\3"+
		"\2\2\2\33\\\3\2\2\2\35`\3\2\2\2\37n\3\2\2\2!u\3\2\2\2#y\3\2\2\2%&\7}\2"+
		"\2&\4\3\2\2\2\'(\7\177\2\2(\6\3\2\2\2)*\7=\2\2*\b\3\2\2\2+,\7/\2\2,-\7"+
		"@\2\2-\n\3\2\2\2./\7*\2\2/\f\3\2\2\2\60\61\7+\2\2\61\16\3\2\2\2\62\63"+
		"\7?\2\2\63\20\3\2\2\2\64\65\7.\2\2\65\22\3\2\2\2\66\67\7]\2\2\67\24\3"+
		"\2\2\289\7_\2\29\26\3\2\2\2:;\7y\2\2;<\7k\2\2<=\7p\2\2=>\7f\2\2>?\7q\2"+
		"\2?@\7y\2\2@\30\3\2\2\2AE\7$\2\2BD\13\2\2\2CB\3\2\2\2DG\3\2\2\2EF\3\2"+
		"\2\2EC\3\2\2\2FH\3\2\2\2GE\3\2\2\2HZ\7$\2\2IM\7)\2\2JL\13\2\2\2KJ\3\2"+
		"\2\2LO\3\2\2\2MN\3\2\2\2MK\3\2\2\2NP\3\2\2\2OM\3\2\2\2PZ\7)\2\2QU\7b\2"+
		"\2RT\13\2\2\2SR\3\2\2\2TW\3\2\2\2UV\3\2\2\2US\3\2\2\2VX\3\2\2\2WU\3\2"+
		"\2\2XZ\7b\2\2YA\3\2\2\2YI\3\2\2\2YQ\3\2\2\2Z\32\3\2\2\2[]\t\2\2\2\\[\3"+
		"\2\2\2]^\3\2\2\2^\\\3\2\2\2^_\3\2\2\2_\34\3\2\2\2`d\7%\2\2ac\n\3\2\2b"+
		"a\3\2\2\2cf\3\2\2\2db\3\2\2\2de\3\2\2\2eg\3\2\2\2fd\3\2\2\2gh\5\37\20"+
		"\2hi\3\2\2\2ij\b\17\2\2j\36\3\2\2\2ko\t\3\2\2lm\7\17\2\2mo\7\f\2\2nk\3"+
		"\2\2\2nl\3\2\2\2op\3\2\2\2pq\b\20\2\2q \3\2\2\2rv\7\"\2\2st\7^\2\2tv\7"+
		"u\2\2ur\3\2\2\2us\3\2\2\2vw\3\2\2\2wx\b\21\2\2x\"\3\2\2\2yz\7\13\2\2z"+
		"{\3\2\2\2{|\b\22\2\2|$\3\2\2\2\13\2EMUY^dnu\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}