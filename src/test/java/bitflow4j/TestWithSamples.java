package bitflow4j;

import bitflow4j.main.Config;
import bitflow4j.sample.Header;
import bitflow4j.sample.Sample;
import javafx.util.Pair;
import org.junit.Before;

import java.util.*;

/**
 * Created by anton on 27.12.16.
 */
public class TestWithSamples {

    static {
        Config.initializeLogger();
    }

    private Random random;

    private static Calendar getYear(int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        return c;
    }

    private static final long dateFrom = getYear(2010).getTime().getTime();
    private static final long dateTo = getYear(2015).getTime().getTime();

    @Before
    public void setup() {
        random = new Random(123123);
    }

    List<Pair<Header, List<Sample>>> createSamples() {
        String fields[][] = new String[][]{
                new String[]{ /* Empty header */},
                new String[]{"field1"},
                new String[]{"field1", "field2", "field3"},
                new String[]{"field1", "field2", "field3", "field4", "field5"},
        };

        List<Header> headers = new ArrayList<>();
        for (String header[] : fields) {
            headers.add(new Header(header, true));
            headers.add(new Header(header, false));
        }

        List<Pair<Header, List<Sample>>> result = new ArrayList<>();
        for (Header h : headers) {
            result.add(new Pair<>(h, createSamplesFor(h, 0)));
            result.add(new Pair<>(h, createSamplesFor(h, 1)));
            result.add(new Pair<>(h, createSamplesFor(h, 5)));
        }
        return result;
    }

    List<Sample> createSamplesFor(Header header, int num) {
        // System.out.println("====== " + num + " SAMPLES FOR " + header.hasTags + ": " + Arrays.toString(header.header));
        List<Sample> result = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            double metrics[] = new double[header.header.length];
            for (int j = 0; j < metrics.length; j++) {
                metrics[j] = random.nextDouble();
            }

            long time = dateFrom + (random.nextLong() % (dateTo - dateFrom));
            Date date = new Date(time);

            Sample sample = new Sample(header, metrics, date);
            if (header.hasTags) {
                // TODO add more variation and edge case with the tags
                sample.setTag("key" + random.nextInt(), "val" + random.nextInt());
                sample.setTag("key" + random.nextInt(), "val" + random.nextInt());
                sample.setTag("key" + random.nextInt(), "val" + random.nextInt());
            }
            result.add(sample);
            // System.out.println(date + ": " + Arrays.toString(metrics) + " -> " + sample.tagString());
        }
        return result;
    }

    List<Sample> flatten(List<Pair<Header, List<Sample>>> samples) {
        List<Sample> result = new ArrayList<>();
        for (Pair<Header, List<Sample>> header : samples) {
            result.addAll(header.getValue());
        }
        return result;
    }

}
