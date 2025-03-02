package aplikacja.aplikacjagieldowa.Kontrolery;

import aplikacja.aplikacjagieldowa.backend.GetFunctionsImplAlpha;
import aplikacja.aplikacjagieldowa.backend.GetFunctionsImplNbp;
import aplikacja.aplikacjagieldowa.dane.MenadzerDanych;
import aplikacja.aplikacjagieldowa.dane.RamkaDanych;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.stage.Stage;

import java.io.IOException;

import java.time.format.DateTimeFormatter;

public abstract class Kontroler {
    // Obsługa widoku
    static int apiCounter=0;
    private Stage stage;
    private Scene scene;
    private Parent root;
    // Do zamiany scen
    private FXMLLoader
            loaderFirm = new FXMLLoader(getClass().getResource("PrzegladarkaFirm.fxml")),
            loaderWalut = new FXMLLoader(getClass().getResource("PrzegladarkaWalut.fxml")),
            loaderSurowcow = new FXMLLoader(getClass().getResource("PrzegladarkaSurowcow.fxml")),
            loaderAutorow =  new FXMLLoader(getClass().getResource("Autorzy.fxml"));
    // Kontrolery
    private KontrolerFirmowy kontrolerFirmowy;
    private KontrolerWalutowy kontrolerWalutowy;
    private KontrolerSurowcow kontrolerSurowcow;
    protected GetFunctionsImplNbp getFunctionImplNbp=new GetFunctionsImplNbp();
    protected GetFunctionsImplAlpha getFunctionsImplAlpha=new GetFunctionsImplAlpha();
    protected MenadzerDanych DataManager=new MenadzerDanych();
    protected String waluta = " usd";
    protected RamkaDanych ramkaDanych;
    protected DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    protected boolean ApiChecker(){
        return apiCounter<25;
    }


    public void zmienNaFirmy(ActionEvent event) throws IOException{
        root = loaderFirm.load();
        stage = (Stage) ((RadioMenuItem) event.getSource()).getParentPopup().getOwnerNode().getScene().getWindow();;
        scene = new Scene(root);

        kontrolerFirmowy = loaderFirm.getController();
        kontrolerFirmowy.przygotujTabele();

        stage.setTitle("Przeglądarka giełdy firm oraz surowców");
        stage.setScene(scene);
        stage.show();
    }

    public void zmienNaWaluty(ActionEvent event) throws IOException{
        root = loaderWalut.load();
        stage = (Stage) ((RadioMenuItem) event.getSource()).getParentPopup().getOwnerNode().getScene().getWindow();;
        scene = new Scene(root);

        kontrolerWalutowy = loaderWalut.getController();
        kontrolerWalutowy.przygotujTabele();

        stage.setTitle("Przeglądarka giełdy walutowej");
        stage.setScene(scene);
        stage.show();
    }

    public void zmienNaSurowce(ActionEvent event) throws IOException{
        root = loaderSurowcow.load();
        stage = (Stage) ((RadioMenuItem) event.getSource()).getParentPopup().getOwnerNode().getScene().getWindow();;
        scene = new Scene(root);
        kontrolerSurowcow = loaderSurowcow.getController();
        kontrolerSurowcow.przygotujTabele();

        stage.setTitle("Przeglądarka giełdy surowców");
        stage.setScene(scene);
        stage.show();
    }

    public void zmienNaAutorow(ActionEvent event) throws IOException{
        root = loaderAutorow.load();
        stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerNode().getScene().getWindow();;
        scene = new Scene(root);

        stage.setTitle("Informacja o autorach");
        stage.setScene(scene);
        stage.show();
    }

    public void zmienOkno(ActionEvent actionEvent, boolean czyFirmy, boolean czyWaluty, boolean czySurowce) throws IOException {
        if (czyFirmy) {
            zmienNaFirmy(actionEvent);
        } else if (czyWaluty){
            zmienNaWaluty(actionEvent);
        } else {
            zmienNaSurowce(actionEvent);
        }
    }





}