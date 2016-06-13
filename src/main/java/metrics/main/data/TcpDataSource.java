package metrics.main.data;

import metrics.io.aggregate.InputStreamProducer;
import metrics.io.aggregate.ParallelAggregator;
import metrics.io.net.TcpMetricsListener;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static metrics.main.AlgorithmPipeline.getMarshaller;

/**
 * Created by anton on 4/29/16.
 */
public class TcpDataSource extends DataSource<Integer> {

    private final int numConnections;
    private final int port;
    private final String inputMarshaller;

    public TcpDataSource(int port, String inputMarshaller, int numConnections) {
        this.port = port;
        this.inputMarshaller = inputMarshaller;
        this.numConnections = numConnections;
    }

    @Override
    public List<Integer> getAllSources() {
        return Collections.singletonList(port);
    }

    @Override
    public InputStreamProducer createProducer(Integer source) throws IOException {
        return new TcpMetricsListener(port, getMarshaller(inputMarshaller), numConnections);
    }

    public ParallelAggregator preferredAggregator() {
        return new ParallelAggregator();
    }

    @Override
    public String toString() {
        return "TCP";
    }

}
