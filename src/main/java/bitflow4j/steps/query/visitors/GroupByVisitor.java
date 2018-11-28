package bitflow4j.steps.query.visitors;

import bitflow4j.steps.query.generated.BitflowQueryBaseVisitor;
import bitflow4j.steps.query.generated.BitflowQueryParser.TagContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;

/**
 * Visits the tree and returns the list of tags for group by.
 */
public class GroupByVisitor extends BitflowQueryBaseVisitor<ArrayList<String>> {

    ArrayList<String> tags;

    public ArrayList<String> visitFunction(ParseTree tree) {
        tags = new ArrayList<>();
        this.visit(tree);
        return tags;
    }

    @Override
    public ArrayList<String> visitTag(TagContext ctx) {
        tags.add(ctx.getChild(0).getText());
        return null;
    }
}
