package bitflow4j.steps.query;

import bitflow4j.Sample;

import java.util.Map;

public class TestHelpers {

    /**
     * This method can be uses to compare two samples. In addition it prints on
     * the console what is different. Returns true when s1 and s2 have the same
     * content.
     */
    public static boolean compareSamples(Sample s1, Sample s2) {

        if (!s1.getTimestamp().equals(s2.getTimestamp())) {
            System.out.println("Different timestamps!");
            return false;
        }

        boolean mapsAreEqual = equalMaps(s1.getTags(), s2.getTags());
        if (!mapsAreEqual) {
            System.out.println("Different tags!");
            return false;
        }

        if (!s1.getHeader().equals(s2.getHeader())) {
            System.out.println("Different headers!");

            for (String s : s1.getHeader().header) {
                System.out.println(s);
            }

            for (String s : s2.getHeader().header) {
                System.out.println(s);
            }
            return false;
        }
        if (s1.getMetrics().length != s2.getMetrics().length)
            return false;

        for (int i = 0; i < s1.getMetrics().length; i++) {
            if (s1.getMetrics()[i] != s2.getMetrics()[i]) {
                System.out.println("Different metrics: " + s1.getMetrics()[i] + " - " + s2.getMetrics()[i]);
                return false;
            }
        }
        return true;
    }

    private static boolean equalMaps(Map<String, String> m1, Map<String, String> m2) {
        if (m1.size() != m2.size())
            return false;
        for (String key : m1.keySet())
            if (!m1.get(key).equals(m2.get(key)))
                return false;
        return true;
    }

}
