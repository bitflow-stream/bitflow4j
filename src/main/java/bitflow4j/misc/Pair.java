package bitflow4j.misc;

import java.io.Serializable;

public class Pair<A, B> implements Serializable {

    private A left;
    private B right;

    public Pair(A left, B right) {
        this.left = left;
        this.right = right;
    }

    public A getLeft() {
        return left;
    }

    public void setLeft(A left) {
        this.left = left;
    }

    public B getRight() {
        return right;
    }

    public void setRight(B right) {
        this.right = right;
    }

    @Override
    public int hashCode() {
        return 17 * (left == null ? 43 : left.hashCode()) + (right == null ? 59 : right.hashCode());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Pair)) return false;
        Pair otherPair = (Pair) other;
        return safeEquals(left, otherPair.left) && safeEquals(right, otherPair.right);
    }

    private static boolean safeEquals(Object o1, Object o2) {
        if (o1 == null != (o2 == null)) return false;
        if (o1 == null) return true;
        return o1.equals(o2);
    }

}
