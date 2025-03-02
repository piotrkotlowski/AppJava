package aplikacja.aplikacjagieldowa.testy;

import aplikacja.aplikacjagieldowa.dane.MenadzerDanych;
import aplikacja.aplikacjagieldowa.dane.RamkaDanych;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CustomCandlestickChartWithLines extends Application {
        private static class DataPoint {
            private final int x;
            private final double wartosc1;
            private final double wartosc2;

            public DataPoint(int x, double wartosc1, double wartosc2) {
                this.x = x;
                this.wartosc1 = wartosc1;
                this.wartosc2 = wartosc2;
            }

            public double getX() {
                return x;
            }

            public double getWartosc1() {
                return wartosc1;
            }

            public double getWartosc2() {
                return wartosc2;
            }
        }

        private static class CandleData {
            final String date;
            final double open;
            final double high;
            final double low;
            final double close;

            public CandleData(String date, double open, double high, double low, double close) {
                this.date = date;
                this.open = open;
                this.high = high;
                this.low = low;
                this.close = close;
            }
        }

    @Override
    public void start(Stage primaryStage) {
        // Przykładowe dane
        List<CandleData> data = new ArrayList<>();

        RamkaDanych ramkaDanych = MenadzerDanych.wczytajPlikCSV(
                "C:\\Users\\ludwi\\OneDrive\\Pulpit\\STUDIA\\2024-2025 Semestr_03\\Zaawansowane_Programowanie_Obiektowe\\BednarzKrul\\AplikacjaGieldowa\\src\\main\\java\\aplikacja\\aplikacjagieldowa\\dane\\kot.txt",
                RamkaDanych.TypDanych.FIRMA);
        System.out.println(ramkaDanych);
//        data.add(new CandleData("2024-01-01", 100, 110, 95, 105));
//        data.add(new CandleData("2024-01-02", 105, 115, 102, 110));
//        data.add(new CandleData("2024-01-03", 110, 120, 108, 115));
//        data.add(new CandleData("2024-01-04", 115, 118, 112, 113));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Map.Entry<LocalDate, Float[]> wiersz : ramkaDanych.getRekordy().entrySet()){
            String chuj = wiersz.getKey().format(dateTimeFormatter);
            Float cenaOtwarcia = wiersz.getValue()[0];
            Float cenaMaksymalna = wiersz.getValue()[1];
            Float cenaMinimalna = wiersz.getValue()[2];
            Float cenaZamkniecia = wiersz.getValue()[3];

            data.add(new CandleData(chuj, cenaOtwarcia, cenaMaksymalna, cenaMinimalna, cenaZamkniecia));
        }

        // Tworzenie osi wykresu
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Price");

        // Tworzenie wykresu typu LineChart
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Candlestick Chart with High, Low and Open/Close");

        // Tworzenie serii dla linii z najwyższymi cenami
        XYChart.Series<Number, Number> highLine = new XYChart.Series<>();
        highLine.setName("High Prices");

        // Tworzenie serii dla linii z najniższymi cenami
        XYChart.Series<Number, Number> lowLine = new XYChart.Series<>();
        lowLine.setName("Low Prices");

        Map<Integer, XYChart.Series<Number, Number>> seriesMap = new HashMap<>();
        // Tworzenie wykresu
        for (int i = 0; i < data.size(); i++) {
            CandleData candle = data.get(i);

            highLine.getData().add(new XYChart.Data<>(i, candle.high));

            lowLine.getData().add(new XYChart.Data<>(i, candle.low));


            if (!seriesMap.containsKey(i)) {
                // Tworzymy nową serię dla tej wartości X
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("Seria X = " + i);
                seriesMap.put(i, series);
            }

            DataPoint dp = new DataPoint(i, candle.open, candle.close);
            XYChart.Series<Number, Number> series = seriesMap.get(i);
            series.getData().add(new XYChart.Data<>(i, candle.open, dp)); // Dodajemy odniesienie do DataPoint
            series.getData().add(new XYChart.Data<>(i, candle.close, dp)); // Dodajemy odniesienie do DataPoint


//            XYChart.Series<Number, Number> nowyPrzebieg = new XYChart.Series<>();
//
//            nowyPrzebieg.getData().add(new XYChart.Data<>(i, candle.open));
//            nowyPrzebieg.getData().add(new XYChart.Data<>(i, candle.close));
//            chart.getData().add(nowyPrzebieg);


//            Node line = nowyPrzebieg.getNode().lookup(".chart-series-area-line");
//            if (candle.open < candle.close) {
//                line.setStyle("-fx-stroke: green;");
//            } else {
//                line.setStyle("-fx-stroke: red;");
//            }
        }
        chart.getData().addAll(seriesMap.values());

        // Kolorowanie punktów i linii
        chart.getData().forEach(series -> {
            series.getData().forEach(dataa -> {
                dataa.getNode().setStyle("-fx-background-radius: 0; -fx-padding: 0; -fx-opacity: 0;"); // tu byla zmiana
            });

            // Kolorowanie linii
            series.getNode().setStyle("-fx-stroke: " + toRgbString(getLineColor(series)) + ";-fx-stroke-width: 4;"); // tu tez
        });

        chart.setLegendVisible(false);
        // Dodanie serii do wykresu
        chart.getData().add(highLine);
        chart.getData().add(lowLine);

        // zmiany
        highLine.getData().forEach(dataa -> dataa.getNode().setStyle("-fx-background-radius: 0; -fx-padding: 0; -fx-opacity: 0;"));
        lowLine.getData().forEach(dataa -> dataa.getNode().setStyle("-fx-background-radius: 0; -fx-padding: 0; -fx-opacity: 0;"));

        highLine.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #d90f9c   ; -fx-stroke-width: 2;");
        lowLine.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #1b4fd1; -fx-stroke-width: 2;");

        float maksymalnaCenaPerDane = 215.9F;
        float minimalnaCenaPerDane = 149.52F;

        float margines = (maksymalnaCenaPerDane - minimalnaCenaPerDane) * 0.05F;
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(maksymalnaCenaPerDane+margines);
        yAxis.setLowerBound(minimalnaCenaPerDane-margines);
//        yAxis.setAutoRanging(false);
//        yAxis.setLowerBound(100F);
//        yAxis.setUpperBound(400F);

        // Tworzenie sceny i wyświetlenie
        StackPane root = new StackPane(chart);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Candlestick Chart with High, Low and Open/Close");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


    private String toRgbString(Color color) {
        return String.format("rgb(%d,%d,%d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private Color getLineColor(XYChart.Series<Number, Number> series) {
        boolean allGreater = true;
        for (XYChart.Data<Number, Number> data : series.getData()) {
            CustomCandlestickChartWithLines.DataPoint dp = (CustomCandlestickChartWithLines.DataPoint) data.getExtraValue();
            if (dp.getWartosc2() <= dp.getWartosc1()) {
                allGreater = false;
                break;
            }
        }
        return allGreater ? Color.GREEN : Color.RED;
    }
}
