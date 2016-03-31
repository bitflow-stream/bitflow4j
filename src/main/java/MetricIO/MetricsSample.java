package MetricIO;

/**
 * Created by mwall on 30.03.16.
 */
public class MetricsSample {

    private String[] metricsHeader;
    private float[] metrics;

    public String[] getMetricsHeader() {
        return metricsHeader;
    }

    public void setMetricsHeader(String[] metricsHeader) {
        this.metricsHeader = metricsHeader;
    }

    public float[] getMetrics() {
        return metrics;
    }

    public void setMetrics(float[] metrics) {
        this.metrics = metrics;
    }
    
    public MetricsSample aggregate(MetricsSample otherSample){
        MetricsSample aggregatedSample = new MetricsSample();
        String[] aggregatedMetricsHeader = new String[this.getMetricsHeader().length + otherSample.getMetricsHeader().length];
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
