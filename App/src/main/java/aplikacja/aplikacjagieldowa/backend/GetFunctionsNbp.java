package aplikacja.aplikacjagieldowa.backend;

import java.io.File;
import java.time.LocalDate;
import java.util.TreeMap;

public interface GetFunctionsNbp {
    Float[] getCurrentCurrencyPol(String currency);

    TreeMap<LocalDate, Float[]> getHistoricalCurrencyPol(String currency, LocalDate lowerDate, LocalDate upperDate);
    TreeMap<LocalDate, Float[]> getHistoricalGoldPol(LocalDate lowerDate, LocalDate upperDate);
    Float[] getCurrentCurrency(String currency1,String currency2);
    TreeMap<LocalDate, Float[]> getHistoricalCurrency(String currency1, String currency2, LocalDate lowerDate, LocalDate upperDate);
    TreeMap<LocalDate, Float[]> getHistoricalGold(String curr, LocalDate lowerDate, LocalDate upperDate);
    Float getCurrentGold();
    void downloadDataCurr(String currency1, String currency2, LocalDate lowerDate, LocalDate UpperDate, File file);
    void downloadGoldFile(String curr, LocalDate UpperDate, LocalDate lowerDate,
                          File fileName);
}
