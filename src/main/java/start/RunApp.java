package start;

import MetricIO.MetricInputStream;
import MetricIO.TCPMetricOutputStream;
import MetricIO.TcpMetricInputStream;

import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author fschmidt
 */
public class RunApp {
    
    public static void main(String[] args){

        System.out.println("Hello world");
        try {

            MetricInputStream mis = new TcpMetricInputStream(9999,"CSV");
            while(true) {

                //System.out.println("read "+mis.readSample().getMetricsHeader().toString());
            }
            } catch (IOException e) {
            // restart TcpMetricInputStream ...
            e.printStackTrace();
        }


    }
    
}
