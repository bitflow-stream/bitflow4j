package bitflow4j.io.net;

import bitflow4j.Marshaller;
import bitflow4j.io.ActiveInputStream;

import java.net.URISyntaxException;

/**
 * Created by anton on 04.11.16.
 */
public class TcpMetricsDownloader extends ActiveInputStream {

    public TcpMetricsDownloader(String[] tcpSources, Marshaller marshaller) throws URISyntaxException {
        for (String source : tcpSources) {
            new ReaderThread(source, new TcpMetricsReader(source, marshaller)).start();
        }
    }

}
