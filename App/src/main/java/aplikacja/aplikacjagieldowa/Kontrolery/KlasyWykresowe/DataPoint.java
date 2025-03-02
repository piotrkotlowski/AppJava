package aplikacja.aplikacjagieldowa.Kontrolery.KlasyWykresowe;

import java.time.LocalDate;

public class DataPoint {
    private final String x;
    private final double wartosc1;
    private final double wartosc2;

    public DataPoint(String x, double wartosc1, double wartosc2) {
        this.x = x;
        this.wartosc1 = wartosc1;
        this.wartosc2 = wartosc2;
    }

    public String getX() {
        return x;
    }

    public double getWartosc1() {
        return wartosc1;
    }

    public double getWartosc2() {
        return wartosc2;
    }
}
