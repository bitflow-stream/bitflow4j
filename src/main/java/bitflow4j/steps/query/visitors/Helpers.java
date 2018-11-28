package bitflow4j.steps.query.visitors;

/**
 * This class have different methods that can be used by other classes.
 */
public class Helpers {

    /**
     * This method is used to check if the string str can be parsed to a double
     * without a parsing error.
     */
    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
