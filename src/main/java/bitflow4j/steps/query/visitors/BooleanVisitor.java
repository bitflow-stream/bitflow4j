package bitflow4j.steps.query.visitors;

import bitflow4j.Sample;
import bitflow4j.steps.query.ReturnHelperWherePart;
import bitflow4j.steps.query.generated.BitflowQueryBaseVisitor;
import bitflow4j.steps.query.generated.BitflowQueryParser.*;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * Visitor to evaluate the where (and having) part of a tree. Processes one
 * sample per visit and checks if it fulfill the conditions of the query.
 * <p>
 * Uses the class ReturnHelperWherePart to return different objects in different
 * nodes. The variable bool from the object of ReturnHelperWherePart has the
 * information if the conditions are fulfilled or not.
 */
public class BooleanVisitor extends BitflowQueryBaseVisitor<ReturnHelperWherePart> {

    // actual Sample
    Sample currentSample;
    boolean calculateForHaving = false;

    // if having is true this is calculated for the having part of the tree
    // instead of the where part
    public ReturnHelperWherePart visitWithSample(ParseTree tree, Sample s, boolean having) {
        calculateForHaving = having;
        this.currentSample = s;
        if (calculateForHaving) {
            tree = tree.getChild(tree.getChildCount() - 2);
        } else {
            tree = tree.getChild(1);
        }
        return this.visit(tree);
    }

    public ReturnHelperWherePart visitWithSample(ParseTree tree, Sample s) {
        calculateForHaving = false;
        this.currentSample = s;
        tree = tree.getChild(1);
        return this.visit(tree);
    }

    @Override
    public ReturnHelperWherePart visitHavingFunction(HavingFunctionContext ctx) {
        if (calculateForHaving)
            return visitChildren(ctx);
        else
            return null;
    }

    @Override
    public ReturnHelperWherePart visitWhereFunction(WhereFunctionContext ctx) {
        if (!calculateForHaving)
            return super.visitWhereFunction(ctx);
        else
            return null;
    }

    /**
     * See class ReturnHelperWherePart for information about the meaning of
     * types.
     */
    @Override
    protected ReturnHelperWherePart aggregateResult(ReturnHelperWherePart aggregate, ReturnHelperWherePart nextResult) {

        if (aggregate == null) {
            return nextResult;
        }
        if (nextResult == null) {
            return aggregate;
        }

        if (aggregate.getType() == 6 && nextResult.getType() == 5) {
            boolean b = !nextResult.getBool();
            return new ReturnHelperWherePart(5, b);
        }

        if (aggregate.getType() == 6 && nextResult.getType() == 3) {
            aggregate.setNotNode(true);
            aggregate.setValue(nextResult.getValue());
            aggregate.setType(nextResult.getType());
            return aggregate;
        }

        if (aggregate.getType() == 1 && nextResult.getType() == 2) {
            aggregate.setComparator(nextResult.getComparator());
            aggregate.setType(2);
            return aggregate;
        }

        if (aggregate.getType() == 3 && nextResult.getType() == 2) {
            aggregate.setComparator(nextResult.getComparator());
            aggregate.setType(2);
            return aggregate;
        }

        if (aggregate.getType() == 5 && nextResult.getType() == 4) {
            aggregate.setType(4);
            aggregate.setBinary(nextResult.getBinary());
            return aggregate;
        }

        if (aggregate.getType() == 4 && nextResult.getType() == 5) {
            if (aggregate.getBinary().equals("AND")) {
                if (aggregate.getBool() && nextResult.getBool()) {
                    return new ReturnHelperWherePart(5, true);
                } else {
                    return new ReturnHelperWherePart(5, false);
                }

            } else {
                if (aggregate.getBool() || nextResult.getBool()) {
                    return new ReturnHelperWherePart(5, true);
                } else {
                    return new ReturnHelperWherePart(5, false);
                }
            }
        }

        if (aggregate.getType() == 2 && nextResult.getType() == 3) {

            if (aggregate.getComparator().equals("=")) {
                if (aggregate.getValue() == nextResult.getValue()) {
                    if (aggregate.isNotNode()) {
                        return new ReturnHelperWherePart(5, false);
                    } else {
                        return new ReturnHelperWherePart(5, true);
                    }
                } else {
                    if (aggregate.isNotNode()) {
                        return new ReturnHelperWherePart(5, true);
                    } else {
                        return new ReturnHelperWherePart(5, false);
                    }
                }
            }
            if (aggregate.getComparator().equals(">")) {
                if (aggregate.getValue() > nextResult.getValue()) {
                    if (aggregate.isNotNode()) {
                        return new ReturnHelperWherePart(5, false);
                    } else {
                        return new ReturnHelperWherePart(5, true);
                    }
                } else {
                    if (aggregate.isNotNode()) {
                        return new ReturnHelperWherePart(5, true);
                    } else {
                        return new ReturnHelperWherePart(5, false);
                    }
                }
            }
            if (aggregate.getComparator().equals(">=")) {
                if (aggregate.getValue() >= nextResult.getValue()) {
                    if (aggregate.isNotNode()) {
                        return new ReturnHelperWherePart(5, false);
                    } else {
                        return new ReturnHelperWherePart(5, true);
                    }
                } else {
                    if (aggregate.isNotNode()) {
                        return new ReturnHelperWherePart(5, true);
                    } else {
                        return new ReturnHelperWherePart(5, false);
                    }
                }
            }
            if (aggregate.getComparator().equals("<")) {
                if (aggregate.getValue() < nextResult.getValue()) {
                    if (aggregate.isNotNode()) {
                        return new ReturnHelperWherePart(5, false);
                    } else {
                        return new ReturnHelperWherePart(5, true);
                    }
                } else {
                    if (aggregate.isNotNode()) {
                        return new ReturnHelperWherePart(5, true);
                    } else {
                        return new ReturnHelperWherePart(5, false);
                    }
                }
            }
            if (aggregate.getComparator().equals("<=")) {
                if (aggregate.getValue() <= nextResult.getValue()) {
                    if (aggregate.isNotNode()) {
                        return new ReturnHelperWherePart(5, false);
                    } else {
                        return new ReturnHelperWherePart(5, true);
                    }
                } else {
                    if (aggregate.isNotNode()) {
                        return new ReturnHelperWherePart(5, true);
                    } else {
                        return new ReturnHelperWherePart(5, false);
                    }
                }
            }
        }
        return aggregate;
    }

    @Override
    public ReturnHelperWherePart visitBoolToken(BoolTokenContext ctx) {
        if (ctx.getChild(0).getText().equals("TRUE")) {
            return new ReturnHelperWherePart(5, true);
        }
        return new ReturnHelperWherePart(5, false);
    }

    @Override
    public ReturnHelperWherePart visitNotnode(NotnodeContext ctx) {
        return new ReturnHelperWherePart(6, true);
    }

    @Override
    public ReturnHelperWherePart visitComparator(ComparatorContext ctx) {
        return new ReturnHelperWherePart(2, ctx.getChild(0).getText());
    }

    @Override
    public ReturnHelperWherePart visitBinary(BinaryContext ctx) {
        return new ReturnHelperWherePart(4, ctx.getChild(0).getText());
    }

    @Override
    public ReturnHelperWherePart visitEndnode(EndnodeContext ctx) {

        String s = ctx.getChild(0).getText();
        boolean sIsDouble = Helpers.isNumeric(s);

        if (sIsDouble) {
            return new ReturnHelperWherePart(3, Double.parseDouble(ctx.getChild(0).getText()));
        } else {
            int index = getIndexOfMetricInHeader(ctx.getChild(0).getText());
            double v = currentSample.getMetrics()[index];
            return new ReturnHelperWherePart(3, v);
        }
    }

    @Override
    public ReturnHelperWherePart visitInexpressiontag(InexpressiontagContext ctx) {

        String tagValue = currentSample.getTag(ctx.getChild(1).getText());
        if (tagValue == null)
            return new ReturnHelperWherePart(5, false);
        for (int i = 3; i < ctx.getChildCount(); i++) {
            if (tagValue.equals(ctx.getChild(i).getText())) {
                return new ReturnHelperWherePart(5, true);
            }
        }
        return new ReturnHelperWherePart(5, false);
    }

    @Override
    public ReturnHelperWherePart visitInexpressionmetric(InexpressionmetricContext ctx) {
        double d = currentSample.getMetrics()[getIndexOfMetricInHeader(ctx.getChild(0).getText())];
        for (int i = 2; i < ctx.getChildCount(); i++) {
            if (Helpers.isNumeric(ctx.getChild(i).getText())) {
                if (d == Double.parseDouble(ctx.getChild(i).getText())) {
                    return new ReturnHelperWherePart(5, true);
                }
            }
        }
        return new ReturnHelperWherePart(5, false);
    }

    @Override
    public ReturnHelperWherePart visitHastag(HastagContext ctx) {

        if (!currentSample.hasTag(ctx.getChild(1).getText()))
            return new ReturnHelperWherePart(5, false);


        String tagValue = currentSample.getTag(ctx.getChild(1).getText());
        if (tagValue.equals(ctx.getChild(3).getText()))
            return new ReturnHelperWherePart(5, true);

        return new ReturnHelperWherePart(5, false);
    }

    private int getIndexOfMetricInHeader(String m) {
        for (int i = 0; i < currentSample.getHeader().header.length; i++) {
            if (m.equals(currentSample.getHeader().header[i])) {
                return i;
            }
        }
        return 0;
    }

}
