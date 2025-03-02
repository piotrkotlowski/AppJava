import java.net.http.HttpRequest;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.HashMap;

public class getFunctionsImplNbp extends getFunctionsImpl implements getFunctionsNbp{


    private String linkPolandCurr="https://api.nbp.pl/api/exchangerates/rates/c/{0}/today/?format=json";
    private String linkPolandHist="https://api.nbp.pl/api/exchangerates/rates/c/{0}/{1}/{2}/";


    private String stringHelperPolandCurr(String currency){
        return MessageFormat.format(linkPolandCurr, currency);
    }


    private Float[] getBidAndAsk(String data){
        int bidIndex = data.indexOf("\"bid\":") + 6; // 6 = length of `"bid":`
        String bidString = data.substring(bidIndex, bidIndex+6);
        float bid = Float.parseFloat(bidString);

        int askIndex = data.indexOf("\"ask\":") + 6; // 6 = length of `"ask":`
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

    private LocalDate stringHelperExtractingDate(String s){
        int dateIndex = s.indexOf("\"effectiveDate\":\"") + 17;
        int dateEndIndex = s.indexOf("\"", dateIndex);
        String effectiveDate = s.substring(dateIndex, dateEndIndex);
        return LocalDate.parse(effectiveDate);
    }

    private String stringHelperHistPol(String currency,LocalDate lowerDate,LocalDate upperDate){
        return MessageFormat.format(linkPolandHist,currency,lowerDate.toString(),upperDate.toString());
    }


    @Override
    public HashMap<LocalDate, Float[]> getHistoricalCurrencyPol(String currency,LocalDate lowerDate,LocalDate upperDate) {
        String url=stringHelperHistPol( currency, lowerDate, upperDate);
        HttpRequest request=this.requestBuilder(url);
        String data=receivingInfo(request);
        String[] dataLines = data.split("\\},\\{");
        HashMap<LocalDate,Float[]> resultMap=new HashMap<>();
        for(String s:dataLines){
            LocalDate localDate=stringHelperExtractingDate(s);
            if(localDate.isAfter(lowerDate) && localDate.isBefore(upperDate)){
             resultMap.put(localDate,getBidAndAsk(s));
        }}
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

    public HashMap<LocalDate, Float[]> getHistoricalCurrency(String currency1, String currency2, LocalDate lowerDate, LocalDate upperDate) {
        HashMap<LocalDate,Float[]> data1=this.getHistoricalCurrencyPol(currency1, lowerDate, upperDate);
        HashMap<LocalDate,Float[]> data2=this.getHistoricalCurrencyPol(currency2, lowerDate, upperDate);

        for (LocalDate key : data1.keySet()) {
            Float[] table1=data1.get(key);
            Float[] table2=data2.get(key);
            System.out.println(data1.get(key)[0]);
            table1[0]=table1[0]/table2[0];
            table1[1]=table1[1]/table2[1];
            System.out.println("Po zmianie" + data1.get(key)[0]);
        }
        return data1;
    }



    @Override
    public void DownloadDataPol(String currency, LocalDate lowerDate, LocalDate upperDate) {

    }

    @Override
    public void DownloadData(String currency1, String currency2, LocalDate lowerDate, LocalDate upperDate) {
        HashMap<LocalDate, Float[]> toCsv=this.getHistoricalCurrency( currency1,  currency2,  lowerDate,  upperDate);

        try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter("employee_data2.csv"), CSVFormat.DEFAULT)) {
            // Write header row
            csvPrinter.printRecord("Name", "Title", "Department", "Salary");

            // Write data row
            csvPrinter.printRecord(employeeData.get("Name"), employeeData.get("Title"), employeeData.get("Department"), employeeData.get("Salary"));
        } catch (IOException e) {
            e.printStackTrace();
        }

// Ensure the CSV file exists
        assertTrue(new File("employee_data2.csv").exists());
    }
}
