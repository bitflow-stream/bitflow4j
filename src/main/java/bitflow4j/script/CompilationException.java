package bitflow4j.script;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;

public class CompilationException extends RuntimeException {

    private static final int MAX_ERROR_TEXT = 50;

    /**
     * This constructor is for compiler errors that cannot be associated with a name entity like a processing step.
     * The error message will be formatted with the given parser context.
     */
    public CompilationException(ParserRuleContext ctx, String error) {
        super(formatError(ctx.getStart(), null, ctx, error), null);
    }

    /**
     * These errors have no associated named script entity, but a exception that can be logged.
     */
    public CompilationException(ParserRuleContext ctx, Throwable cause) {
        super(formatError(ctx.getStart(), null, ctx, cause.getMessage()), cause);
    }

    /**
     * This constructor additionally provides the name of a pipeline step that caused the error.
     */
    public CompilationException(ParserRuleContext ctx, String stepName, String error) {
        super(formatError(ctx.getStart(), stepName, ctx, error), null);
    }

    /**
     * This constructor additionally provides the name of a pipeline step that caused the error.
     */
    public CompilationException(ParserRuleContext ctx, String stepName, Throwable cause) {
        super(formatError(ctx.getStart(), stepName, ctx, cause.getMessage()), cause);
    }

    /**
     * This shortcut constructor unpacks the information from the given RecognitionException and formats it.
     * The exception itself is not logged as a cause, because all information it contains is formatted to a message.
     */
    public CompilationException(RecognitionException e) {
        super(formatError(e.getOffendingToken(), null, e.getCtx(), e.getMessage()), null);
    }

    /**
     * This constructor is for unexpected errors that have no further context
     */
    public CompilationException(Throwable cause) {
        super(String.format("Unknown %s during Bitflow script compilation", cause.getClass().getName()), cause);
    }

    private static String formatError(Token position, String stepName, RuleContext ctx, String msg) {
        String text = "";
        if (stepName != null) {
            text = stepName;
        } else {
            if (ctx != null) {
                text = ctx.getText();
            }
            if (text == null || text.isEmpty()) {
                text = position.getText();
            }
            if (text.length() > MAX_ERROR_TEXT + "...".length()) {
                text = text.substring(0, MAX_ERROR_TEXT) + "...";
            }
        }
        return String.format("Line %s (%s) '%s': %s",
                position.getLine(), position.getCharPositionInLine(), text, msg);
    }

}
