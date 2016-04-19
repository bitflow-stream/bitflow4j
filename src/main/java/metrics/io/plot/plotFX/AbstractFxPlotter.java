package metrics.io.plot.plotFX;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import metrics.io.plot.AbstractPlotter;
import metrics.io.plot.OutputMetricPlotter;

import java.io.IOException;
import java.util.Map;

/**
 * Created by mwall on 18.04.16.
 */
public class AbstractFxPlotter extends AbstractPlotter<FxDataContainer> {

    static Map<String, FxDataContainer> map;

    @Override
    public String toString() {
        return "java fx plot";
    }

    @Override
    public void plotResult(OutputMetricPlotter.PlotType outputType, Map<String, FxDataContainer> map, String filename) throws IOException {

        this.map = map;

        Application.launch(App.class);

    }

    public static class App extends Application {

        public App() {
            super();
        }

        @Override
        public void start(Stage stage) throws Exception {

            Platform.setImplicitExit(false);

            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            final ScatterChart<Number, Number> sc = new ScatterChart<>(xAxis, yAxis);
            xAxis.setLabel("Age (years)");
            yAxis.setLabel("Returns to date");
            sc.setTitle("Investment Overview");

            for (Map.Entry<String, FxDataContainer> entry : map.entrySet()) {
                entry.getValue().series.setName(entry.getKey());
                sc.getData().add(entry.getValue().series);
            }
            Scene scene = new Scene(sc, 500, 400);

            stage.setTitle("Scatter Chart Sample");

            stage.setScene(scene);
            stage.show();
        }
    }

    @Override
    public FxDataContainer createDataContainer(int numDimensions) {
        //series2.setName("Mutual funds");
        return new FxDataContainer(new XYChart.Series());
    }

    @Override
    public FxDataContainer createDataContainer(int numDimensions, String label) {
        XYChart.Series c = new XYChart.Series();
                c.setName(label);
        return new FxDataContainer(c);
    }
}
