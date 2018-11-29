package bitflow4j.misc;

import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class TreeFormatterTest extends TestCase {

    public void doTest(Object tree, String... expectedLines) {
        List<String> lines = TreeFormatter.standard.formatLines(tree);
        Assert.assertArrayEquals(expectedLines, lines.toArray(new String[0]));
    }

    public void testSingle() {
        doTest("hello", "hello");
    }

    public void testNull() {
        doTest(null, "(null)");
    }

    public void testMulti() {
        doTest(new TreeFormatter.SimpleNode("parent", "child1", "child2"), "parent", "├─child1", "└─child2");
    }

    public void testDeep() {
        doTest(new TreeFormatter.SimpleNode("parent",
                        "child1",
                        new TreeFormatter.SimpleNode("deepChild",
                                new TreeFormatter.SimpleNode("otherDeepChild",
                                        "h1",
                                        "h2")),
                        "child2",
                        new TreeFormatter.SimpleNode("endChild",
                                new TreeFormatter.SimpleNode("h4", "h5", "h6"),
                                "h3")
                ),
                "parent",
                "├─child1",
                "├─deepChild",
                "│ └─otherDeepChild",
                "│   ├─h1",
                "│   └─h2",
                "├─child2",
                "└─endChild",
                "  ├─h4",
                "  │ ├─h5",
                "  │ └─h6",
                "  └─h3"
        );
    }

}
