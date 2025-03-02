package aplikacja.aplikacjagieldowa.testy;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;

public class LineChartExample extends Application {

    @Override
    public void start(Stage stage) {

        // Przykładowe dane: (X, wartosc1, wartosc2)
        List<DataPoint> dataPoints = Arrays.asList(
                new DataPoint(1, 10, 3),
                new DataPoint(2, 15, 25),
                new DataPoint(3, 18, 28)

        );

        // Tworzymy oś X i Y
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("X");
        yAxis.setLabel("Wartość");

        // Tworzymy wykres liniowy
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Wykres Liniowy");

        // Mapowanie danych do wykresu
        Map<Integer, XYChart.Series<Number, Number>> seriesMap = new HashMap<>();

        // Grupowanie danych według wartości X
        for (DataPoint dp : dataPoints) {
            int xValue = dp.getX();

            // Sprawdzamy, czy seria dla danej wartości X już istnieje
            if (!seriesMap.containsKey(xValue)) {
                // Tworzymy nową serię dla tej wartości X
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("Seria X = " + xValue);
                seriesMap.put(xValue, series);
            }

            // Dodajemy punkty do odpowiedniej serii
            XYChart.Series<Number, Number> series = seriesMap.get(xValue);
            series.getData().add(new XYChart.Data<>(dp.getX(), dp.getWartosc1(), dp)); // Dodajemy odniesienie do DataPoint
            series.getData().add(new XYChart.Data<>(dp.getX(), dp.getWartosc2(), dp)); // Dodajemy odniesienie do DataPoint
        }

        // Dodajemy wszystkie serie do wykresu
        lineChart.getData().addAll(seriesMap.values());

        // Kolorowanie punktów i linii
        lineChart.getData().forEach(series -> {
            series.getData().forEach(data -> {
                // Kolorowanie punktów w zależności od warunków
                DataPoint dp = (DataPoint) data.getExtraValue(); // Pobieramy DataPoint z dodatkowej wartości
                Color color = (dp.getWartosc2() > dp.getWartosc1()) ? Color.GREEN : Color.RED;

                // Ustawianie koloru punktu
                data.getNode().setStyle("-fx-background-color: " + toRgbString(color) + ";");
            });

            // Kolorowanie linii
            series.getNode().setStyle("-fx-stroke: " + toRgbString(getLineColor(series)) + ";");
        });

        // Przygotowanie sceny
        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Wykres Liniowy");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Klasa reprezentująca pojedynczy punkt danych
    public static class DataPoint {
        private final int x;
        private final int wartosc1;
        private final int wartosc2;

        public DataPoint(int x, int wartosc1, int wartosc2) {
            this.x = x;
            this.wartosc1 = wartosc1;
            this.wartosc2 = wartosc2;
        }

        public int getX() {
            return x;
        }

        public int getWartosc1() {
            return wartosc1;
        }

        public int getWartosc2() {
            return wartosc2;
        }
    }

    // Pomocnicza funkcja do zamiany koloru na format RGB
    private String toRgbString(Color color) {
        return String.format("rgb(%d,%d,%d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    // Funkcja, która ustala kolor linii na podstawie warunku
    private Color getLineColor(XYChart.Series<Number, Number> series) {
        // Sprawdzamy, czy wszystkie punkty w serii mają 'wartosc2' większą niż 'wartosc1'
        boolean allGreater = true;
        for (XYChart.Data<Number, Number> data : series.getData()) {
            DataPoint dp = (DataPoint) data.getExtraValue();
            if (dp.getWartosc2() <= dp.getWartosc1()) {
                allGreater = false;
                break;
            }
        }
        return allGreater ? Color.GREEN : Color.RED;
    }
}
