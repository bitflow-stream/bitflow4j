package bitflow4j.main;

import bitflow4j.algorithms.Algorithm;
import bitflow4j.algorithms.NoopAlgorithm;
import bitflow4j.filter.Filter;
import bitflow4j.filter.SimpleFilter;
import bitflow4j.filter.ThreadedFilter;
import bitflow4j.io.EmptyOutputStream;
import bitflow4j.io.MetricInputStream;
import bitflow4j.io.MetricOutputStream;
import bitflow4j.io.MetricPipe;
import bitflow4j.io.fork.AbstractFork;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings({"unused", "WeakerAccess"})
public class BaseAlgorithmPipeline implements AlgorithmPipeline {

    private static final Logger logger = Logger.getLogger(BaseAlgorithmPipeline.class.getName());

    private boolean parallel = false;
    private MetricInputStream inputStream;
    private final List<Algorithm> algorithms = new ArrayList<>();
    private final List<BaseAlgorithmPipeline> forks = new ArrayList<>();
    private MetricOutputStream outputStream;

    private final TaskPool pool;
    private boolean started = false;

    public BaseAlgorithmPipeline(TaskPool pool) {
        this.pool = pool;
    }

    public BaseAlgorithmPipeline() {
        this(new TaskPool());
    }

    public BaseAlgorithmPipeline parallel() {
        parallel = true;
        return this;
    }

    // ===============================================
    // Inputs ========================================
    // ===============================================

    @Override
    public BaseAlgorithmPipeline input(MetricInputStream input) {
        if (this.inputStream != null)
            throw new IllegalStateException("outputStream was already configured");
        this.inputStream = input;
        return this;
    }

    public BaseAlgorithmPipeline inputListen(int port, String format) throws IOException {
        return (BaseAlgorithmPipeline) inputListen(pool, port, format);
    }

    public BaseAlgorithmPipeline inputDownload(String sources[], String format) throws URISyntaxException {
        return (BaseAlgorithmPipeline) inputDownload(pool, sources, format);
    }

    // ===============================================
    // Algorithms & Forks ============================
    // ===============================================

    @Override
    public AlgorithmPipeline step(Algorithm algo) {
        algorithms.add(algo);
        return this;
    }

    @Override
    public <T> AlgorithmPipeline fork(AbstractFork<T> fork, ForkHandler<T> handler) {
        fork.setOutputFactory(key -> {
            MetricPipe pipe = AlgorithmPipeline.newPipe();
            BaseAlgorithmPipeline subPipeline = newSubpipeline();
            subPipeline.input(pipe);
            handler.buildForkedPipeline(key, subPipeline);
            if (subPipeline.algorithms.isEmpty() && subPipeline.outputStream != null) {
                // Optimization: If there are no algorithms, skip the entire output and connect the output directly
                subPipeline.started = true;
                return subPipeline.outputStream;
            } else {
                subPipeline.run();
                return pipe;
            }
        });
        return output(fork);
    }

    public BaseAlgorithmPipeline newSubpipeline() {
        BaseAlgorithmPipeline subPipeline = new BaseAlgorithmPipeline(pool);
        forks.add(subPipeline);
        return subPipeline;
    }

    // =========================================
    // Outputs =================================
    // =========================================

    @Override
    public AlgorithmPipeline output(MetricOutputStream outputStream) {
        if (this.outputStream != null)
            throw new IllegalStateException("outputStream was already configured");
        this.outputStream = outputStream;
        return this;
    }

    // =========================================
    // Running =================================
    // =========================================

    @Override
    public void runAndWait() throws IOException {
        run();
        waitForOutput();
        pool.waitForTasks();
    }

    public synchronized void run() throws IOException {
        if (started) {
            return;
        }
        started = true;
        if (inputStream == null) {
            logger.warning("Pipeline running without active input stream");
        }
        if (outputStream == null) {
            outputStream = new EmptyOutputStream();
        }
        if (algorithms.size() == 0) {
            step(new NoopAlgorithm());
        }

        if (parallel)
            doRunParallel();
        else
            doRunRecursive();
    }


    // =========================================
    // Private =================================
    // =========================================

    private void waitForOutput() {
        outputStream.waitUntilClosed();
        forks.forEach(BaseAlgorithmPipeline::waitForOutput);
    }

    private void doRunRecursive() throws IOException {
        MetricOutputStream output = outputStream;
        for (int i = algorithms.size() - 1; i > 0; i--) {
            Algorithm algo = algorithms.get(i);
            Filter filter = new SimpleFilter();
            filter.start(algo, output);
            output = algo;
        }
        // Only one thread is started, which will pull samples from the input
        // and push them through the entire stack of Algorithms.
        startThreadedAlgorithm(inputStream, output, algorithms.get(0));
    }

    private void doRunParallel() throws IOException {
        MetricInputStream runningInput = inputStream;
        for (int i = 0; i < algorithms.size(); i++) {
            Algorithm algo = algorithms.get(i);
            MetricInputStream input = runningInput;
            MetricOutputStream output;

            if (i < algorithms.size() - 1) {
                MetricPipe pipe = AlgorithmPipeline.newPipe();
                runningInput = pipe;
                output = pipe;
            } else {
                output = outputStream;
            }

            startThreadedAlgorithm(input, output, algo);
        }
    }

    private void startThreadedAlgorithm(MetricInputStream input, MetricOutputStream output, Algorithm algo) throws IOException {
        Filter filter;
        if (input == null) {
            filter = new SimpleFilter();
        } else {
            filter = new ThreadedFilter(pool, input);
        }
        filter.start(algo, output);
    }

}
