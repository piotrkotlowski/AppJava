package aplikacja.aplikacjagieldowa.dane;

import javafx.util.Pair;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RamkaDanych {
    private static String[][] nazwyKolumnDoWyboru = {
            {"DATE", "OPEN", "HIGH", "LOW", "CLOSE", "VOLUME"},
            {"DATE", "BID", "ASK"},
            {"DATE", "VALUE"}
    };

    private String[] nazwyKolumn;
    public enum TypDanych{
        FIRMA, WALUTA, SUROWIEC
    }
    private TreeMap<LocalDate, Float[]> rekordy;
    private TypDanych typDanych;


    public RamkaDanych(TreeMap<LocalDate, Float[]> dane, TypDanych typDanych) {
        switch (typDanych){
            case FIRMA -> this.nazwyKolumn = nazwyKolumnDoWyboru[0];
            case WALUTA -> this.nazwyKolumn = nazwyKolumnDoWyboru[1];
            case SUROWIEC -> this.nazwyKolumn = nazwyKolumnDoWyboru[2];
        }

        if (dane != null){
            int porzadanaDlugosc = nazwyKolumn.length-1;
            for (Map.Entry<LocalDate, Float[]> entry : dane.entrySet()){
                if (entry.getValue().length != porzadanaDlugosc){
                    throw new RuntimeException("Wybrałeś zły typ danych, albo Twoje dane sa kulawe");
                }
            }
        }


        this.rekordy = dane;
        this.typDanych = typDanych;

    }


    public Map<LocalDate, Float[]> getRekordy() {
        return rekordy;
    }

    @Override
    public String toString() {
        DateTimeFormatter formaterDat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        StringBuilder toReturn = new StringBuilder();
        toReturn.append(nazwyKolumn[0]);
        for (int i = 1; i < nazwyKolumn.length; i++){
            toReturn.append(", "+nazwyKolumn[i]);
        }
        toReturn.append("\n");
        for (LocalDate data : rekordy.keySet()){
            StringBuilder wiersz = new StringBuilder(data.format(formaterDat));
            for (Float liczba : rekordy.get(data)){
                wiersz.append(", "+liczba);
            }
            wiersz.append("\n");
            toReturn.append(wiersz);
        }
        return toReturn.toString();
    }
}
