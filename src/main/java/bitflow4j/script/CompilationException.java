package bitflow4j.script;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CompilationException extends RuntimeException {

    private static final int MAX_ERROR_TEXT = 30;

    private List<String> errors;

    public CompilationException(List<String> errors) {
        super();
        this.errors = errors;
    }

    public CompilationException(String... errors) {
        this(Arrays.asList(errors));
    }

    public CompilationException(ParserRuleContext ctx, String error) {
        this(Collections.singletonList(error));
    }

    public CompilationException(RecognitionException e) {
        this(null, formatError(e.getOffendingToken(), e.getCtx(), e.getMessage()));
    }

    public static String formatError(ParserRuleContext ctx, String msg) {
        return formatError(ctx.getStart(), ctx, msg);
    }

    public static String formatError(Token position, RuleContext ctx, String msg) {
        String text = "";
        if (ctx != null)
            text = ctx.getText();
        if (text == null || text.isEmpty()) {
            text = position.getText();
        }
        if (text.length() > MAX_ERROR_TEXT + "...".length()) {
            text = text.substring(0, MAX_ERROR_TEXT) + "...";
        }
        return String.format("Line %s (%s) '%s': %s",
                position.getLine(), position.getCharPositionInLine(), text, msg);
    }

    public List<String> getErrors() {
        return errors;
    }

}
