import java.time.LocalDate;

public interface getFunctionsAlpha {
    String getCompanyHistoricData(String companyTicker, LocalDate UpperDate, LocalDate lowerDate,String time);
    String getCompanyCurrentData(String companyTicker);
    String getCommodityHistoricData(String commodityTicker, LocalDate UpperDate, LocalDate lowerDate,String time);
    void DownloadCompanyFile(String companyTicker, LocalDate UpperDate, LocalDate lowerDate,String time);
}
