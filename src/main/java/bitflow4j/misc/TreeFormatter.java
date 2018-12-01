package bitflow4j.misc;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class TreeFormatter {

    public interface FormattedNode {
        Collection<Object> formattedChildren();
    }

    public static final TreeFormatter standard = new TreeFormatter("│ ", "├─", "  ", "└─");

    private final String outer;
    private final String inner;
    private final String filler;
    private final String corner;

    public TreeFormatter(String outer, String inner, String filler, String corner) {
        this.outer = outer;
        this.inner = inner;
        this.filler = filler;
        this.corner = corner;
    }

    public String format(Object tree) {
        List<String> lines = formatLines(tree);
        return String.join("\n", lines);
    }

    public List<String> formatLines(Object tree) {
        List<String> lines = new ArrayList<>();
        format(tree, lines);
        return lines;
    }

    public void format(Object tree, List<String> lines) {
        format(tree, "", "", lines);
    }

    @SuppressWarnings("unchecked")
    private void format(Object tree, String headerPrefix, String childPrefix, List<String> lines) {
        String newLine = tree == null ? "(null)" : tree.toString();
        lines.add(headerPrefix + newLine);

        Collection children = null;
        if (tree instanceof FormattedNode) {
            children = ((FormattedNode) tree).formattedChildren();
        } else if (tree instanceof Collection) {
            children = (Collection) tree;
        }

        if (children != null) {
            if (children.isEmpty()) {
                children.add("empty");
            }
            Iterator iter = children.iterator();
            while (iter.hasNext()) {
                Object child = iter.next();
                String nextHeader, nextChild;
                if (!iter.hasNext()) {
                    // Handling last child element -> insert corner connector
                    nextHeader = corner;
                    nextChild = filler;
                } else {
                    nextHeader = inner;
                    nextChild = outer;
                }
                format(child, childPrefix + nextHeader, childPrefix + nextChild, lines);
            }
        }
    }

    public static class SimpleNode implements FormattedNode {

        private final String name;
        private final Collection<Object> children;

        public SimpleNode(String name, Collection<Object> children) {
            this.name = name;
            this.children = children;
        }

        public SimpleNode(String name, Object... children) {
            this.name = name;
            this.children = Lists.newArrayList(children);
        }

        public String toString() {
            return name;
        }

        @Override
        public Collection<Object> formattedChildren() {
            return children;
        }
    }

}
