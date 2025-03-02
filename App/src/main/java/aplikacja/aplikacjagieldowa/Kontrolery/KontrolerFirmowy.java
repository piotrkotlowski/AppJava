package aplikacja.aplikacjagieldowa.Kontrolery;

import aplikacja.aplikacjagieldowa.Kontrolery.KlasyWykresowe.DataPoint;
import aplikacja.aplikacjagieldowa.dane.RamkaDanych;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class KontrolerFirmowy extends Kontroler{
    @FXML
    ChoiceBox<String> wyborFirmyLubSurowca;
    @FXML
    TextField inneFirmyLubSurowce;
    @FXML
    DatePicker dataPoczatkowa, dataKoncowa;
    @FXML
    CheckBox szukanieNiestandardowe;
    @FXML
    Button przyciskWyszukiwania, przyciskCenaAktualna;
    @FXML
    TableView<Map.Entry<LocalDate, Float[]>> tabela;
    @FXML
    TableColumn<Map.Entry<LocalDate, Float[]>, String> kolumnaDat;
    @FXML
    TableColumn<Map.Entry<LocalDate, Float[]>, String> kolumnaCenOtwarcia, kolumnaCenMaksymalnych, kolumnaCenMinimalnych, kolumnaCenZamkniecia;
    @FXML
    LineChart<String, Number> wykres;
    @FXML
    RadioMenuItem czyWaluty, czyFirmy, czySurowce;
    @FXML
    Label cenaAkcjiAktualnej, nazwaFirmyAktualnej;
    private RamkaDanych ramkaDanych;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private List<String> DownloadedCompanies=List.of("IBM","NVDA","TSLA","MSFT","AAPL","AMZN","INTL");

    public void ustawCeneAktualna(){
        String nazwaFirmy;
        if (szukanieNiestandardowe.isSelected()){
            nazwaFirmy = inneFirmyLubSurowce.getText();
        } else {
            nazwaFirmy = wyborFirmyLubSurowca.getValue();
        }
        if (ApiChecker()) {
            if(nazwaFirmy!=null) {
                Float[] cenaAktualna = this.getFunctionsImplAlpha.getCompanyCurrentData(nazwaFirmy);
                nazwaFirmyAktualnej.setText(nazwaFirmy);
                cenaAkcjiAktualnej.setText("Aktualna cena wynosi: " + cenaAktualna[0] + waluta);
                apiCounter+=1;
            }else{
                JOptionPane.showMessageDialog(
                        null,
                        "Bledne dane firmy",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }else{
            JOptionPane.showMessageDialog(
                    null,
                    "Limit na API zostal wyczerpany, zostaje tylko strona z walutami",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
        }

    }

    public void przygotujTabele(){
        // ustawianie wartości
        kolumnaDat.setCellValueFactory(dana -> new SimpleObjectProperty<>(dana.getValue().getKey().format(dateTimeFormatter)));
        kolumnaCenOtwarcia.setCellValueFactory(dana -> new SimpleObjectProperty<>(dana.getValue().getValue()[0] + waluta));
        kolumnaCenMaksymalnych.setCellValueFactory(dana -> new SimpleObjectProperty<>(dana.getValue().getValue()[1] + waluta));
        kolumnaCenMinimalnych.setCellValueFactory(dana -> new SimpleObjectProperty<>(dana.getValue().getValue()[2] + waluta));
        kolumnaCenZamkniecia.setCellValueFactory(dana -> new SimpleObjectProperty<>(dana.getValue().getValue()[3] + waluta));
        // wyglad
        kolumnaDat.setStyle("-fx-alignment: center;");
        kolumnaCenOtwarcia.setStyle("-fx-alignment: center;");
        kolumnaCenMaksymalnych.setStyle("-fx-alignment: center;");
        kolumnaCenMinimalnych.setStyle("-fx-alignment: center;");
        kolumnaCenZamkniecia.setStyle("-fx-alignment: center;");
        //wybor firm
        wyborFirmyLubSurowca.getItems().addAll(DownloadedCompanies);

    }

    public void downloadButtonClicked() {
        try {
        if (ApiChecker()) {

        String nazwaFirmy;
        if (szukanieNiestandardowe.isSelected()) {
            nazwaFirmy = inneFirmyLubSurowce.getText();
        } else {
            nazwaFirmy = wyborFirmyLubSurowca.getValue();
        }
        //nazwaFirmy = wyborFirmyLubSurowca.getValue();
        LocalDate dataPoczatkowaWartosc = dataPoczatkowa.getValue();
        LocalDate dataKoncowaWartosc = dataKoncowa.getValue();

        if (nazwaFirmy == null) {

            JOptionPane.showMessageDialog(
                    null,
                    "Podaj firme",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE


            );
        } else if ((dataPoczatkowaWartosc==null) || (dataKoncowaWartosc==null)
                || (dataPoczatkowaWartosc.isAfter(dataKoncowaWartosc)) || (dataPoczatkowaWartosc.isEqual(dataKoncowaWartosc))
                || dataKoncowaWartosc.isAfter(LocalDate.now())) {
            JOptionPane.showMessageDialog(
                    null,
                    "Podaj dobra date",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
        }else {
            apiCounter+=2;
            if(this.getFunctionsImplAlpha.tickerChecker(nazwaFirmy)){
            this.DataManager.downloadCompany(nazwaFirmy, dataPoczatkowaWartosc, dataKoncowaWartosc);
            } else{
                JOptionPane.showMessageDialog(
                        null,
                        "Podaj dobra firme",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
                }
            }
        } else{
            JOptionPane.showMessageDialog(
                    null,
                    "Limit na API zostal wyczerpany, zostaje tylko strona z walutami",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
        }
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText(null); // Brak nagłówka
            alert.setContentText("Upss, coś poszło nie tak");
            alert.showAndWait();
        }

    }

    public void wyszukajFirme(){
        try {
            System.out.println();
            if (ApiChecker()) {
                String nazwaFirmy = null;
                LocalDate poczatekOkresuWyszukiwania, koniecOkresuWyszukiwania;
                poczatekOkresuWyszukiwania = dataPoczatkowa.getValue();
                koniecOkresuWyszukiwania = dataKoncowa.getValue();

                if (!((poczatekOkresuWyszukiwania == null) || (koniecOkresuWyszukiwania == null)
                        || (poczatekOkresuWyszukiwania.isAfter(koniecOkresuWyszukiwania)) || (poczatekOkresuWyszukiwania.isEqual(koniecOkresuWyszukiwania)) || koniecOkresuWyszukiwania.isAfter(LocalDate.now()))) {
                    if (ChronoUnit.YEARS.between(poczatekOkresuWyszukiwania, koniecOkresuWyszukiwania) > 6) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Maksymalny zakres do 6 lat",
                                "Warning",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }

                    if (szukanieNiestandardowe.isSelected()) {
                        nazwaFirmy = inneFirmyLubSurowce.getText();
                        if (nazwaFirmy != null && this.getFunctionsImplAlpha.tickerChecker(nazwaFirmy)) {
                            apiCounter += 1;
                            TreeMap<LocalDate, Float[]> map = this.getFunctionsImplAlpha.getCompanyHistoricData(nazwaFirmy, poczatekOkresuWyszukiwania, koniecOkresuWyszukiwania, "monthly");
                            ramkaDanych = new RamkaDanych(map, RamkaDanych.TypDanych.FIRMA);
                        } else {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Bledne dane firmy",
                                    "Warning",
                                    JOptionPane.WARNING_MESSAGE
                            );
                        }

                    } else {
                        nazwaFirmy = wyborFirmyLubSurowca.getValue();
                        if (nazwaFirmy != null) {
                            TreeMap<LocalDate, Float[]> map = this.getFunctionsImplAlpha.getCompanyHistoricData(nazwaFirmy, poczatekOkresuWyszukiwania, koniecOkresuWyszukiwania, "monthly");
                            ramkaDanych = new RamkaDanych(map, RamkaDanych.TypDanych.FIRMA);
                        }
                    }
                    if (nazwaFirmy != null) {
                        wykres.setVisible(false);
                        zaktualizujWykres(nazwaFirmy);
                        apiCounter += 1;
                        new Thread(() -> {
                            try {
                                Thread.sleep(1500);
                                Platform.runLater(() -> {
                                    wykres.setVisible(true);
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    } else {
                        JOptionPane.showMessageDialog(
                                null,
                                "Bledne dane",
                                "Warning",
                                JOptionPane.WARNING_MESSAGE
                        );
                        System.out.println("Bledne dane");
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            "Bledne dane",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE
                    );
                    System.out.println("Bledne dane");
                }
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Limit na API zostal wyczerpany, zostaje tylko strona z walutami",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText(null); // Brak nagłówka
            alert.setContentText("Upss, coś poszło nie tak ;(");
            alert.showAndWait();
        }
    }


    public void zmienOkno(ActionEvent actionEvent) throws IOException {
        if (czyFirmy.isSelected()){
            czyFirmy.setSelected(false);
        } else{
            super.zmienOkno(actionEvent, czyFirmy.isSelected(), czyWaluty.isSelected(), czySurowce.isSelected());
        }
    }

    @Override
    public void zmienNaAutorow(ActionEvent event) throws IOException {
        super.zmienNaAutorow(event);
    }

    public void zaktualizujWykres(String nazwaFirmy){
        CategoryAxis xAxis = (CategoryAxis) wykres.getXAxis();
        NumberAxis yAxis = (NumberAxis) wykres.getYAxis();

        xAxis.setLabel("Data");
        yAxis.setLabel("Cena");

        //LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        wykres.setTitle("Wykres przedstawiający akcje firmy: " + nazwaFirmy + " w okresie " + dataPoczatkowa.getValue().toString()+" - "+dataKoncowa.getValue().toString());

        //XYChart.Series<String, Number> highLine = new XYChart.Series<>();
        //XYChart.Series<String, Number> lowLine = new XYChart.Series<>();

        Map<String, XYChart.Series<String, Number>> seriesMap = new TreeMap<>();

        Map<String, XYChart.Series<String, Number>> mapaPatyczkow = new TreeMap<>();

        // Czyscimy dane tablicowe
        tabela.getItems().clear();

        float minimalnaCenaPerDane = Float.POSITIVE_INFINITY, maksymalnaCenaPerDane = 0;
        wykres.getData().clear();
        for (Map.Entry<LocalDate, Float[]> wiersz : ramkaDanych.getRekordy().entrySet()){
            tabela.getItems().add(wiersz);
            String data = wiersz.getKey().format(dateTimeFormatter);
            Float cenaOtwarcia = wiersz.getValue()[0];
            Float cenaMaksymalna = wiersz.getValue()[1];
            Float cenaMinimalna = wiersz.getValue()[2];
            Float cenaZamkniecia = wiersz.getValue()[3];

            // szukanie wartosc granicznych, zeby ograniczyc wykres
            if (cenaMinimalna < minimalnaCenaPerDane) minimalnaCenaPerDane = cenaMinimalna;
            if (cenaMaksymalna > maksymalnaCenaPerDane) maksymalnaCenaPerDane = cenaMaksymalna;

            //highLine.getData().add(new XYChart.Data<>(data, cenaMaksymalna));
            //lowLine.getData().add(new XYChart.Data<>(data, cenaMinimalna));

            if (!seriesMap.containsKey(data)) {
                // Tworzymy nową serię dla tej wartości X
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                seriesMap.put(data, series);
            }

            if (!mapaPatyczkow.containsKey(data)) {
                // Tworzymy nową serię dla tej wartości X
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                mapaPatyczkow.put(data, series);
            }

            DataPoint dataPoint = new DataPoint(data, cenaOtwarcia, cenaZamkniecia);
            XYChart.Series<String, Number> series = seriesMap.get(data);
            
            XYChart.Series<String, Number> seriesPatyczkowe = mapaPatyczkow.get(data);

            series.getData().add(new XYChart.Data<>(data, cenaOtwarcia, dataPoint));
            series.getData().add(new XYChart.Data<>(data, cenaZamkniecia, dataPoint));

            seriesPatyczkowe.getData().add(new XYChart.Data<>(data, cenaMaksymalna, dataPoint));
            seriesPatyczkowe.getData().add(new XYChart.Data<>(data, cenaMinimalna, dataPoint));
        }


        wykres.setLegendVisible(false);
        float margines = (maksymalnaCenaPerDane - minimalnaCenaPerDane) * 0.05F;
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(Math.round((maksymalnaCenaPerDane+margines)*100)/100F);
        yAxis.setLowerBound(Math.round((minimalnaCenaPerDane-margines)*100)/100F);

        wykres.getData().addAll(mapaPatyczkow.values());
        mapaPatyczkow.values().forEach(
                series -> {
                    series.getData().forEach(
                            dataa -> dataa.getNode().setStyle("-fx-background-radius: 0; -fx-padding: 0; -fx-opacity: 0;")
                    );
                    series.getNode().setStyle("-fx-stroke: " + toRgbString(getLineColor(series)) + ";-fx-stroke-width: 2;");
                }
        );

        // Kolorowanie punktów i linii
        wykres.getData().addAll(seriesMap.values());
        seriesMap.values().forEach(series -> {
            series.getData().forEach(dataa -> {
                // Kolorowanie punktów w zależności od warunków
//                DataPoint dp = (DataPoint) dataa.getExtraValue();
//                Color color = (dp.getWartosc2() > dp.getWartosc1()) ? Color.GREEN : Color.RED;
                dataa.getNode().setStyle("-fx-background-radius: 0; -fx-padding: 0; -fx-opacity: 0;");
            }
            );

            // Kolorowanie linii
            series.getNode().setStyle("-fx-stroke: " + toRgbString(getLineColor(series)) + ";-fx-stroke-width: 6;");
        });

        //wykres.getData().add(highLine);
        //wykres.getData().add(lowLine);

        //highLine.getData().forEach(dataa -> dataa.getNode().setStyle("-fx-background-radius: 0; -fx-padding: 0; -fx-opacity: 0;"));
        //lowLine.getData().forEach(dataa -> dataa.getNode().setStyle("-fx-background-radius: 0; -fx-padding: 0; -fx-opacity: 0;"));

        //highLine.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #d90f9c   ; -fx-stroke-width: 2;");
        //lowLine.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #1b4fd1; -fx-stroke-width: 2;");
    }

    private String toRgbString(Color color) {
        return String.format("rgb(%d,%d,%d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private Color getLineColor(XYChart.Series<String, Number> series) {
        boolean allGreater = true;
        for (XYChart.Data<String, Number> data : series.getData()) {
            DataPoint dp = (DataPoint) data.getExtraValue();

            if (dp.getWartosc2() <= dp.getWartosc1()) {
                allGreater = false;
                break;
                }
            }
        return allGreater ? Color.GREEN : Color.RED;
    }

}
