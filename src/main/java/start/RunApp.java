package start;

import Marshaller.CsvMarshaller;
import Marshaller.Marshaller;
import MetricIO.MetricInputStream;
import MetricIO.MetricsSample;
import MetricIO.TcpMetricInputStream;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author fschmidt
 */
public class RunApp {

    static final int PORT = 9999;

    public static void main(String[] args){
        try {
            Marshaller marshaller = new CsvMarshaller();
            System.out.println("Listening on " + PORT);
            MetricInputStream mis = new TcpMetricInputStream(PORT, marshaller);

            while (true) {

                MetricsSample sample = mis.readSample();
                System.out.println("Received: " + Arrays.toString(sample.getHeader()));
                System.out.println("Data: " + Arrays.toString(sample.getMetrics()));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
