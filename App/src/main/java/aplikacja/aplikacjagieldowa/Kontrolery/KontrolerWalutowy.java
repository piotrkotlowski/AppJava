package aplikacja.aplikacjagieldowa.Kontrolery;

import aplikacja.aplikacjagieldowa.dane.RamkaDanych;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class KontrolerWalutowy extends Kontroler{
    @FXML
    ChoiceBox<String> waluta1, waluta2;
    @FXML
    DatePicker dataPoczatkowa, dataKoncowa;
    @FXML
    CheckBox sprawdzenieCzyDaneAktualne;
    @FXML
    Button przyciskWyszukiwania;
    @FXML
    TableView<Map.Entry<LocalDate, Float[]>> tabela;
    @FXML
    LineChart<String, Number> wykres;
    @FXML
    RadioMenuItem czyWaluty, czyFirmy, czySurowce;
    @FXML
    TableColumn<Map.Entry<LocalDate, Float[]>, String> kolumnaDat, kolumnaKupno, kolumnaSprzedaz;
    @FXML
    Label paraWalutowaNazwa, paraWalutowaKupno, paraWalutowaSprzedaz;
    public void przygotujTabele(){
        kolumnaDat.setCellValueFactory(dana -> new javafx.beans.property.SimpleObjectProperty<>(dana.getValue().getKey().format(dateTimeFormatter)));
        kolumnaKupno.setCellValueFactory(dana -> new javafx.beans.property.SimpleObjectProperty<>(Float.toString(dana.getValue().getValue()[0])));
        kolumnaSprzedaz.setCellValueFactory(dana -> new javafx.beans.property.SimpleObjectProperty<>(Float.toString(dana.getValue().getValue()[1])));

        waluta1.getItems().addAll("usd","chf","eur","gbp","pln");
        waluta2.getItems().addAll("usd","chf","eur","gbp","pln");

        kolumnaDat.setStyle("-fx-alignment: center;");
        kolumnaKupno.setStyle("-fx-alignment: center;");
        kolumnaSprzedaz.setStyle("-fx-alignment: center;");
    }

    public void zmienOkno(ActionEvent actionEvent) throws IOException {
        if (czyWaluty.isSelected()){
            czyWaluty.setSelected(false);
        } else{
            super.zmienOkno(actionEvent, czyFirmy.isSelected(), czyFirmy.isSelected(), czySurowce.isSelected());
        }
    }

    @Override
    public void zmienNaAutorow(ActionEvent event) throws IOException {
        super.zmienNaAutorow(event);
    }

    public void zaktualizujWykresiTabele(String paraWalutowa){
        CategoryAxis xAxis = (CategoryAxis) wykres.getXAxis();
        NumberAxis yAxis = (NumberAxis) wykres.getYAxis();

        xAxis.setLabel("Data");
        yAxis.setLabel("Cena");

        wykres.setTitle("Wykres przedstawiający historię pary walutowej: "+paraWalutowa);
        wykres.getData().clear();
        wykres.setLegendVisible(false);

        XYChart.Series<String, Number> sprzedazLine = new XYChart.Series<>();
        XYChart.Series<String, Number> kupnoLine = new XYChart.Series<>();
        sprzedazLine.setName("Cena sprzedaży");
        kupnoLine.setName("Cena kupna");

        float minimalnaCenaPerDane = Float.POSITIVE_INFINITY, maksymalnaCenaPerDane = 0;

        tabela.getItems().clear();

        for (Map.Entry<LocalDate, Float[]> wiersz : ramkaDanych.getRekordy().entrySet()){
            tabela.getItems().add(wiersz);
            String data = wiersz.getKey().format(dateTimeFormatter);
            Float cenaKupna = wiersz.getValue()[0];
            Float cenaSprzedazy = wiersz.getValue()[1];

            if (cenaSprzedazy < minimalnaCenaPerDane) minimalnaCenaPerDane = cenaSprzedazy;
            if (cenaSprzedazy > maksymalnaCenaPerDane) maksymalnaCenaPerDane = cenaSprzedazy;
            if (cenaKupna < minimalnaCenaPerDane) minimalnaCenaPerDane = cenaKupna;
            if (cenaKupna > maksymalnaCenaPerDane) maksymalnaCenaPerDane = cenaKupna;

            sprzedazLine.getData().add(new XYChart.Data<>(data, cenaSprzedazy));
            kupnoLine.getData().add(new XYChart.Data<>(data, cenaKupna));
        }

        // Ustawianie zakresu osi OY
        float margines = (maksymalnaCenaPerDane - minimalnaCenaPerDane) * 0.05F;
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(maksymalnaCenaPerDane+margines);
        yAxis.setLowerBound(minimalnaCenaPerDane-margines);

        wykres.getData().addAll(sprzedazLine, kupnoLine);

        sprzedazLine.getData().forEach(dataa -> dataa.getNode().setStyle("-fx-background-radius: 0; -fx-padding: 0; -fx-opacity: 0;"));
        kupnoLine.getData().forEach(dataa -> dataa.getNode().setStyle("-fx-background-radius: 0; -fx-padding: 0; -fx-opacity: 0;"));

        sprzedazLine.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #d90f9c   ; -fx-stroke-width: 2;");
        kupnoLine.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #1b4fd1; -fx-stroke-width: 2;");
    }

    public void downloadButtonClicked() {
        try {
        String waluta1String = waluta1.getValue();
        String waluta2String = waluta2.getValue();
        LocalDate dataPoczatkowaWartosc = dataPoczatkowa.getValue();
        LocalDate dataKoncowaWartosc = dataKoncowa.getValue();

        if ((waluta1String == null || waluta2String == null)) {
            JOptionPane.showMessageDialog(
                    null,
                    "Podaj walute",
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
            this.DataManager.downloadCurrency(waluta1String,waluta2String, dataPoczatkowaWartosc, dataKoncowaWartosc);
        }
    } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText(null); // Brak nagłówka
            alert.setContentText("Upss, coś poszło nie tak");
            alert.showAndWait();
        }
    }
    public void akcjaPrzycisku() {
        try {
        String waluta1String = waluta1.getValue();
        String waluta2String = waluta2.getValue();
        Float[] a;
        if (!(waluta1String == null || waluta2String == null)) {
            if (sprawdzenieCzyDaneAktualne.isSelected()) {
                if (!waluta1String.equals("pln") && waluta2String.equals("pln")) {
                    a = this.getFunctionImplNbp.getCurrentCurrencyPol(waluta1String);
                } else if (!waluta1String.equals("pln") && !waluta2String.equals("pln")) {
                    a = this.getFunctionImplNbp.getCurrentCurrency(waluta1String, waluta2String);
                } else {
                    a = this.getFunctionImplNbp.getCurrentCurrencyPol(waluta2String);
                    a = this.getFunctionImplNbp.reverseCurrency(a);
                }

                Float[] aktualneCeny = a;
                paraWalutowaKupno.setText(Float.toString(aktualneCeny[0]));
                paraWalutowaSprzedaz.setText(Float.toString(aktualneCeny[1]));
                paraWalutowaNazwa.setText(waluta1String + " - " + waluta2String);
                return;
            }

            LocalDate dataPoczatkowaWartosc = dataPoczatkowa.getValue();
            LocalDate dataKoncowaWartosc = dataKoncowa.getValue();

            TreeMap<LocalDate, Float[]> map;
            if (!((dataPoczatkowaWartosc==null) || (dataKoncowaWartosc==null) || (dataPoczatkowaWartosc.isAfter(dataKoncowaWartosc)) || (dataPoczatkowaWartosc.isEqual(dataKoncowaWartosc)) || dataKoncowaWartosc.isAfter(LocalDate.now()))) {
                if (!waluta1String.equals("pln") && waluta2String.equals("pln")) {
                    map = this.getFunctionImplNbp.getHistoricalCurrencyPol(waluta1String, dataPoczatkowaWartosc, dataKoncowaWartosc);
                } else if (!waluta1String.equals("pln") && !waluta2String.equals("pln")) {
                    map = this.getFunctionImplNbp.getHistoricalCurrency(waluta1String, waluta2String, dataPoczatkowaWartosc, dataKoncowaWartosc);
                } else {
                    map = this.getFunctionImplNbp.getHistoricalCurrencyPol(waluta2String, dataPoczatkowaWartosc, dataKoncowaWartosc);
                    map = this.getFunctionImplNbp.reverseCurrencyFloatArray(map);
                }

                ramkaDanych = new RamkaDanych(map, RamkaDanych.TypDanych.WALUTA);

                wykres.setVisible(false);
                zaktualizujWykresiTabele(waluta1String + " - " + waluta2String);

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
        }else{
            JOptionPane.showMessageDialog(
                    null,
                    "Bledne dane",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
            System.out.println("Bledne dane");
        }
    } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText(null); // Brak nagłówka
            alert.setContentText("Upss, coś poszło nie tak");
            alert.showAndWait();
        }
    }
}
