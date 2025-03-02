package aplikacja.aplikacjagieldowa;

import aplikacja.aplikacjagieldowa.Kontrolery.KontrolerFirmowy;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Aplikacja extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PrzegladarkaFirm.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        KontrolerFirmowy kontrolerFirmowy = fxmlLoader.getController();
        kontrolerFirmowy.przygotujTabele();


        stage.setTitle("Przeglądarka giełdy firm oraz surowców");
        stage.setResizable(false);


        stage.setScene(scene);
        stage.show();

    }
    public static void main(String[] args) {

        launch();
    }
}