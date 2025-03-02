package aplikacja.aplikacjagieldowa.Kontrolery;

import aplikacja.aplikacjagieldowa.backend.DateComperator;
import aplikacja.aplikacjagieldowa.dane.MenadzerDanych;
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
import java.util.*;

public class KontrolerSurowcow extends Kontroler {
    @FXML
    ChoiceBox<String> wyborSurowca;
    @FXML
    CheckBox sprawdzenieCzyDaneAktualne;
    @FXML
    DatePicker dataPoczatkowa, dataKoncowa;
    @FXML
    Button przyciskWyszukiwania;
    @FXML
    TableView<Map.Entry<LocalDate, Float[]>> tabela;
    @FXML
    LineChart<String, Number> wykres;
    @FXML
    RadioMenuItem czyWaluty, czyFirmy, czySurowce;
    @FXML
    TableColumn<Map.Entry<LocalDate, Float[]>, String> kolumnaDat,kolumnaCena;
    @FXML
    Label surowiecEtykietka, cenaAktualnaEtykietka;
    private final MenadzerDanych DataManager=new MenadzerDanych();
    private List<String> commoditiesList = Arrays.asList("WHEAT","NATURAL_GAS","WTI","GOLD","CORN","COTTON","SUGAR","COFFEE","ALUMINUM","GOLD");
    public void przygotujTabele(){
        wyborSurowca.getItems().addAll(commoditiesList);
        kolumnaDat.setCellValueFactory(dana -> new javafx.beans.property.SimpleObjectProperty<>(dana.getValue().getKey().format(dateTimeFormatter)));
        kolumnaCena.setCellValueFactory(dana -> new javafx.beans.property.SimpleObjectProperty<>(dana.getValue().getValue()[0] + waluta));
        kolumnaDat.setStyle("-fx-alignment: center;");
        kolumnaCena.setStyle("-fx-alignment: center;");
    }
    public void zmienOkno(ActionEvent actionEvent) throws IOException {
        if (czySurowce.isSelected()) {
            czySurowce.setSelected(false);
        } else{
            super.zmienOkno(actionEvent, czyFirmy.isSelected(), czyWaluty.isSelected(), czySurowce.isSelected());
        }
    }
    @Override
    public void zmienNaAutorow(ActionEvent event) throws IOException {
        super.zmienNaAutorow(event);
    }

    public void downloadButtonClicked() {
        try {
            if (ApiChecker()) {
                String nazwaSurowca = wyborSurowca.getValue();
                LocalDate dataPoczatkowaWartosc = dataPoczatkowa.getValue();
                LocalDate dataKoncowaWartosc = dataKoncowa.getValue();


                if (nazwaSurowca == null) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Podaj surowiec",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE

                    );
                } else if (
                        (dataPoczatkowaWartosc == null) ||
                                (dataKoncowaWartosc == null) ||
                                (dataPoczatkowaWartosc.isAfter(dataKoncowaWartosc)) ||
                                (dataPoczatkowaWartosc.isEqual(dataKoncowaWartosc)) ||
                                dataKoncowaWartosc.isAfter(LocalDate.now())
                ) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Podaj dobra date",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    apiCounter += 1;
                    this.DataManager.downloadCommodity(nazwaSurowca, dataPoczatkowaWartosc, dataKoncowaWartosc);

                }
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Limit na API zostal wyczerpany, zostaje tylko strona z walutami",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText(null); // Brak nagłówka
            alert.setContentText("Upss, coś poszło nie tak");
            alert.showAndWait();
        }
    }
        public void zaktualizujWykresiTabele (String surowiec){
            CategoryAxis xAxis = (CategoryAxis) wykres.getXAxis();
            NumberAxis yAxis = (NumberAxis) wykres.getYAxis();

            xAxis.setLabel("Data");
            yAxis.setLabel("Cena");

            wykres.setTitle("Wykres przedstawiający historię ceny surowca: " + surowiec);
            wykres.getData().clear();
            wykres.setLegendVisible(false);

            XYChart.Series<String, Number> cenaLine = new XYChart.Series<>();
            cenaLine.setName("Cena sprzedaży");

            float minimalnaCenaPerDane = Float.POSITIVE_INFINITY, maksymalnaCenaPerDane = 0;

            tabela.getItems().clear();

            for (Map.Entry<LocalDate, Float[]> wiersz : ramkaDanych.getRekordy().entrySet()) {
                tabela.getItems().add(wiersz);
                String data = wiersz.getKey().format(dateTimeFormatter);
                Float cena = wiersz.getValue()[0];

                if (cena < minimalnaCenaPerDane) minimalnaCenaPerDane = cena;
                if (cena > maksymalnaCenaPerDane) maksymalnaCenaPerDane = cena;

                cenaLine.getData().add(new XYChart.Data<>(data, cena));
            }

            float margines = (maksymalnaCenaPerDane - minimalnaCenaPerDane) * 0.05F;
            yAxis.setAutoRanging(false);
            yAxis.setUpperBound(Math.round((maksymalnaCenaPerDane + margines) * 100) / 100F);
            yAxis.setLowerBound(Math.round((minimalnaCenaPerDane - margines) * 100) / 100F);

            wykres.getData().add(cenaLine);

            cenaLine.getData().forEach(dataa -> dataa.getNode().setStyle("-fx-background-radius: 0; -fx-padding: 0; -fx-opacity: 0;"));
            cenaLine.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #d90f9c   ; -fx-stroke-width: 2;");
    }

    public void akcjaPrzycisku() {
        try {
            if (ApiChecker()) {
                String nazwaSurowca = wyborSurowca.getValue();
                if (wyborSurowca != null) {
                    if (sprawdzenieCzyDaneAktualne.isSelected()) {
                        LocalDate lc = LocalDate.of(2020, 1, 1);

                        Float aktualnaCena;
                        if (!nazwaSurowca.equals("GOLD")) {
                            TreeMap<LocalDate, Float[]> map = this.getFunctionsImplAlpha.getCommodityHistoricData(nazwaSurowca, lc, LocalDate.now(), "monthly");
                            if (map.size() == 0) return;
                            aktualnaCena = map.get(Collections.max(map.keySet(), new DateComperator()))[0];
                        } else {
                            aktualnaCena = this.getFunctionImplNbp.getCurrentGold();
                        }

                        cenaAktualnaEtykietka.setText(aktualnaCena + waluta);
                        surowiecEtykietka.setText(nazwaSurowca);
                        return;
                    }

                    LocalDate dataPoczatkowaWartosc = dataPoczatkowa.getValue();
                    LocalDate dataKoncowaWartosc = dataKoncowa.getValue();


                    if (!((dataPoczatkowaWartosc == null) || (dataKoncowaWartosc == null)
                            || (dataPoczatkowaWartosc.isAfter(dataKoncowaWartosc)) || (dataPoczatkowaWartosc.isEqual(dataKoncowaWartosc)) || (dataKoncowaWartosc.isAfter(LocalDate.now())))) {
                        if (nazwaSurowca.equals("GOLD")) {
                            TreeMap<LocalDate, Float[]> map = this.getFunctionImplNbp.getHistoricalGold("usd", dataPoczatkowaWartosc, dataKoncowaWartosc);
                            ramkaDanych = new RamkaDanych(map, RamkaDanych.TypDanych.SUROWIEC);
                        } else {
                            TreeMap<LocalDate, Float[]> map = this.getFunctionsImplAlpha.getCommodityHistoricData(nazwaSurowca, dataPoczatkowaWartosc, dataKoncowaWartosc, "monthly");
                            ramkaDanych = new RamkaDanych(map, RamkaDanych.TypDanych.SUROWIEC);
                        }

                        wykres.setVisible(false);
                        zaktualizujWykresiTabele(nazwaSurowca);
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
                        }
                        ).start();
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
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText(null); // Brak nagłówka
            alert.setContentText("Upss, coś poszło nie tak");
            alert.showAndWait();
        }
    }

}
