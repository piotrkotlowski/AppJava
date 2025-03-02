import java.time.LocalDate;
import java.util.HashMap;

public interface getFunctionsNbp {
    Float[] getCurrentCurrencyPol(String currency);
    HashMap<LocalDate, Float[]> getHistoricalCurrencyPol(String currency,LocalDate lowerDate,LocalDate upperDate);
    Float[] getCurrentCurrency(String currency1,String currency2);
    HashMap<LocalDate, Float[]> getHistoricalCurrency(String currency1, String currency2, LocalDate lowerDate, LocalDate upperDate);
    void DownloadDataPol(String currency,LocalDate lowerDate,LocalDate upperDate);
    void DownloadData(String currency1, String currency2, LocalDate lowerDate, LocalDate upperDate);
}
