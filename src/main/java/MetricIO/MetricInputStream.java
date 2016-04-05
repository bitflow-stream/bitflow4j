package MetricIO;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by mwall on 30.03.16.
 */
public interface MetricInputStream {


    public MetricsSample readSample() throws IOException;


}
