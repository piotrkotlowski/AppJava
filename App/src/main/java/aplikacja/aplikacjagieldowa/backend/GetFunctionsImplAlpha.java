package aplikacja.aplikacjagieldowa.backend;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.TreeMap;


public class GetFunctionsImplAlpha extends GetFunctionsImpl implements GetFunctionsAlpha {



    private final String linkCompanyCurr= "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol={0}&interval=5min&apikey=706DB3X90X6IKCR2&datatype=csv";
    private final String linkCompanyHist="https://www.alphavantage.co/query?function={0}&symbol={1}&apikey=706DB3X90X6IKCR2&datatype=csv";
    private final String linkCommodityHist="https://www.alphavantage.co/query?function={0}&interval={1}&apikey=706DB3X90X6IKCR2&datatype=csv";
    private final String linkTickerChecker="https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords={0}&apikey=706DB3X90X6IKCR2&datatype=csv";

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
    public TreeMap<LocalDate, Float[]> getCompanyHistoricData(String companyTicker, LocalDate lowerDate,LocalDate UpperDate, String time) {
        if(UpperDate.isBefore(lowerDate)){
            LocalDate temp=lowerDate;
            lowerDate=UpperDate;
            UpperDate=temp;
        }
        LocalDate Up=UpperDate;
        LocalDate Dw=lowerDate;


        String url=stringHelperCompanyHist(companyTicker,time);
        HttpRequest request=this.requestBuilder(url);

        String data=receivingInfo(request);
        if (data == null) return new TreeMap<>();

        TreeMap<LocalDate,Float[]> resultmap=new TreeMap<>(new DateComperator());
        data.lines().skip(1).filter(x-> (LocalDate.parse(x.substring(0,10)).isBefore(Up) && (LocalDate.parse(x.substring(0,10)).isAfter(Dw))))
                .forEach(input->{
                    int startIndex = input.indexOf(",") + 1;
                    String numbersPart = input.substring(startIndex);
                    String[] numbers = numbersPart.split(",");
                    Float[] floats = Arrays.stream(numbers).map(Float::valueOf).toArray(Float[]::new);
                    resultmap.put(LocalDate.parse(input.substring(0,10)),floats);
                });
         return resultmap;


    }

    @Override
    public TreeMap<LocalDate, Float[]> getCommodityHistoricData(String commodityTicker, LocalDate UpperDate, LocalDate lowerDate, String time) {
        if(UpperDate.isBefore(lowerDate)){
            LocalDate temp=lowerDate;
            lowerDate=UpperDate;
            UpperDate=temp;
        }
        LocalDate Up=UpperDate;
        LocalDate Dw=lowerDate;

        String url=stringHelperCommodityHist(commodityTicker,time);
        HttpRequest request=this.requestBuilder(url);
        String data=receivingInfo(request);
        if (data == null) return new TreeMap<>();
        TreeMap<LocalDate,Float[]> resultmap=new TreeMap<>(new DateComperator());
        data.lines().skip(1).filter(x-> (LocalDate.parse(x.substring(0,10)).isBefore(Up) && (LocalDate.parse(x.substring(0,10)).isAfter(Dw))))
                .forEach(input->{
                    int startIndex = input.indexOf(",") + 1;
                    System.out.println(input);
                    float value;
                    if(!input.substring(startIndex).equals(".")){
                         value=Float.parseFloat(input.substring(startIndex));
                    }
                   else{
                       value= 0;
                    }
                    Float[] floats=new Float[1];
                    floats[0]=value;
                    resultmap.put(LocalDate.parse(input.substring(0,10)),floats);
                });
        return resultmap;

    }

    @Override
    public Float[] getCompanyCurrentData(String companyTicker) {
        String url=stringHelperCompanyCurr(companyTicker);
        HttpRequest request=this.requestBuilder(url);
        String data=receivingInfo(request);
        if (data == null) return  new Float[]{-1F, -1F};
        int count= (int) data.lines().count();
        final Float[][] floats = new Float[1][1];
        data.lines().skip(count-1).forEach(input->{
            int startIndex = input.indexOf(",") + 1;
            String numbersPart = input.substring(startIndex);
            String[] numbers = numbersPart.split(",");
            floats[0] = Arrays.stream(numbers).map(Float::valueOf).toArray(Float[]::new);
    });
    return floats[0];
    }
    @Override
    @FileGenerator
    public void downloadCompanyFile(String companyTicker, LocalDate lower, LocalDate upper,
                                    String time, File file) {

        if(upper.isBefore(lower)){
            LocalDate temp=lower;
            lower=upper;
            upper=temp;
        }
        TreeMap<LocalDate,Float[]> map=this.getCompanyHistoricData( companyTicker,  lower,  upper,  time);
        writeInFileCompany(file,map,false);


    }
    @FileGenerator
    private void writeInFileCompany(File fileName, TreeMap<LocalDate, Float[]> map, boolean append){

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,append))){
            writer.write(String.join(",","date","open","high","low","close","volume"));
            writer.newLine();
            for(LocalDate localDate : map.keySet()){
                writer.write(String.join(",",
                        localDate.toString(),
                        map.get(localDate)[0].toString(),
                        map.get(localDate)[1].toString(),
                        map.get(localDate)[2].toString(),
                        map.get(localDate)[3].toString(),
                        map.get(localDate)[4].toString()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FileGenerator
    private void writeInFileCommodity(File fileName, TreeMap<LocalDate, Float[]> toCsv, boolean append){

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,append))){
            if(!append){
            writer.write(String.join(",","date","value"));}
            writer.newLine();
            for(LocalDate localDate : toCsv.keySet()){
                writer.write(String.join(",",localDate.toString(),toCsv.get(localDate)[0].toString()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FileGenerator
    @Override
    public void downloadCommodityFile(String commodityTicker, LocalDate UpperDate, LocalDate lowerDate, String time,
                                      File file) {
        if(UpperDate.isBefore(lowerDate)){
            LocalDate temp=lowerDate;
            lowerDate=UpperDate;
            UpperDate=temp;
        }
        TreeMap<LocalDate,Float[]> map=this.getCommodityHistoricData( commodityTicker,  UpperDate,  lowerDate,  time);
        writeInFileCommodity(file,map,false);
    }




    private String stringHelperTicker(String s ){return MessageFormat.format(linkTickerChecker,s);}

    @Override
    public boolean tickerChecker(String ticker) {
        String url=stringHelperTicker(ticker);
        HttpRequest request=this.requestBuilder(url);
        String data=receivingInfo(request);
        if (data == null) return false;
        long a=data.lines().skip(1).map(s->{
            int Index = s.indexOf(",");
            System.out.println(s.substring(0,Index));
            return s.substring(0, Index).toUpperCase();
        }).filter(s->(s.equals(ticker.toUpperCase()))).count();
        return a != 0;
    }


}
