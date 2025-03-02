module aplikacja.aplikacjagieldowa {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.net.http;
    requires jdk.jshell;


    opens aplikacja.aplikacjagieldowa to javafx.fxml;
    exports aplikacja.aplikacjagieldowa;
    exports aplikacja.aplikacjagieldowa.Kontrolery;
    exports aplikacja.aplikacjagieldowa.testy;
    opens aplikacja.aplikacjagieldowa.Kontrolery to javafx.fxml;
}