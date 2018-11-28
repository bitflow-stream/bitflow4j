package bitflow4j.steps.query;

/**
 * This class is needed for returning different Objecttypes when parsing the
 * where and having part of a tree. For example sometimes it is needed to return
 * a boolean and sometimes a double or a string. In addition in some cases its
 * necessary to return more than one object, like a value and a comparator.
 */
public class ReturnHelperWherePart {

    private int type;

    private String comparator; // type 2 -> comparison operator
    private double value; // type 3 -> value
    private String binary; // type 4 -> AND or OR
    private boolean bool; // type 5 -> true or false
    private boolean notNode; // type 6 -> NOT

    public ReturnHelperWherePart(int type, int index) {
        this.setType(type);
    }

    public ReturnHelperWherePart(int type, String inputString) {
        this.setType(type);

        if (inputString.toUpperCase().equals("AND"))
            setBinary("AND");
        else if (inputString.toUpperCase().equals("OR"))
            setBinary("OR");
        else
            setComparator(inputString);
    }

    public ReturnHelperWherePart(int type, double value) {
        this.setType(type);
        this.setValue(value);
    }

    public ReturnHelperWherePart(int type, boolean bool) {
        this.setType(type);

        if (type == 5)
            this.setBool(bool);
        if (type == 6)
            this.setNotNode(bool);
    }

    public boolean getBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isNotNode() {
        return notNode;
    }

    public void setNotNode(boolean notNode) {
        this.notNode = notNode;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public String getBinary() {
        return binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
