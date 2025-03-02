import java.net.http.HttpRequest;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.stream.Collectors;


public class getFunctionsImplAlpha extends getFunctionsImpl implements getFunctionsAlpha {



    private  String linkCompanyCurr= "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol={0}&interval=5min&apikey=706DB3X90X6IKCR2&datatype=csv";
    private String linkCompanyHist="https://www.alphavantage.co/query?function={0}&symbol={1}&apikey=demo&datatype=csv";
    private String linkCommodityHist="https://www.alphavantage.co/query?function={0}&interval={1}&apikey=GA257DOZJDUESM5B&datatype=csv";





    private String stringHelperCompanyCurr(String companyTicker){
        return MessageFormat.format(linkCompanyCurr, companyTicker);
    }


    private String stringHelperCompanyHist(String companyTicker,String timestamp){
        String time = switch (timestamp) {
            case "monthly" -> "TIME_SERIES_MONTHLY";
            case "daily" -> "TIME_SERIES_DAILY";
            default -> "TIME_SERIES_WEEKLY";
        };
        return MessageFormat.format(linkCompanyHist, time,companyTicker);
    }
    private String stringHelperCommodityHist(String commodityName,String timestamp){
        return MessageFormat.format(linkCommodityHist, commodityName,timestamp);
    }



    @Override
    public String getCompanyHistoricData(String companyTicker,LocalDate UpperDate,LocalDate lowerDate,String time) {
        String url=stringHelperCompanyHist(companyTicker,time);
        HttpRequest request=this.requestBuilder(url);
        String data=receivingInfo(request);

        return data.lines().skip(1).filter(x-> (LocalDate.parse(x.substring(0,10)).isBefore(UpperDate) && (LocalDate.parse(x.substring(0,10)).isAfter(lowerDate))))
                .collect(Collectors.joining(""));


    }

    @Override
    public String getCompanyCurrentData(String companyTicker) {
        String url=stringHelperCompanyCurr(companyTicker);
        HttpRequest request=this.requestBuilder(url);
        String data=receivingInfo(request);
        int count= (int) data.lines().count();
        return data.lines().skip(count-1).collect(Collectors.joining());
    }

    @Override
    public String getCommodityHistoricData(String commodityTicker, LocalDate UpperDate, LocalDate lowerDate, String time) {
        String url=stringHelperCommodityHist(commodityTicker,time);
        HttpRequest request=this.requestBuilder(url);
        String data=receivingInfo(request);
        data.lines().forEach(System.out::println);
        return data.lines().skip(1).filter(x-> (LocalDate.parse(x.substring(0,10)).isBefore(UpperDate) && (LocalDate.parse(x.substring(0,10)).isAfter(lowerDate))))
                .collect(Collectors.joining(""));
    }

    @Override
    public void DownloadCompanyFile(String companyTicker, LocalDate UpperDate, LocalDate lowerDate, String time) {
    }
}
