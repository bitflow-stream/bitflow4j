package bitflow4j.steps.visualization;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.io.IOException;

/**
 * Created by alex on 19.03.18.
 */
public abstract class AbstractJFreeChartSamplePlotter extends AbstractPipelineStep {

    protected ApplicationFrame liveApplicationFrame;
    protected ChartPanel chartPanel;
    protected Dataset dataSet;

    public AbstractJFreeChartSamplePlotter() {
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        if(chartPanel == null){
            this.initializeChart(sample);
        } else {
            this.updateDataSet(sample);
        }
        super.writeSample(sample);
    }

    protected void initializeChart(Sample sample) {
        dataSet = this.getInitialDataSet(sample);
        chartPanel = new ChartPanel(this.initializeChart(dataSet, sample));
        liveApplicationFrame = new ApplicationFrame("Live Sample Monitoring");
        chartPanel.setPreferredSize( new java.awt.Dimension( 860 , 640 ) );
        liveApplicationFrame.setContentPane(chartPanel);
        liveApplicationFrame.pack( );
        RefineryUtilities.centerFrameOnScreen(liveApplicationFrame);
        liveApplicationFrame.setVisible(true);
    }

    protected abstract void updateDataSet(Sample sample);
    protected abstract JFreeChart initializeChart(Dataset dataSet, Sample sample);
    protected abstract Dataset getInitialDataSet(Sample sample);
}
