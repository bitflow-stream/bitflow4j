package metrics.main.data;

import metrics.Header;
import metrics.Sample;
import metrics.io.InputStreamClosedException;
import metrics.io.MetricInputStream;
import metrics.io.aggregate.InputStreamProducer;
import metrics.io.aggregate.MetricInputAggregator;

import java.io.IOException;
import java.util.*;

/**
 * Created by anton on 5/5/16.
 */
public class MockDataSource extends DataSource<String> {

    private void fillSampleQueue(Queue<Sample> samples) {
        Header hdr = new Header(new String[]{"field1", "libvirt/instance-0000061d/field"});
        Sample sample1 = new Sample(hdr, new double[]{1, 2}, new Date());
        sample1.setLabel("label1");
        sample1.setSource("source1");
        Sample sample2 = new Sample(hdr, new double[]{3, 4}, new Date());
        sample1.setLabel("label2");
        sample1.setSource("source2");

        samples.offer(sample1);
        samples.offer(sample2);
    }

    @Override
    public InputStreamProducer createProducer(String source) throws IOException {
        return new InputStreamProducer() {

            @Override
            public void start(MetricInputAggregator aggregator) {
                aggregator.producerStarting(this);
                MockInputStream input = new MockInputStream();
                fillSampleQueue(input.samples);
                aggregator.addInput("mock-input", input);
                aggregator.producerFinished(this);
            }

        };
    }

    @Override
    public List<String> getAllSources() {
        return Collections.singletonList("mock-source");
    }

    @Override
    public String toString() {
        return "mock";
    }

    private static class MockInputStream implements MetricInputStream {

        final Queue<Sample> samples = new LinkedList<>();

        @Override
        public Sample readSample() throws IOException {
            Sample sample = samples.poll();
            if (sample == null)
                throw new InputStreamClosedException();
            else
                return sample;
        }

    }

}
