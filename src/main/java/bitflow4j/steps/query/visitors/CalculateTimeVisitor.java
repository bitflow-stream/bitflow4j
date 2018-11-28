package bitflow4j.steps.query.visitors;

import bitflow4j.steps.query.generated.BitflowQueryBaseVisitor;
import bitflow4j.steps.query.generated.BitflowQueryParser.DaysContext;
import bitflow4j.steps.query.generated.BitflowQueryParser.HoursContext;
import bitflow4j.steps.query.generated.BitflowQueryParser.MinutesContext;
import bitflow4j.steps.query.generated.BitflowQueryParser.SecondsContext;

/**
 * Calculates the time in seconds if Window is part of the query and the mode is
 * time.
 */
public class CalculateTimeVisitor extends BitflowQueryBaseVisitor<Long> {

    @Override
    protected Long aggregateResult(Long aggregate, Long nextResult) {
        if (aggregate == null) {
            return nextResult;
        }
        if (nextResult == null) {
            return aggregate;
        }
        return aggregate + nextResult;
    }

    @Override
    public Long visitDays(DaysContext ctx) {
        return Long.parseLong(ctx.getChild(0).getText()) * 24 * 60 * 60;
    }

    @Override
    public Long visitHours(HoursContext ctx) {
        return Long.parseLong(ctx.getChild(0).getText()) * 60 * 60;
    }

    @Override
    public Long visitMinutes(MinutesContext ctx) {
        return Long.parseLong(ctx.getChild(0).getText()) * 60;
    }

    @Override
    public Long visitSeconds(SecondsContext ctx) {
        return Long.parseLong(ctx.getChild(0).getText());
    }

}
