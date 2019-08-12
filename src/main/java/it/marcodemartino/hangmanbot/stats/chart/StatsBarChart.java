package it.marcodemartino.hangmanbot.stats.chart;

import it.marcodemartino.hangmanbot.stats.StatsManager;
import it.marcodemartino.hangmanbot.stats.UserStats;
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
import java.util.List;

public class StatsBarChart extends Application {

    private static CustomStackedBarChart customStackedBarChart;
    private static Stage stage;

    public static void saveChart() {
        drawChart();
        Scene scene = new Scene(customStackedBarChart, 800, 600);
        stage.setScene(scene);
        saveAsPng();
    }

    private static void drawChart() {
        List<UserStats> bestUsers = StatsManager.getBestUsers();

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Giocatori");
        yAxis.setLabel("Valore");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Lettere indovinate");

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Lettere sbagliate");

        for (UserStats user : bestUsers) {
            series1.getData().add(new XYChart.Data(user.getUsername(), user.getGuessedLetters()));
            series2.getData().add(new XYChart.Data(user.getUsername(), user.getWrongLetters()));
        }

        customStackedBarChart = new CustomStackedBarChart(xAxis, yAxis);
        customStackedBarChart.setAnimated(false);
        customStackedBarChart.setStyle("-fx-font-size: 15px;");
        customStackedBarChart.getData().addAll(series1, series2);
    }

    private static void saveAsPng() {
        WritableImage image = customStackedBarChart.snapshot(new SnapshotParameters(), null);
        File file = new File("chart.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        drawChart();
        Scene scene = new Scene(customStackedBarChart, 800, 600);
        //scene.getStylesheets().add("stylesheet.css");
        StatsBarChart.stage = stage;
        stage.setScene(scene);
        saveAsPng();
        stage.close();
    }


}

