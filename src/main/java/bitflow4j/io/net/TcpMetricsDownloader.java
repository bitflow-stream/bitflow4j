package bitflow4j.io.net;

import bitflow4j.Marshaller;
import bitflow4j.io.aggregate.InputStreamProducer;
import bitflow4j.io.aggregate.MetricInputAggregator;
import bitflow4j.main.ParameterHash;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anton on 04.11.16.
 */
public class TcpMetricsDownloader implements InputStreamProducer {

    private final List<RobustTcpMetricsReader> readers = new ArrayList<>();
    private final Marshaller marshaller;

    public TcpMetricsDownloader(String[] tcpSources, Marshaller marshaller) throws URISyntaxException {
        this.marshaller = marshaller;
        for (String source : tcpSources) {
            readers.add(new RobustTcpMetricsReader(source, marshaller));
        }
    }

    @Override
    public void start(MetricInputAggregator aggregator) {
        aggregator.producerStarting(this);
        for (RobustTcpMetricsReader reader : readers) {
            aggregator.addInput(reader.getSource(), reader);
        }
        aggregator.producerFinished(this);
    }

    @Override
    public void hashParameters(ParameterHash hash) {
        for (RobustTcpMetricsReader reader : readers) {
            hash.write(reader.getSource().getBytes());
        }
    }

}