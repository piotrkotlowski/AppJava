package org.example;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class getFunctionsImplNbp extends getFunctionsImpl implements getFunctionsNbp{


    private final String linkPolandCurr="https://api.nbp.pl/api/exchangerates/rates/c/{0}/today/?format=json";
    private final  String linkPolandHist="https://api.nbp.pl/api/exchangerates/rates/c/{0}/{1}/{2}/";
    private final String linkGold="https://api.nbp.pl/api/cenyzlota/{0}/{1}";






    private String stringHelpergGold(LocalDate lowerDate,LocalDate upperDate){
        return MessageFormat.format(linkGold,lowerDate.toString(),upperDate.toString());
    }
    private Float[] getValueGold(String s ){
        int dateIndex = s.indexOf("cena");
        Float[] floats=new Float[1];
        floats[0]=Float.parseFloat(s.substring(dateIndex+6, dateIndex+12));
        return floats;
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
    public void DownloadGoldFile(String curr, LocalDate UpperDate, LocalDate lowerDate,
                                      boolean exists,String fileName) {
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
            if (!exists) {
                writeInFileGold(fileName, map, false);
            } else {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Pliki", "txt", "csv");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(null);
                writeInFileGold(chooser.getSelectedFile().getName(), map, true);
            }
    }

    @Override
    public TreeMap<LocalDate, Float[]> reverseCurrencyFloat(TreeMap<LocalDate, Float[]> map) {
        for(LocalDate lc:map.keySet()){
            float a = map.get(lc)[0];
            Float[] floats=new Float[1];
            floats[0]=1/a;
            map.put(lc,floats);
        }
        return map;
    }

    @Override
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
    private void writeInFileGold(String fileName, TreeMap<LocalDate, Float[]> toCsv, boolean append){
        if(!append){
            fileName=fileName+".txt";
        }
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
                String[] dataLines = data.split("\\},\\{");

                for(String s:dataLines){
                    System.out.println(s);
                    LocalDate localDate= stringHelperExtractingDateGold(s);
                    if(localDate.isAfter(lowerDate) && localDate.isBefore(UpperDate)){
                        resultMap.put(localDate,getValueGold(s));
                    }}
                lowerDate=newDate;
            }
        }

        return resultMap;

    }


    private String stringHelperPolandCurr(String currency){
        return MessageFormat.format(linkPolandCurr, currency);
    }

    private LocalDate stringHelperExtractingDateCurrency(String s){
        int dateIndex = s.indexOf("\"effectiveDate\":\"") + 17;
        int dateEndIndex = s.indexOf("\"", dateIndex);
        String effectiveDate = s.substring(dateIndex, dateEndIndex);
        return LocalDate.parse(effectiveDate);
    }



    private Float[] getBidAndAsk(String data){
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
        String url=stringHelperPolandCurr(currency);
        HttpRequest request=this.requestBuilder(url);
        String data=receivingInfo(request);
        return getBidAndAsk(data);
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


    @FileGenerator
    @Override
    public void DownloadDataCurr(String currency1, String currency2, LocalDate lowerDate, LocalDate upperDate,boolean poland,String fileName,boolean append){
        TreeMap<LocalDate, Float[]> map;
        if (!poland) {
            map = this.getHistoricalCurrency(currency1, currency2, lowerDate, upperDate);
        }else{
            map=this.getHistoricalCurrencyPol(currency1, lowerDate, upperDate);
        }
        if(append) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Pliki", "txt", "csv");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            writeInFile(chooser.getSelectedFile().getName(), map, true);
        }else{
            writeInFile(fileName,map,false);
        }
    }
    @FileGenerator
    private void writeInFile(String fileName, TreeMap<LocalDate, Float[]> toCsv, boolean append){
        if(!append){
            fileName=fileName+".txt";
        }
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,append))){
            if(!append) {
                writer.write(String.join(",", "date", "bid", "ask"));
            }
            writer.newLine();
            for(LocalDate localDate : toCsv.keySet()){
                writer.write(String.join(",",localDate.toString(),toCsv.get(localDate)[0].toString(),toCsv.get(localDate)[1].toString()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
