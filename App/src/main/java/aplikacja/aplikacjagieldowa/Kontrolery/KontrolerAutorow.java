package aplikacja.aplikacjagieldowa.Kontrolery;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class KontrolerAutorow extends Kontroler {
    @FXML
    RadioMenuItem czyWaluty, czyFirmy, czySurowce;

    public void zmienOkno(ActionEvent actionEvent) throws IOException {
        super.zmienOkno(actionEvent, czyFirmy.isSelected(), czyWaluty.isSelected(), czySurowce.isSelected());
    }
}
