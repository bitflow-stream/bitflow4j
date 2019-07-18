package bitflow4j.misc;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TreeFormatterTest {

    public void doTest(Object tree, String... expectedLines) {
        List<String> lines = TreeFormatter.standard.formatLines(tree);
        assertArrayEquals(expectedLines, lines.toArray(new String[0]));
    }

    @Test
    public void testSingle() {
        doTest("hello", "hello");
    }

    @Test
    public void testNull() {
        doTest(null, "(null)");
    }

    @Test
    public void testMulti() {
        doTest(new TreeFormatter.SimpleNode("parent", "child1", "child2"), "parent", "├─child1", "└─child2");
    }

    @Test
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
