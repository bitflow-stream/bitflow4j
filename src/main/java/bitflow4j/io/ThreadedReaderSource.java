package bitflow4j.io;

import bitflow4j.sample.Sample;
import bitflow4j.sample.ThreadedSource;
import bitflow4j.task.TaskPool;

import java.io.IOException;

/**
 * Created by anton on 23.12.16.
 */
public abstract class ThreadedReaderSource extends ThreadedSource {

    public boolean suppressHeaderUpdateLogs = false;

    @Override
    public void readSamples(TaskPool pool, SampleGenerator generator, boolean keepAlive) throws IOException {
        if (generator instanceof SampleReader) {
            SampleReader reader = (SampleReader) generator;
            reader.suppressHeaderUpdateLogs = suppressHeaderUpdateLogs;
            reader.inputClosedHook = this::handleClosedInput;
        }
        super.readSamples(pool, generator, keepAlive);
    }

    // ================================================================================================
    // TODO the code below is a hack to enable synchronized reading of files.
    // Must be handled differently in the future.
    // This hack also includes the inputClosedHook field of SampleReader.
    // ================================================================================================

    public static final String INPUT_FILE_SAMPLE_ID_TAG = "input-file-sample-id";

    public interface FileInputFinishedHook {
        // Implementation can block here, the next file will be started only after
        // this method returns
        void finishedFileInput(int numSampleIds);
    }

    private FileInputFinishedHook fileFinishedHook = null;
    private int readSamples = 0;

    public void setFileInputNotification(FileInputFinishedHook hook) {
        fileFinishedHook = hook;
    }

    protected Sample handleGeneratedSample(Sample sample) {
        if (fileFinishedHook != null) {
            synchronized (outputLock) {
                sample.setTag(INPUT_FILE_SAMPLE_ID_TAG, String.valueOf(readSamples));
                readSamples++;
            }
        }
        return sample;
    }

    private void handleClosedInput() {
        if (fileFinishedHook != null) {
            synchronized (outputLock) {
                fileFinishedHook.finishedFileInput(readSamples);
                readSamples = 0;
            }
        }
    }

}
