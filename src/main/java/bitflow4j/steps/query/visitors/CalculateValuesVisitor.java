package bitflow4j.steps.query.visitors;

import bitflow4j.Sample;
import bitflow4j.steps.query.StreamingQuery;
import bitflow4j.steps.query.exceptions.MissingTagException;
import bitflow4j.steps.query.generated.BitflowQueryBaseVisitor;
import bitflow4j.steps.query.generated.BitflowQueryParser.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Calculate the values that are claimed by the select part of the query.
 */
public class CalculateValuesVisitor extends BitflowQueryBaseVisitor<Double> {

    static String lastOperation;
    Sample currentSample;
    ArrayList<Double> valueList;
    boolean selectAll;
    StreamingQuery streamingQuery;

    public CalculateValuesVisitor(StreamingQuery sq) {
        this.streamingQuery = sq;
    }

    public double[] visitWithSample(ParseTree tree, Sample s) {
        valueList = new ArrayList<>();
        selectAll = false;
        this.currentSample = s;
        tree = tree.getChild(0);
        this.visit(tree);

        if (selectAll) {
            return s.getMetrics();
        } else {
            double[] valueArray = new double[valueList.size()];
            for (int i = 0; i < valueList.size(); i++) {
                valueArray[i] = valueList.get(i);
            }
            return valueArray;
        }

    }

    // do not visit having part of tree
    @Override
    public Double visitHavingFunction(HavingFunctionContext ctx) {
        return null;
    }

    @Override
    public Double visitSelectAll(SelectAllContext ctx) {
        selectAll = true;
        return null;
    }

    @Override
    public Double visitSelectDefault(SelectDefaultContext ctx) {
        boolean childIsDouble = Helpers.isNumeric(ctx.getChild(0).getText());
        if (childIsDouble) {
            return Double.parseDouble(ctx.getChild(0).getText());
        }

        int index = getIndexOfMetricInHeader(currentSample, ctx.getChild(0).getText());
        return currentSample.getMetrics()[index];
    }

    @Override
    public Double visitMathematicalOperation(MathematicalOperationContext ctx) {
        lastOperation = ctx.getChild(0).getText();
        return null;
    }

    @Override
    public Double visitSelectElement(SelectElementContext ctx) {
        double d = visitChildren(ctx);
        valueList.add(d);
        return null;
    }

    @Override
    public Double visitSelectSum(SelectSumContext ctx) {
        double sum = 0;
        for (Sample s : streamingQuery.getSampleList()) {
            int index = getIndexOfMetricInHeader(s, ctx.getChild(1).getText());
            sum = sum + s.getMetrics()[index];
        }
        return sum;
    }

    @Override
    public Double visitCountNorTIMES(CountNorTIMESContext ctx) {
        return (double) streamingQuery.getSampleList().size();
    }

    @Override
    public Double visitCountTag(CountTagContext ctx) {
        ArrayList<String> tagsInQuery = new ArrayList<>();
        for (int i = 1; i < ctx.getChildCount(); i = i + 2) {
            tagsInQuery.add(ctx.getChild(i).getText());
        }

        for (String t : tagsInQuery) {
            if (!currentSample.getTags().containsValue(t)) {
                try {
                    throw new MissingTagException("in Count Function");
                } catch (MissingTagException e) {
                    e.printStackTrace();
                }
            }
        }

        ArrayList<String> distinctTagList = new ArrayList<>();

        for (Sample s : streamingQuery.getSampleList()) {
            StringBuilder value = new StringBuilder();

            for (String t : tagsInQuery) {
                value.append(s.getTag(t));
            }

            boolean found = false;
            for (String tagInList : distinctTagList) {
                if (tagInList.equals(value.toString())) {
                    found = true;
                }
            }

            if (!found && !value.toString().equals("") && !value.toString().equals("null")) {
                distinctTagList.add(value.toString());
            }
        }
        return (double) distinctTagList.size();
    }

    @Override
    public Double visitSelectAvg(SelectAvgContext ctx) {
        double sum = 0;
        for (Sample s : streamingQuery.getSampleList()) {
            int index = getIndexOfMetricInHeader(s, ctx.getChild(1).getText());
            sum = sum + s.getMetrics()[index];
        }
        return sum / streamingQuery.getSampleList().size();
    }

    @Override
    public Double visitSelectMin(SelectMinContext ctx) {
        double min = Double.MAX_VALUE;
        for (Sample s : streamingQuery.getSampleList()) {
            int index = getIndexOfMetricInHeader(s, ctx.getChild(1).getText());
            if (s.getMetrics()[index] < min) {
                min = s.getMetrics()[index];
            }
        }
        return min;
    }

    @Override
    public Double visitSelectMedian(SelectMedianContext ctx) {
        ArrayList<Double> values = new ArrayList<>();
        for (Sample s : streamingQuery.getSampleList()) {
            int index = getIndexOfMetricInHeader(s, ctx.getChild(1).getText());
            values.add(s.getMetrics()[index]);
        }
        Double[] valuesAsArray = values.toArray(new Double[0]);
        Arrays.sort(valuesAsArray);

        int middle = valuesAsArray.length / 2;
        if (valuesAsArray.length % 2 == 1)
            return valuesAsArray[middle];
        else
            return (valuesAsArray[middle - 1] + valuesAsArray[middle]) / 2;
    }

    @Override
    public Double visitSelectMax(SelectMaxContext ctx) {
        double max = Double.MIN_VALUE;
        for (Sample s : streamingQuery.getSampleList()) {
            int index = getIndexOfMetricInHeader(s, ctx.getChild(1).getText());
            if (s.getMetrics()[index] > max) {
                max = s.getMetrics()[index];
            }
        }
        return max;
    }

    @Override
    protected Double aggregateResult(Double aggregate, Double nextResult) {
        if (aggregate == null) {
            return nextResult;
        }
        if (nextResult == null) {
            return aggregate;
        }

        switch (lastOperation) {
            case "+":
                return aggregate + nextResult;
            case "-":
                return aggregate - nextResult;
            case "*":
                return aggregate * nextResult;
            default:
                return aggregate / nextResult;
        }

    }

    private int getIndexOfMetricInHeader(Sample s, String m) {
        for (int i = 0; i < s.getHeader().header.length; i++) {
            if (m.equals(s.getHeader().header[i])) {
                return i;
            }
        }
        return 0;
    }

}
