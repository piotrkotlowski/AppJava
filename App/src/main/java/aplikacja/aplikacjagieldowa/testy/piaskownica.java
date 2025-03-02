package aplikacja.aplikacjagieldowa.testy;

import aplikacja.aplikacjagieldowa.dane.MenadzerDanych;
import aplikacja.aplikacjagieldowa.dane.RamkaDanych;

public class piaskownica {
    public static void main(String... args){
        MenadzerDanych menadzerDanych = new MenadzerDanych();

        RamkaDanych ramkaDanych = MenadzerDanych.wczytajPlikCSV("C:\\Users\\ludwi\\OneDrive\\Pulpit\\STUDIA\\2024-2025 Semestr_03\\Zaawansowane_Programowanie_Obiektowe\\Projekt_2\\AplikacjaGieldowa\\src\\main\\java\\aplikacja\\aplikacjagieldowa\\dane\\try.txt", RamkaDanych.TypDanych.SUROWIEC);
        System.out.println(ramkaDanych);
    }
}
