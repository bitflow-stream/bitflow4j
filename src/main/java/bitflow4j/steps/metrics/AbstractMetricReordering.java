package bitflow4j.steps.metrics;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Header;
import bitflow4j.Sample;

import java.io.IOException;

/**
 * Created by anton on 08.02.17.
 */
public abstract class AbstractMetricReordering extends AbstractPipelineStep {

    private Header lastHeader = null;
    private int[] outIndices;
    private Header outHeader;

    @Override
    public void writeSample(Sample sample) throws IOException {
        if (sample.getHeader().hasChanged(lastHeader)) {
            initializeHeader(sample.getHeader());
            lastHeader = sample.getHeader();
        }
        if (outHeader == null) {
            // The order of metrics is not changed - forward the incoming sample directly
            super.writeSample(sample);
            return;
        }

        double inValues[] = sample.getMetrics();
        double newValues[] = new double[outIndices.length];
        for (int i = 0; i < outIndices.length; i++) {
            int index = outIndices[i];
            if (index == -1) {
                newValues[i] = 0;
            } else {
                newValues[i] = inValues[outIndices[i]];
            }
        }

        super.writeSample(new Sample(outHeader, newValues, sample));
    }

    private void initializeHeader(Header inHeader) {
        outIndices = newHeaderIndices(inHeader);

        // Optimization: check if the order is not changed at all
        if (outIndices.length == inHeader.header.length) {
            boolean changed = false;
            for (int i = 0; i < outIndices.length; i++) {
                if (outIndices[i] != i) {
                    changed = true;
                    break;
                }
            }
            if (!changed) {
//                logger.info(this + ": Not reordering any metrics in:");
//                logger.info(this + ": " + Arrays.toString(inHeader.header));
                outHeader = null;
                return;
            }
        }

        String outFields[] = new String[outIndices.length];
        for (int i = 0; i < outFields.length; i++) {
            outFields[i] = inHeader.header[outIndices[i]];
        }
        outHeader = new Header(outFields);

//        logger.info(this + ": REORDERING: ");
//        logger.info(this + ": From: " + Arrays.toString(inHeader.header));
//        logger.info(this + ":   To: " + Arrays.toString(outHeader.header));
    }

    protected abstract int[] newHeaderIndices(Header inHeader);

}
