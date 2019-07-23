package bitflow4j.steps;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.script.registry.BitflowConstructor;
import bitflow4j.script.registry.Optional;
import ode.multivariate.densitygrids.DensityGridODE;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;


/**
 * Online density
 * <p>
 * Created by alex on 04.04.19.
 */
public class OnlineEstimateDensityWithGrids extends AbstractPipelineStep {

    protected static final Logger logger = Logger.getLogger(OnlineEstimateDensityWithGrids.class.getName());

    private final DensityGridODE model;

    private final boolean enableSampling;
    private final boolean setSparseToZero;
    private final boolean setSporadicToZero;

    private final String pathToFile;
    private BufferedWriter writer;

    /* Internal State */
    private long sampleCounter;

    public OnlineEstimateDensityWithGrids(DensityGridODE model, boolean enableSampling, boolean setSparseToZero,
                                          boolean setSporadicToZero, String pathToFile) throws IOException {
        this.model = model;
        this.enableSampling = enableSampling;
        this.setSparseToZero = setSparseToZero;
        this.setSporadicToZero = setSporadicToZero;
        this.pathToFile = pathToFile;
        if (enableSampling) {
            writer = new BufferedWriter(new FileWriter(pathToFile, false));
            this.writeToFile(this.getHeader());
        } else {
            writer = null;
        }
    }

    @BitflowConstructor
    public OnlineEstimateDensityWithGrids(List<Integer> p, List<Double> minValues, List<Double> maxValues,
                                          @Optional(defaultDouble = .97) double decayFactor,
                                          @Optional(defaultDouble = .7) double cm,
                                          @Optional(defaultDouble = 4.0) double cl,
                                          @Optional(defaultDouble = 0.02) double beta,
                                          @Optional(defaultBool = true) boolean enableSampling,
                                          @Optional(defaultBool = true) boolean setSparseToZero,
                                          @Optional(defaultBool = true) boolean setSporadicToZero,
                                          String pathToFile) throws IOException {
        this(new DensityGridODE(p.stream().mapToInt(i -> i).toArray(),
                        minValues.stream().mapToDouble(i -> i).toArray(),
                        maxValues.stream().mapToDouble(i -> i).toArray(),
                        decayFactor, cm, cl, beta, 0L, true),
                enableSampling, setSparseToZero, setSporadicToZero, pathToFile);
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        this.sampleCounter++;
        this.model.fitOnInstance(sample.getMetrics(), this.sampleCounter);
        if (enableSampling) {
            this.writeToFile(this.getResult());
        }
        super.writeSample(sample);
    }

    private void writeToFile(String... lines) throws IOException {
        for (String line : lines) {
            writer.write(line + System.lineSeparator());
        }
    }

    private String getHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("sampleCounter");
        for (String key : model.getFlattenDensityGridValues(this.setSparseToZero, this.setSporadicToZero).keySet()) {
            sb.append(",").append(key);
        }
        return sb.toString();
    }

    private String getResult() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sampleCounter);
        for (Double value : model.getFlattenDensityGridValues(this.setSparseToZero, this.setSporadicToZero).values()) {
            sb.append(",").append(String.format("%7.3e", value));
        }
        return sb.toString();
    }

    @Override
    protected void doClose() throws IOException {
        if (!enableSampling) {
            writer = new BufferedWriter(new FileWriter(pathToFile, false));
            this.writeToFile(this.getHeader(), this.getResult());
        }
        this.writer.flush();
        this.writer.close();
        super.doClose();
    }
}
