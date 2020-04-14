package bitflow4j.steps.query;

import bitflow4j.Header;
import bitflow4j.MockContext;
import bitflow4j.Sample;

import java.io.IOException;
import java.util.ArrayList;

public class QueryOptimization {

    private final ArrayList<Sample> list;
    private final MockContext sink;
    private final String query;

    public QueryOptimization(String query) {
        list = new ArrayList<>();
        sink = new MockContext();
        this.query = query;
    }

    void writeSampleOptimization(Sample s) throws IOException {
        if (query.equals("Select First")) {
            double[] values_output = {s.getMetrics()[0]};
            Header header_output = new Header(new String[]{"First"});
            Sample output = new Sample(header_output, values_output, s.getTimestamp(), s.getTags());
            sink.outputSample(output);
        } else if (query.equals("Select Sum(First) As SUM Where First>5 AND Second<5 Group by host Window 1000 Having SUM>1000")) {
            if (s.getMetrics()[0] > 5 && s.getMetrics()[1] < 5) {
                list.add(s);
                if (list.size() > 1000) list.remove(0);

                ArrayList<Sample> list_Germany = new ArrayList<>();
                ArrayList<Sample> list_England = new ArrayList<>();
                ArrayList<Sample> list_France = new ArrayList<>();
                ArrayList<Sample> list_null = new ArrayList<>();

                for (Sample actualSample : list) {
                    if (actualSample.hasTag("host")) {
                        switch (actualSample.getTag("host")) {
                            case "Germany":
                                list_Germany.add(actualSample);
                                break;
                            case "England":
                                list_England.add(actualSample);
                                break;
                            case "France":
                                list_France.add(actualSample);
                                break;
                        }
                    } else {
                        list_null.add(actualSample);
                    }
                }

                if (list_Germany.size() > 0) {
                    double sum = 0;
                    for (Sample actualSample : list_Germany) {
                        sum = sum + actualSample.getMetrics()[0];
                    }
                    if (sum > 1000) {
                        double[] values_output = {sum};
                        Header header_output = new Header(new String[]{"SUM"});
                        Sample output = new Sample(header_output, values_output, s.getTimestamp());
                        output.setTag("host", "Germany");
                        sink.outputSample(output);
                    }
                }
                if (list_England.size() > 0) {
                    double sum = 0;
                    for (Sample actualSample : list_England) {
                        sum = sum + actualSample.getMetrics()[0];
                    }
                    if (sum > 1000) {
                        double[] values_output = {sum};
                        Header header_output = new Header(new String[]{"SUM"});
                        Sample output = new Sample(header_output, values_output, s.getTimestamp());
                        output.setTag("host", "England");
                        sink.outputSample(output);
                    }
                }
                if (list_France.size() > 0) {
                    double sum = 0;
                    for (Sample actualSample : list_France) {
                        sum = sum + actualSample.getMetrics()[0];
                    }
                    if (sum > 1000) {
                        double[] values_output = {sum};
                        Header header_output = new Header(new String[]{"SUM"});
                        Sample output = new Sample(header_output, values_output, s.getTimestamp());
                        output.setTag("host", "France");
                        sink.outputSample(output);
                    }
                }
                if (list_null.size() > 0) {
                    double sum = 0;
                    for (Sample actualSample : list_null) {
                        sum = sum + actualSample.getMetrics()[0];
                    }
                    if (sum > 1000) {
                        double[] values_output = {sum};
                        Header header_output = new Header(new String[]{"SUM"});
                        Sample output = new Sample(header_output, values_output, s.getTimestamp());
                        sink.outputSample(output);
                    }
                }
            }
        }
    }

}
