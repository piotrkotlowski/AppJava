package org.example;

import java.time.LocalDate;
import java.util.TreeMap;

public interface getFunctionsAlpha {
    TreeMap<LocalDate, Float[]> getCompanyHistoricData(String companyTicker, LocalDate UpperDate, LocalDate lowerDate, String time);
    Float[] getCompanyCurrentData(String companyTicker);
    TreeMap<LocalDate, Float[]> getCommodityHistoricData(String commodityTicker, LocalDate UpperDate, LocalDate lowerDate, String time);
    void DownloadCompanyFile(String companyTicker, LocalDate UpperDate, LocalDate lowerDate,String time,boolean exists,String fileName);
    void DownloadCommodityFile(String commodityTicker, LocalDate UpperDate, LocalDate lowerDate, String time,boolean exists,String fileName);
    boolean TickerChecker(String ticker);
}
