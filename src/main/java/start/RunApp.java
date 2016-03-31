package start;

import MetricIO.MetricInputStream;
import MetricIO.MetricsSample;
import MetricIO.TCPMetricOutputStream;
import MetricIO.TcpMetricInputStream;

import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author fschmidt
 */
public class RunApp {

    static final int PORT = 9999;

    public static void main(String[] args){

        try {

            MetricInputStream mis = new TcpMetricInputStream(PORT,"CSV");
            while(true) {
                try {
                    Thread.sleep(500);

                    MetricsSample sample = mis.readSample();


                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            } catch (IOException e) {
            e.printStackTrace();
        }


    }
    
}
