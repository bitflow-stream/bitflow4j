package metrics.main;

import java.io.IOException;

/**
 * Created by anton on 6/9/16.
 */
public class TrainingMain {

    static final String TRAINING_INPUT_FORMAT = "BIN";

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Need 2 parameters: <input " + TRAINING_INPUT_FORMAT + " file> <output file>");
            return;
        }
        TrainedDataModel model = PrototypeMain.createDataModel(args[0]);
        PrototypeMain.storeDataModel(args[1], model);
    }

}
