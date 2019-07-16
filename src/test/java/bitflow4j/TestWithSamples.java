package bitflow4j;

import bitflow4j.misc.Config;
import bitflow4j.misc.Pair;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.*;

/**
 * Created by anton on 27.12.16.
 */
public class TestWithSamples {

    static {
        try {
            Config.initializeLogger();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Random random;

    private static Calendar getYear(int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        return c;
    }

    private static final long dateFrom = getYear(2010).getTime().getTime();
    private static final long dateTo = getYear(2015).getTime().getTime();

    @BeforeEach
    public void setup() {
        random = new Random(123123);
    }

    List<Pair<Header, List<Sample>>> createSamples() {
        String[][] fields = new String[][]{
                new String[]{ /* Empty header */},
                new String[]{"field1"},
                new String[]{"field1", "field2", "field3"},
                new String[]{"field1", "field2", "field3", "field4", "field5"},
        };

        List<Header> headers = new ArrayList<>();
        for (String[] header : fields) {
            headers.add(new Header(header));
        }

        List<Pair<Header, List<Sample>>> result = new ArrayList<>();
        for (Header h : headers) {
            result.add(new Pair<>(h, createSamplesFor(h, 0, 0)));
            result.add(new Pair<>(h, createSamplesFor(h, 1, 0)));
            result.add(new Pair<>(h, createSamplesFor(h, 2, 0)));
            result.add(new Pair<>(h, createSamplesFor(h, 3, 0)));
            result.add(new Pair<>(h, createSamplesFor(h, 4, 0)));

            result.add(new Pair<>(h, createSamplesFor(h, 0, 1)));
            result.add(new Pair<>(h, createSamplesFor(h, 1, 1)));
            result.add(new Pair<>(h, createSamplesFor(h, 2, 1)));
            result.add(new Pair<>(h, createSamplesFor(h, 3, 1)));
            result.add(new Pair<>(h, createSamplesFor(h, 4, 1)));

            result.add(new Pair<>(h, createSamplesFor(h, 0, 5)));
            result.add(new Pair<>(h, createSamplesFor(h, 1, 5)));
            result.add(new Pair<>(h, createSamplesFor(h, 2, 5)));
            result.add(new Pair<>(h, createSamplesFor(h, 3, 5)));
            result.add(new Pair<>(h, createSamplesFor(h, 4, 5)));
        }
        return result;
    }

    List<Sample> createSamplesFor(Header header, int numTags, int numFields) {
        List<Sample> result = new ArrayList<>();
        for (int i = 0; i < numFields; i++) {
            double[] metrics = new double[header.header.length];
            for (int j = 0; j < metrics.length; j++) {
                metrics[j] = random.nextDouble();
            }

            long time = dateFrom + (random.nextLong() % (dateTo - dateFrom));
            Date date = new Date(time);

            Sample sample = new Sample(header, metrics, date);
            for (int j = 0; j < numTags; j++) {
                // TODO add more variation and edge cases with the tags
                if (j == 1) {
                    sample.setTag("", "");
                } else if (j == 2) {
                    sample.setTag("key" + random.nextInt(), "");
                } else if (j == 3) {
                    sample.setTag("", "val" + random.nextInt());
                } else {
                    sample.setTag("key" + random.nextInt(), "val" + random.nextInt());
                }
            }
            result.add(sample);
//            System.out.println(date + ": " + Arrays.toString(metrics) + " -> " + sample.tagString());
        }
        return result;
    }

    List<Sample> flatten(List<Pair<Header, List<Sample>>> samples) {
        List<Sample> result = new ArrayList<>();
        for (Pair<Header, List<Sample>> header : samples) {
            result.addAll(header.getRight());
        }
        return result;
    }

}
