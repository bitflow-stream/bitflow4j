package bitflow4j.steps.query.visitors;

import bitflow4j.Sample;
import bitflow4j.steps.query.generated.BitflowQueryBaseVisitor;
import bitflow4j.steps.query.generated.BitflowQueryParser.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;

/**
 * Visitor to calculate the header for the output sample.
 */
public class HeaderBuilderVisitor extends BitflowQueryBaseVisitor<String> {

    Sample s;
    String[] newHeader;
    ArrayList<String> tmpHeaderList;
    boolean selectAll;

    public String[] visitWithSample(ParseTree tree, Sample s) {
        this.s = s;
        tmpHeaderList = new ArrayList<>();
        selectAll = false;
        this.visit(tree);

        for (int i = 0; i < newHeader.length; i++) {
            newHeader[i] = Sample.escapeTagString(newHeader[i]);
        }

        return newHeader;
    }

    @Override
    public String visitSelectAll(SelectAllContext ctx) {
        newHeader = s.getHeader().header;
        selectAll = true;
        return null;
    }

    @Override
    protected String aggregateResult(String aggregate, String nextResult) {
        if (aggregate == null) {
            return nextResult;
        }
        if (nextResult == null) {
            return aggregate;
        }

        return aggregate + " " + nextResult;
    }

    @Override
    public String visitAggregateSelections(AggregateSelectionsContext ctx) {
        visitChildren(ctx);

        if (!selectAll) {
            newHeader = new String[tmpHeaderList.size()];
            for (int i = 0; i < tmpHeaderList.size(); i++) {
                newHeader[i] = tmpHeaderList.get(i);
            }
        }
        return "";
    }

    @Override
    public String visitSelectSum(SelectSumContext ctx) {
        return "Sum(" + ctx.getChild(1).getText() + ")";
    }

    @Override
    public String visitSelectMin(SelectMinContext ctx) {
        return "Min(" + ctx.getChild(1).getText() + ")";
    }

    @Override
    public String visitSelectAvg(SelectAvgContext ctx) {
        return "Avg(" + ctx.getChild(1).getText() + ")";
    }

    @Override
    public String visitSelectMax(SelectMaxContext ctx) {
        return "Max(" + ctx.getChild(1).getText() + ")";
    }

    @Override
    public String visitCountNorTIMES(CountNorTIMESContext ctx) {
        return "Count(*)";
    }

    @Override
    public String visitCountTag(CountTagContext ctx) {
        return "Count(" + ctx.getChild(1).getText() + ")";
    }

    @Override
    public String visitSelectMedian(SelectMedianContext ctx) {
        return "Median(" + ctx.getChild(1).getText() + ")";
    }

    @Override
    public String visitSelectElement(SelectElementContext ctx) {
        if (ctx.getChildCount() == 3) {

            tmpHeaderList.add(ctx.getChild(2).getText());
        } else {
            String s = visitChildren(ctx);
            s = s.replace(" ", "");
            tmpHeaderList.add(s);
        }
        return "";
    }

    @Override
    public String visitMathematicalOperation(MathematicalOperationContext ctx) {
        return ctx.getChild(0).getText();
    }

    @Override
    public String visitSelectDefault(SelectDefaultContext ctx) {
        return ctx.getChild(0).getText();
    }

    @Override
    public String visitLeftParen(LeftParenContext ctx) {
        return ctx.getChild(0).getText();
    }

    @Override
    public String visitRightParen(RightParenContext ctx) {
        return ctx.getChild(0).getText();
    }

}
