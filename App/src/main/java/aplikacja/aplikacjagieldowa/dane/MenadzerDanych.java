package aplikacja.aplikacjagieldowa.dane;

import aplikacja.aplikacjagieldowa.backend.GetFunctionsImplAlpha;
import aplikacja.aplikacjagieldowa.backend.GetFunctionsImplNbp;

import javax.swing.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

public class MenadzerDanych {
    private final GetFunctionsImplAlpha getFunctionsImplAlpha = new GetFunctionsImplAlpha();
    private final GetFunctionsImplNbp getFunctionsImplNbp=new GetFunctionsImplNbp();
    public void downloadCommodity(String commodityName, LocalDate lower, LocalDate upper) {
       File file= fileChooser();
       if(file==null){
           JOptionPane.showMessageDialog(
                   null,
                   "Bledne dane",
                   "Warning",
                   JOptionPane.WARNING_MESSAGE
           );
       }else{
           if(!commodityName.equals("GOLD")) {
               this.getFunctionsImplAlpha.downloadCommodityFile(commodityName, lower, upper, "monthly", file);
           }else{
               this.getFunctionsImplNbp.downloadGoldFile("usd",lower,upper,file);
           }
           }
    }
    public void downloadCompany(String Company, LocalDate lower, LocalDate upper) {
        File file= fileChooser();
        if(file==null){
            JOptionPane.showMessageDialog(
                    null,
                    "Bledne dane",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
        }else{
            this.getFunctionsImplAlpha.downloadCompanyFile(Company,lower,upper,"monthly",file);
        }
    }

    public void downloadCurrency(String waluta1String, String waluta2String, LocalDate dataPoczatkowaWartosc, LocalDate dataKoncowaWartosc) {
        File file= fileChooser();
        if(file==null){
            JOptionPane.showMessageDialog(
                    null,
                    "Bledne dane",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
        }else{
            this.getFunctionsImplNbp.downloadDataCurr(waluta1String,waluta2String,dataPoczatkowaWartosc,dataKoncowaWartosc,file);
        }
    }


    private File fileChooser(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Create a Text File");
        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToCreate = fileChooser.getSelectedFile();
            if (!fileToCreate.getName().toLowerCase().endsWith(".txt")) {
                fileToCreate = new File(fileToCreate.getAbsolutePath() + ".txt");
            }
            try {

                if (fileToCreate.createNewFile()) {
                    JOptionPane.showMessageDialog(null,
                            "File created: " + fileToCreate.getAbsolutePath(),
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    return fileToCreate;
                } else {
                    JOptionPane.showMessageDialog(null,
                            "File already exists.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "An error occurred while creating the file.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "No file was selected.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        return null;
    }


    public RamkaDanych readCsv(String fileName, RamkaDanych.TypDanych datatype, LocalDate lower, LocalDate upper) {
        TreeMap<LocalDate, Float[]> data = new TreeMap<>();
        DateTimeFormatter formaterDat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String line;
        String delimeter = ",";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            line = bufferedReader.readLine();
            if (line == null) return null;

            while ((line = bufferedReader.readLine()) != null) {
                String[] strings = line.split(delimeter);
                Float[] floats = new Float[strings.length - 1];
                if (!(LocalDate.parse(strings[0], formaterDat).isBefore(lower) || LocalDate.parse(strings[0], formaterDat).isAfter(upper))) {
                    for (int indeks = 1; indeks < strings.length; indeks++) {
                        floats[indeks - 1] = Float.parseFloat(strings[indeks]);
                    }

                    data.put(LocalDate.parse(strings[0], formaterDat), floats);
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new RamkaDanych(data, datatype);
    }


    public static RamkaDanych wczytajPlikCSV(String sciezka, RamkaDanych.TypDanych typDanych) {
        TreeMap<LocalDate, Float[]> dane = new TreeMap<>();
        DateTimeFormatter formaterDat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String line;
        String delimeter = ",";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(sciezka))) {
            line = bufferedReader.readLine();
            if (line == null) return null;

            while ((line = bufferedReader.readLine()) != null) {
                String[] wartosci = line.split(delimeter);
                Float[] wartosciLiczbowe = new Float[wartosci.length - 1];

                for (int indeks = 1; indeks < wartosci.length; indeks++) {
                    wartosciLiczbowe[indeks - 1] = Float.parseFloat(wartosci[indeks]);
                }

                dane.put(LocalDate.parse(wartosci[0], formaterDat), wartosciLiczbowe);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new RamkaDanych(dane, typDanych);
    }



}
