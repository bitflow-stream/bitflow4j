package Marshaller;

import Metrics.Sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mwall on 30.03.16.
 */
public class BinaryMarshaller implements Marshaller {

    public Sample unmarshallSample(DataInputStream input, String[] header) throws IOException {

        Date timestamp = new Date(input.readLong() / 1000000);
        Double value = 0.0;
        List<Double> metricList = new ArrayList<Double>();

        while((value = input.readDouble()) != null) {
            metricList.add(value);
        }

        return new Sample(header, timestamp, (Double[]) metricList.toArray());
    }

    public String[] unmarshallHeader(DataInputStream input) throws IOException {

        String value;
        List<String> headerList = new ArrayList<String>();

        while(!(value = input.readUTF()).isEmpty()) {

            headerList.add(value);
        }

        return (String[]) headerList.toArray();
    }

    public void marshallSample(DataOutputStream output, Sample sample) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    public void marshallHeader(DataOutputStream output, String[] header) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

}
