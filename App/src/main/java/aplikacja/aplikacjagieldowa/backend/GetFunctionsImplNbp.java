package aplikacja.aplikacjagieldowa.backend;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class GetFunctionsImplNbp extends GetFunctionsImpl implements GetFunctionsNbp {
    private final  String linkPolandHist="https://api.nbp.pl/api/exchangerates/rates/c/{0}/{1}/{2}/";
    private final String linkGold="https://api.nbp.pl/api/cenyzlota/{0}/{1}";
    private final String CurrentGold="https://api.nbp.pl/api/cenyzlota";
    private final String LinkUsd="https://api.nbp.pl/api/exchangerates/rates/c/usd";




    private String stringHelpergGold(LocalDate lowerDate,LocalDate upperDate){
        return MessageFormat.format(linkGold,lowerDate.toString(),upperDate.toString());
    }
    private Float[] getValueGold(String s ){
        int dateIndex = s.indexOf("cena");
        Float[] floats=new Float[1];
        floats[0]=Float.parseFloat(s.substring(dateIndex+6, dateIndex+12));
        return floats;
    }
    @Override
    public Float getCurrentGold(){
        HttpRequest request=this.requestBuilder(CurrentGold);
        String data=receivingInfo(request);
        if (data == null) return -1F;
        Float gold=stringHelperGold(data);
        HttpRequest request1=this.requestBuilder(LinkUsd);
        String data1=receivingInfo(request1);
        if (data1 == null) return -1F;
        Float[] floats=getBidAndAsk(data1);

        return gold/((floats[0]+floats[1])/2);
    }
    private float stringHelperGold(String s){
        String key = "\"cena\":";
        int startIndex = s.indexOf(key) + key.length();
        int endIndex = s.indexOf("}", startIndex);

        String cenaValue = s.substring(startIndex, endIndex).trim();
        return Float.parseFloat(cenaValue);
    }

    private LocalDate stringHelperExtractingDateGold(String s ){
        int dateIndex = s.indexOf(":\"");
        String effectiveDate = s.substring(dateIndex+2, dateIndex+12);
        return LocalDate.parse(effectiveDate);
    }
    @Override
    public TreeMap<LocalDate, Float[]> getHistoricalGold(String curr, LocalDate lowerDate, LocalDate upperDate){
            TreeMap<LocalDate,Float[]> mapGold=this.getHistoricalGoldPol(lowerDate,upperDate);
            TreeMap<LocalDate,Float[]> mapPolandCurrency=this.getHistoricalCurrencyPol(curr,lowerDate,upperDate);
            for(LocalDate lc:mapGold.keySet()){
                Float a=mapGold.get(lc)[0];
                Float avg=(mapPolandCurrency.get(lc)[0]+mapPolandCurrency.get(lc)[1])/2;
                Float[] floats=new Float[1];
                floats[0]=a/avg;
                mapGold.put(lc,floats);
            }
            return mapGold;
    }

    @FileGenerator
    @Override
    public void downloadGoldFile(String curr, LocalDate UpperDate, LocalDate lowerDate,
                                 File fileName) {
        if (UpperDate.isBefore(lowerDate)) {
            LocalDate temp = lowerDate;
            lowerDate = UpperDate;
            UpperDate = temp;
        }
        TreeMap<LocalDate, Float[]> map;
        if (curr.equals("pln")) {
            map = this.getHistoricalGoldPol(UpperDate, lowerDate);
        }
            else{
                 map = this.getHistoricalGold(curr,UpperDate, lowerDate);
            }
            writeInFileGold(fileName,map,false);
    }

    public Float[] reverseCurrency(Float[] floats){
        Float[] result=new Float[2];
        result[0]=1/floats[0];
        result[1]=1/floats[1];
        return result;
    }


    public TreeMap<LocalDate, Float[]> reverseCurrencyFloatArray(TreeMap<LocalDate, Float[]> map) {
        for(LocalDate lc:map.keySet()){
            Float[] floats = map.get(lc);
            floats[0]=1/floats[0];
            floats[1]=1/floats[1];
            map.put(lc,floats);
        }
        return map;
    }
    @FileGenerator
    private void writeInFileGold(File fileName, TreeMap<LocalDate, Float[]> toCsv, boolean append){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,append))){
            writer.write(String.join(",", "date", "price"));
            writer.newLine();
            for(LocalDate localDate : toCsv.keySet()){
                writer.write(String.join(",",localDate.toString(),toCsv.get(localDate)[0].toString()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public TreeMap<LocalDate, Float[]> getHistoricalGoldPol(LocalDate lowerDate, LocalDate UpperDate) {
        TreeMap<LocalDate,Float[]> resultMap=new TreeMap<>(new DateComperator());
        if(UpperDate.isBefore(lowerDate)){
            LocalDate temp=lowerDate;
            lowerDate=UpperDate;
            UpperDate=temp;
        }

        long daysBetween = ChronoUnit.DAYS.between(lowerDate, UpperDate);
        if (daysBetween<366){
            String url=stringHelpergGold(lowerDate,UpperDate);
            HttpRequest request=this.requestBuilder(url);
            String data=receivingInfo(request);

            if (data == null) return new TreeMap<>();
            String[] dataLines = data.split("\\},\\{");
            for(String s:dataLines){

                LocalDate localDate= stringHelperExtractingDateGold(s);
                if(localDate.isAfter(lowerDate) && localDate.isBefore(UpperDate)){
                    resultMap.put(localDate,getValueGold(s));
                }}

        }
        else{

            while (lowerDate.isBefore(UpperDate)){
                LocalDate newDate;

                if (ChronoUnit.DAYS.between(lowerDate, UpperDate) <366){
                    newDate=UpperDate;
                }else{
                    newDate=lowerDate.plusDays(366);

                }
                String url=stringHelpergGold(lowerDate,newDate);
                HttpRequest request=this.requestBuilder(url);
                String data=receivingInfo(request);

                if (data == null) return new TreeMap<>();
                String[] dataLines = data.split("\\},\\{");

                for(String s:dataLines){

                    LocalDate localDate= stringHelperExtractingDateGold(s);
                    if(localDate.isAfter(lowerDate) && localDate.isBefore(UpperDate)){
                        resultMap.put(localDate,getValueGold(s));
                    }}
                lowerDate=newDate;
            }
        }

        return resultMap;

    }


    private LocalDate stringHelperExtractingDateCurrency(String s){
        System.out.println(s);
        int dateIndex = s.indexOf("\"effectiveDate\":\"") + 17;
        int dateEndIndex = s.indexOf("\"", dateIndex);
        String effectiveDate = s.substring(dateIndex, dateEndIndex);
        return LocalDate.parse(effectiveDate);
    }



    private Float[] getBidAndAsk(String data){
        System.out.println(data);
        int bidIndex = data.indexOf("\"bid\":") + 6;
        String bidString = data.substring(bidIndex, bidIndex+6);
        float bid = Float.parseFloat(bidString);

        int askIndex = data.indexOf("\"ask\":") + 6;
        String askString = data.substring(askIndex, askIndex+6);
        float ask = Float.parseFloat(askString);

        Float[] result=new Float[2];
        result[1]=ask;
        result[0]=bid;
        return result;
    }
    @Override
    public Float[] getCurrentCurrencyPol(String currency) {
        LocalDate lower=LocalDate.of(2024,12,30);
        TreeMap<LocalDate,Float[]> map=this.getHistoricalCurrencyPol(currency,lower,LocalDate.now());
        if (map.size() == 0) return new Float[]{-1F, -1F, -1F, -1F};
        System.out.println(Collections.max(map.keySet(),new DateComperator()));
        return map.get(Collections.max(map.keySet(),new DateComperator()));

    }



    private String stringHelperHistPol(String currency,LocalDate lowerDate,LocalDate upperDate){
        return MessageFormat.format(linkPolandHist,currency,lowerDate.toString(),upperDate.toString());
    }


    @Override
    public TreeMap<LocalDate, Float[]> getHistoricalCurrencyPol(String currency, LocalDate lowerDate, LocalDate upperDate) {
        TreeMap<LocalDate,Float[]> resultMap=new TreeMap<>(new DateComperator());
        if(upperDate.isBefore(lowerDate)){
            LocalDate temp=lowerDate;
            lowerDate=upperDate;
            upperDate=temp;
        }
        long daysBetween = ChronoUnit.DAYS.between(lowerDate, upperDate);
        if (daysBetween<366){

        String url=stringHelperHistPol( currency, lowerDate, upperDate);
        HttpRequest request=this.requestBuilder(url);
        String data=receivingInfo(request);

        if (data == null) return new TreeMap<>();

        String[] dataLines = data.split("\\},\\{");

        for(String s:dataLines){
            LocalDate localDate= stringHelperExtractingDateCurrency(s);
            if(localDate.isAfter(lowerDate) && localDate.isBefore(upperDate)){
             resultMap.put(localDate,getBidAndAsk(s));
        }}

        }
        else{
            while (lowerDate.isBefore(upperDate)){
                LocalDate newDate;
                if (ChronoUnit.DAYS.between(lowerDate, upperDate) <366){
                     newDate=upperDate;
                }else{
                     newDate=lowerDate.plusDays(366);

                }
                String url=stringHelperHistPol( currency, lowerDate, newDate);
                HttpRequest request=this.requestBuilder(url);
                String data=receivingInfo(request);

                if (data == null) return new TreeMap<>();

                String[] dataLines = data.split("\\},\\{");

                for(String s:dataLines){
                    LocalDate localDate= stringHelperExtractingDateCurrency(s);
                    if(localDate.isAfter(lowerDate) && localDate.isBefore(upperDate)){
                        resultMap.put(localDate,getBidAndAsk(s));
                    }}
                lowerDate=newDate;
            }
        }

        return resultMap;

    }



    @Override
    public Float[] getCurrentCurrency(String currency1,String currency2) {
        Float[] data1=this.getCurrentCurrencyPol(currency1);
        Float[] data2=this.getCurrentCurrencyPol(currency2);

        Float[] result_data=new Float[2];
        result_data[0]=data1[0]/data2[0];
        result_data[1]=data1[1]/data2[1];
        return result_data;
    }
@Override
    public TreeMap<LocalDate, Float[]> getHistoricalCurrency(String currency1, String currency2, LocalDate lowerDate, LocalDate upperDate) {
        if(upperDate.isBefore(lowerDate)){
            LocalDate temp=lowerDate;
            lowerDate=upperDate;
            upperDate=temp;
        }
        TreeMap<LocalDate,Float[]> data1=this.getHistoricalCurrencyPol(currency1, lowerDate, upperDate);
        TreeMap<LocalDate,Float[]> data2=this.getHistoricalCurrencyPol(currency2, lowerDate, upperDate);
        for (LocalDate key : data1.keySet()) {
            Float[] table1=data1.get(key);
            Float[] table2=data2.get(key);

            table1[0]=table1[0]/table2[0];
            table1[1]=table1[1]/table2[1];

        }
        return data1;
    }
    @Override
    @FileGenerator
    public void downloadDataCurr(String currency1, String currency2, LocalDate lowerDate, LocalDate UpperDate, File file){
        if(UpperDate.isBefore(lowerDate)){
            LocalDate temp=lowerDate;
            lowerDate=UpperDate;
            UpperDate=temp;
        }
        TreeMap<LocalDate,Float[]> map;
        if (!currency1.equals("pln") && currency2.equals("pln")) {
            map=this.getHistoricalCurrencyPol(currency1,lowerDate,UpperDate);
;        } else if ((!currency1.equals("pln") && !currency2.equals("pln"))) {
            map=this.getHistoricalCurrency(currency1,currency2,lowerDate,UpperDate);
        }else{
            map=this.getHistoricalCurrencyPol(currency2,lowerDate,UpperDate);
            map=this.reverseCurrencyFloatArray(map);
        }
        writeInFile(file,map,false);
    }
    @FileGenerator
    private void writeInFile(File fileName, TreeMap<LocalDate, Float[]> map,boolean append){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,append))){
            writer.write(String.join(",", "date", "bid", "ask"));
            writer.newLine();
            for(LocalDate localDate : map.keySet()){
                writer.write(String.join(",",localDate.toString(),map.get(localDate)[0].toString(),map.get(localDate)[1].toString()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
