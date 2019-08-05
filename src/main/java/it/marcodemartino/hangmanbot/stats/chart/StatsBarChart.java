package it.marcodemartino.hangmanbot.stats.chart;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class StatsBarChart extends Application {

    private Map<String, Integer> guessedLetters;
    private Map<String, Integer> wrongLetters;

    @Override
    public void start(Stage stage) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Giocatori");
        yAxis.setLabel("Valore");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Lettere indovinate");

        guessedLetters.forEach((username, data) -> series1.getData().add(new XYChart.Data(username, data)));

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Lettere sbagliate");
        wrongLetters.forEach((username, data) -> series2.getData().add(new XYChart.Data(username, data)));

        CustomStackedBarChart customStackedBarChart = new CustomStackedBarChart(xAxis, yAxis);
        customStackedBarChart.setAnimated(false);
        customStackedBarChart.setStyle("-fx-font-size: 15px;");

        Scene scene = new Scene(customStackedBarChart, 800, 600);
        scene.getStylesheets().add("stylesheet.css");

        customStackedBarChart.getData().addAll(series1, series2);
        stage.setScene(scene);
        saveAsPng(customStackedBarChart, "chart.png");
        stage.close();
    }

    public void saveAsPng(CustomStackedBarChart barChart, String path) {
        WritableImage image = barChart.snapshot(new SnapshotParameters(), null);
        File file = new File(path);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

