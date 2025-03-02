package org.example;

import java.time.LocalDate;
import java.util.TreeMap;

public interface getFunctionsNbp {
    Float[] getCurrentCurrencyPol(String currency);

    TreeMap<LocalDate, Float[]> getHistoricalCurrencyPol(String currency, LocalDate lowerDate, LocalDate upperDate);
    TreeMap<LocalDate, Float[]> getHistoricalGoldPol(LocalDate lowerDate, LocalDate upperDate);
    Float[] getCurrentCurrency(String currency1,String currency2);
    TreeMap<LocalDate, Float[]> getHistoricalCurrency(String currency1, String currency2, LocalDate lowerDate, LocalDate upperDate);
    TreeMap<LocalDate, Float[]> getHistoricalGold(String curr, LocalDate lowerDate, LocalDate upperDate);
    void DownloadDataCurr(String currency1, String currency2, LocalDate lowerDate, LocalDate upperDate,boolean poland,String fileName,boolean append);
    void DownloadGoldFile(String curr, LocalDate UpperDate, LocalDate lowerDate,
                               boolean exists,String fileName);
    TreeMap<LocalDate, Float[]> reverseCurrencyFloat(TreeMap<LocalDate, Float[]> map);
    TreeMap<LocalDate,Float[]> reverseCurrencyFloatArray(TreeMap<LocalDate,Float[]> map);
}
