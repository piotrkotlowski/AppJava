package aplikacja.aplikacjagieldowa.backend;

import java.io.File;
import java.time.LocalDate;
import java.util.TreeMap;

public interface GetFunctionsAlpha {
    TreeMap<LocalDate, Float[]> getCompanyHistoricData(String companyTicker, LocalDate UpperDate, LocalDate lowerDate, String time);
    Float[] getCompanyCurrentData(String companyTicker);
    TreeMap<LocalDate, Float[]> getCommodityHistoricData(String commodityTicker, LocalDate UpperDate, LocalDate lowerDate, String time);
    void downloadCompanyFile(String companyTicker, LocalDate UpperDate, LocalDate lowerDate,
                             String time, File file);
    void downloadCommodityFile(String commodityTicker, LocalDate UpperDate, LocalDate lowerDate, String time,
                               File file);
    boolean tickerChecker(String ticker);
}
