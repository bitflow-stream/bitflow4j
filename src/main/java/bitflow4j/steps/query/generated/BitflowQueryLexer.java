// Generated from BitflowQuery.g4 by ANTLR 4.7.1
package bitflow4j.steps.query.generated;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class BitflowQueryLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, SELECTKEYWORD=16, 
		WHEREKEYWORD=17, WINDOWKEYWORD=18, GROUPBYKEYWORD=19, HAVINGKEYWORD=20, 
		ALLWORD=21, ASWORD=22, SUMKEYWORD=23, MINKEYWORD=24, MAXKEYWORD=25, AVGKEYWORD=26, 
		MEDIANWORD=27, COUNTWORD=28, TAGWORD=29, FILEWORD=30, INWORD=31, PLUS=32, 
		MINUS=33, TIMES=34, DIVIDED=35, LPAREN=36, RPAREN=37, AND=38, OR=39, NOT=40, 
		TRUE=41, FALSE=42, GT=43, GE=44, LT=45, LE=46, EQ=47, NUMBER=48, STRING=49, 
		DECITIMES=50, IDENTIFIER=51, WS=52;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "SELECTKEYWORD", 
		"WHEREKEYWORD", "WINDOWKEYWORD", "GROUPBYKEYWORD", "HAVINGKEYWORD", "ALLWORD", 
		"ASWORD", "SUMKEYWORD", "MINKEYWORD", "MAXKEYWORD", "AVGKEYWORD", "MEDIANWORD", 
		"COUNTWORD", "TAGWORD", "FILEWORD", "INWORD", "PLUS", "MINUS", "TIMES", 
		"DIVIDED", "LPAREN", "RPAREN", "AND", "OR", "NOT", "TRUE", "FALSE", "GT", 
		"GE", "LT", "LE", "EQ", "NUMBER", "STRING", "DECITIMES", "IDENTIFIER", 
		"WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "', '", "','", "'*)'", "'d'", "'D'", "'h'", "'H'", "'m'", "'M'", 
		"'s'", "'S'", "'{'", "'}'", "') = \"'", "'\"'", null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		"'+'", "'-'", "'*'", "'/'", "'('", "')'", null, null, null, null, null, 
		"'>'", "'>='", "'<'", "'<='", "'='"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, "SELECTKEYWORD", "WHEREKEYWORD", "WINDOWKEYWORD", 
		"GROUPBYKEYWORD", "HAVINGKEYWORD", "ALLWORD", "ASWORD", "SUMKEYWORD", 
		"MINKEYWORD", "MAXKEYWORD", "AVGKEYWORD", "MEDIANWORD", "COUNTWORD", "TAGWORD", 
		"FILEWORD", "INWORD", "PLUS", "MINUS", "TIMES", "DIVIDED", "LPAREN", "RPAREN", 
		"AND", "OR", "NOT", "TRUE", "FALSE", "GT", "GE", "LT", "LE", "EQ", "NUMBER", 
		"STRING", "DECITIMES", "IDENTIFIER", "WS"
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


	public BitflowQueryLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "BitflowQuery.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\66\u014d\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3"+
		"\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3"+
		"\17\3\17\3\17\3\17\3\17\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3"+
		"\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3"+
		"\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3"+
		"\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3"+
		"\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3"+
		"\31\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3"+
		"\34\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3"+
		"\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3 \3 \3 \3 \3 \3!\3!\3\"\3\"\3"+
		"#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3\'\3\'\3(\3(\3(\3)\3)\3)\3)\3*\3*\3*\3"+
		"*\3*\3+\3+\3+\3+\3+\3+\3,\3,\3-\3-\3-\3.\3.\3/\3/\3/\3\60\3\60\3\61\6"+
		"\61\u0127\n\61\r\61\16\61\u0128\3\62\6\62\u012c\n\62\r\62\16\62\u012d"+
		"\3\63\5\63\u0131\n\63\3\63\6\63\u0134\n\63\r\63\16\63\u0135\3\63\3\63"+
		"\6\63\u013a\n\63\r\63\16\63\u013b\5\63\u013e\n\63\3\64\3\64\7\64\u0142"+
		"\n\64\f\64\16\64\u0145\13\64\3\65\6\65\u0148\n\65\r\65\16\65\u0149\3\65"+
		"\3\65\2\2\66\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16"+
		"\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34"+
		"\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g"+
		"\65i\66\3\2\34\4\2UUuu\4\2GGgg\4\2NNnn\4\2EEee\4\2VVvv\4\2YYyy\4\2JJj"+
		"j\4\2TTtt\4\2KKkk\4\2PPpp\4\2FFff\4\2QQqq\4\2IIii\4\2WWww\4\2RRrr\4\2"+
		"DDdd\4\2[[{{\4\2CCcc\4\2XXxx\4\2OOoo\4\2ZZzz\4\2HHhh\3\2\62;\6\2\62;C"+
		"\\aac|\4\2C\\c|\5\2\13\f\16\17\"\"\2\u0154\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2"+
		"\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2"+
		"\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2"+
		"\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2"+
		"\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M"+
		"\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2"+
		"\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2"+
		"\2g\3\2\2\2\2i\3\2\2\2\3k\3\2\2\2\5n\3\2\2\2\7p\3\2\2\2\ts\3\2\2\2\13"+
		"u\3\2\2\2\rw\3\2\2\2\17y\3\2\2\2\21{\3\2\2\2\23}\3\2\2\2\25\177\3\2\2"+
		"\2\27\u0081\3\2\2\2\31\u0083\3\2\2\2\33\u0085\3\2\2\2\35\u0087\3\2\2\2"+
		"\37\u008d\3\2\2\2!\u008f\3\2\2\2#\u0097\3\2\2\2%\u009f\3\2\2\2\'\u00a8"+
		"\3\2\2\2)\u00b3\3\2\2\2+\u00bc\3\2\2\2-\u00c0\3\2\2\2/\u00c5\3\2\2\2\61"+
		"\u00ca\3\2\2\2\63\u00cf\3\2\2\2\65\u00d4\3\2\2\2\67\u00d9\3\2\2\29\u00e1"+
		"\3\2\2\2;\u00e8\3\2\2\2=\u00ed\3\2\2\2?\u00f2\3\2\2\2A\u00f7\3\2\2\2C"+
		"\u00f9\3\2\2\2E\u00fb\3\2\2\2G\u00fd\3\2\2\2I\u00ff\3\2\2\2K\u0101\3\2"+
		"\2\2M\u0103\3\2\2\2O\u0107\3\2\2\2Q\u010a\3\2\2\2S\u010e\3\2\2\2U\u0113"+
		"\3\2\2\2W\u0119\3\2\2\2Y\u011b\3\2\2\2[\u011e\3\2\2\2]\u0120\3\2\2\2_"+
		"\u0123\3\2\2\2a\u0126\3\2\2\2c\u012b\3\2\2\2e\u0130\3\2\2\2g\u013f\3\2"+
		"\2\2i\u0147\3\2\2\2kl\7.\2\2lm\7\"\2\2m\4\3\2\2\2no\7.\2\2o\6\3\2\2\2"+
		"pq\7,\2\2qr\7+\2\2r\b\3\2\2\2st\7f\2\2t\n\3\2\2\2uv\7F\2\2v\f\3\2\2\2"+
		"wx\7j\2\2x\16\3\2\2\2yz\7J\2\2z\20\3\2\2\2{|\7o\2\2|\22\3\2\2\2}~\7O\2"+
		"\2~\24\3\2\2\2\177\u0080\7u\2\2\u0080\26\3\2\2\2\u0081\u0082\7U\2\2\u0082"+
		"\30\3\2\2\2\u0083\u0084\7}\2\2\u0084\32\3\2\2\2\u0085\u0086\7\177\2\2"+
		"\u0086\34\3\2\2\2\u0087\u0088\7+\2\2\u0088\u0089\7\"\2\2\u0089\u008a\7"+
		"?\2\2\u008a\u008b\7\"\2\2\u008b\u008c\7$\2\2\u008c\36\3\2\2\2\u008d\u008e"+
		"\7$\2\2\u008e \3\2\2\2\u008f\u0090\t\2\2\2\u0090\u0091\t\3\2\2\u0091\u0092"+
		"\t\4\2\2\u0092\u0093\t\3\2\2\u0093\u0094\t\5\2\2\u0094\u0095\t\6\2\2\u0095"+
		"\u0096\7\"\2\2\u0096\"\3\2\2\2\u0097\u0098\7\"\2\2\u0098\u0099\t\7\2\2"+
		"\u0099\u009a\t\b\2\2\u009a\u009b\t\3\2\2\u009b\u009c\t\t\2\2\u009c\u009d"+
		"\t\3\2\2\u009d\u009e\7\"\2\2\u009e$\3\2\2\2\u009f\u00a0\7\"\2\2\u00a0"+
		"\u00a1\t\7\2\2\u00a1\u00a2\t\n\2\2\u00a2\u00a3\t\13\2\2\u00a3\u00a4\t"+
		"\f\2\2\u00a4\u00a5\t\r\2\2\u00a5\u00a6\t\7\2\2\u00a6\u00a7\7\"\2\2\u00a7"+
		"&\3\2\2\2\u00a8\u00a9\7\"\2\2\u00a9\u00aa\t\16\2\2\u00aa\u00ab\t\t\2\2"+
		"\u00ab\u00ac\t\r\2\2\u00ac\u00ad\t\17\2\2\u00ad\u00ae\t\20\2\2\u00ae\u00af"+
		"\7\"\2\2\u00af\u00b0\t\21\2\2\u00b0\u00b1\t\22\2\2\u00b1\u00b2\7\"\2\2"+
		"\u00b2(\3\2\2\2\u00b3\u00b4\7\"\2\2\u00b4\u00b5\t\b\2\2\u00b5\u00b6\t"+
		"\23\2\2\u00b6\u00b7\t\24\2\2\u00b7\u00b8\t\n\2\2\u00b8\u00b9\t\13\2\2"+
		"\u00b9\u00ba\t\16\2\2\u00ba\u00bb\7\"\2\2\u00bb*\3\2\2\2\u00bc\u00bd\t"+
		"\23\2\2\u00bd\u00be\t\4\2\2\u00be\u00bf\t\4\2\2\u00bf,\3\2\2\2\u00c0\u00c1"+
		"\7\"\2\2\u00c1\u00c2\t\23\2\2\u00c2\u00c3\t\2\2\2\u00c3\u00c4\7\"\2\2"+
		"\u00c4.\3\2\2\2\u00c5\u00c6\t\2\2\2\u00c6\u00c7\t\17\2\2\u00c7\u00c8\t"+
		"\25\2\2\u00c8\u00c9\7*\2\2\u00c9\60\3\2\2\2\u00ca\u00cb\t\25\2\2\u00cb"+
		"\u00cc\t\n\2\2\u00cc\u00cd\t\13\2\2\u00cd\u00ce\7*\2\2\u00ce\62\3\2\2"+
		"\2\u00cf\u00d0\t\25\2\2\u00d0\u00d1\t\23\2\2\u00d1\u00d2\t\26\2\2\u00d2"+
		"\u00d3\7*\2\2\u00d3\64\3\2\2\2\u00d4\u00d5\t\23\2\2\u00d5\u00d6\t\24\2"+
		"\2\u00d6\u00d7\t\16\2\2\u00d7\u00d8\7*\2\2\u00d8\66\3\2\2\2\u00d9\u00da"+
		"\t\25\2\2\u00da\u00db\t\3\2\2\u00db\u00dc\t\f\2\2\u00dc\u00dd\t\n\2\2"+
		"\u00dd\u00de\t\23\2\2\u00de\u00df\t\13\2\2\u00df\u00e0\7*\2\2\u00e08\3"+
		"\2\2\2\u00e1\u00e2\t\5\2\2\u00e2\u00e3\t\r\2\2\u00e3\u00e4\t\17\2\2\u00e4"+
		"\u00e5\t\13\2\2\u00e5\u00e6\t\6\2\2\u00e6\u00e7\7*\2\2\u00e7:\3\2\2\2"+
		"\u00e8\u00e9\t\6\2\2\u00e9\u00ea\t\23\2\2\u00ea\u00eb\t\16\2\2\u00eb\u00ec"+
		"\7*\2\2\u00ec<\3\2\2\2\u00ed\u00ee\t\27\2\2\u00ee\u00ef\t\n\2\2\u00ef"+
		"\u00f0\t\4\2\2\u00f0\u00f1\t\3\2\2\u00f1>\3\2\2\2\u00f2\u00f3\7\"\2\2"+
		"\u00f3\u00f4\t\n\2\2\u00f4\u00f5\t\13\2\2\u00f5\u00f6\7\"\2\2\u00f6@\3"+
		"\2\2\2\u00f7\u00f8\7-\2\2\u00f8B\3\2\2\2\u00f9\u00fa\7/\2\2\u00faD\3\2"+
		"\2\2\u00fb\u00fc\7,\2\2\u00fcF\3\2\2\2\u00fd\u00fe\7\61\2\2\u00feH\3\2"+
		"\2\2\u00ff\u0100\7*\2\2\u0100J\3\2\2\2\u0101\u0102\7+\2\2\u0102L\3\2\2"+
		"\2\u0103\u0104\t\23\2\2\u0104\u0105\t\13\2\2\u0105\u0106\t\f\2\2\u0106"+
		"N\3\2\2\2\u0107\u0108\t\r\2\2\u0108\u0109\t\t\2\2\u0109P\3\2\2\2\u010a"+
		"\u010b\t\13\2\2\u010b\u010c\t\r\2\2\u010c\u010d\t\6\2\2\u010dR\3\2\2\2"+
		"\u010e\u010f\t\6\2\2\u010f\u0110\t\t\2\2\u0110\u0111\t\17\2\2\u0111\u0112"+
		"\t\3\2\2\u0112T\3\2\2\2\u0113\u0114\t\27\2\2\u0114\u0115\t\23\2\2\u0115"+
		"\u0116\t\4\2\2\u0116\u0117\t\2\2\2\u0117\u0118\t\3\2\2\u0118V\3\2\2\2"+
		"\u0119\u011a\7@\2\2\u011aX\3\2\2\2\u011b\u011c\7@\2\2\u011c\u011d\7?\2"+
		"\2\u011dZ\3\2\2\2\u011e\u011f\7>\2\2\u011f\\\3\2\2\2\u0120\u0121\7>\2"+
		"\2\u0121\u0122\7?\2\2\u0122^\3\2\2\2\u0123\u0124\7?\2\2\u0124`\3\2\2\2"+
		"\u0125\u0127\t\30\2\2\u0126\u0125\3\2\2\2\u0127\u0128\3\2\2\2\u0128\u0126"+
		"\3\2\2\2\u0128\u0129\3\2\2\2\u0129b\3\2\2\2\u012a\u012c\t\31\2\2\u012b"+
		"\u012a\3\2\2\2\u012c\u012d\3\2\2\2\u012d\u012b\3\2\2\2\u012d\u012e\3\2"+
		"\2\2\u012ed\3\2\2\2\u012f\u0131\7/\2\2\u0130\u012f\3\2\2\2\u0130\u0131"+
		"\3\2\2\2\u0131\u0133\3\2\2\2\u0132\u0134\t\30\2\2\u0133\u0132\3\2\2\2"+
		"\u0134\u0135\3\2\2\2\u0135\u0133\3\2\2\2\u0135\u0136\3\2\2\2\u0136\u013d"+
		"\3\2\2\2\u0137\u0139\7\60\2\2\u0138\u013a\t\30\2\2\u0139\u0138\3\2\2\2"+
		"\u013a\u013b\3\2\2\2\u013b\u0139\3\2\2\2\u013b\u013c\3\2\2\2\u013c\u013e"+
		"\3\2\2\2\u013d\u0137\3\2\2\2\u013d\u013e\3\2\2\2\u013ef\3\2\2\2\u013f"+
		"\u0143\t\32\2\2\u0140\u0142\t\31\2\2\u0141\u0140\3\2\2\2\u0142\u0145\3"+
		"\2\2\2\u0143\u0141\3\2\2\2\u0143\u0144\3\2\2\2\u0144h\3\2\2\2\u0145\u0143"+
		"\3\2\2\2\u0146\u0148\t\33\2\2\u0147\u0146\3\2\2\2\u0148\u0149\3\2\2\2"+
		"\u0149\u0147\3\2\2\2\u0149\u014a\3\2\2\2\u014a\u014b\3\2\2\2\u014b\u014c"+
		"\b\65\2\2\u014cj\3\2\2\2\13\2\u0128\u012d\u0130\u0135\u013b\u013d\u0143"+
		"\u0149\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}