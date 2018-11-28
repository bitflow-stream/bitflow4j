package bitflow4j.steps.query.visitors;

import bitflow4j.steps.query.generated.BitflowQueryBaseVisitor;
import bitflow4j.steps.query.generated.BitflowQueryParser.EndnodeContext;
import bitflow4j.steps.query.generated.BitflowQueryParser.HavingFunctionContext;
import bitflow4j.steps.query.generated.BitflowQueryParser.SelectDefaultContext;

import java.util.ArrayList;

/**
 * Calculate the needed metrics from a query string and returns these as a list.
 */
public class CalculateNeededMetricsVisitor extends BitflowQueryBaseVisitor<ArrayList<String>> {

    // has all metrics after visiting the tree
    ArrayList<String> neededMetrics;

    public CalculateNeededMetricsVisitor() {
        neededMetrics = new ArrayList<>();
    }

    // visit not the having part of the tree
    @Override
    public ArrayList<String> visitHavingFunction(HavingFunctionContext ctx) {
        return null;
    }

    @Override
    public ArrayList<String> visitSelectDefault(SelectDefaultContext ctx) {
        String metric = ctx.getChild(0).getText();

        boolean metricIsNumeric = Helpers.isNumeric(metric);
        if (!neededMetrics.contains(metric) && !metricIsNumeric) {
            neededMetrics.add(metric);
        }
        return null;
    }

    @Override
    public ArrayList<String> visitEndnode(EndnodeContext ctx) {
        String metric = ctx.getChild(0).getText();
        if (!Helpers.isNumeric(metric)) {
            if (!neededMetrics.contains(metric))
                neededMetrics.add(metric);
        }
        return null;
    }

    @Override
    protected ArrayList<String> defaultResult() {
        return neededMetrics;
    }

}
