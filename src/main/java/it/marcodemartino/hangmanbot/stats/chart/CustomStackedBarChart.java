package it.marcodemartino.hangmanbot.stats.chart;

import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

class CustomStackedBarChart extends StackedBarChart<String, Number> {

    public CustomStackedBarChart(CategoryAxis xAxis, Axis<Number> yAxis) {
        super(xAxis, yAxis);
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();

        for (Series<String, Number> series : getData()) {
            for (Data<String, Number> data : series.getData()) {
                StackPane bar = (StackPane) data.getNode();

                final Text dataText = new Text(data.getYValue() + "");
                bar.getChildren().add(dataText);

            }
        }
    }
}

