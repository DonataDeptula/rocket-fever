/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rf;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Słownik wszystkich słów do skategoryzowania w ramach rozgrywki
 * @author donata
 */
public class Dictionary {

    /**
     * Rodzaj słowa
     */
    public enum WordType {

        /**
         * Rzeczownik
         */
        RZECZOWNIK,

        /**
         * Czasownik
         */
        CZASOWNIK,

        /**
         * Przymiotnik
         */
        PRZYMIOTNIK
    };
    
    /**
     * Lista wszystkich rzeczowników.
     */
    static final List<String> rzeczowniki = Arrays.asList(
            "galaktyka",
            "kosmos",
            "rakieta", 
            "astronauta", 
            "astronomia",
            "atmosfera", 
            "czarna dziura", 
            "galaktyka", 
            "gwiazda", 
            "Merkury",
            "Wenus", 
            "Ziemia", 
            "Mars", 
            "Jowisz", 
            "Saturn", 
            "Uran", 
            "Neptun",
            "Pluton", 
            "kometa", 
            "księżyc", 
            "meteor",
            "niebo", 
            "planeta",
            "promień", 
            "satelita", 
            "Słońce", 
            "statek kosmiczny", 
            "Supernowa"
    );
    
    /**
     * Lista wszystkich czasowników.
     */
    static final List<String> czasowniki = Arrays.asList(
            "krążyć",
            "kręcić się",
            "obserwować",
            "odbierać",
            "nadawać",
            "okrążać",
            "patrzeć",
            "oddalać się",
            "płonąć",
            "orbitować",
            "powrócić",
            "odlecieć",
            "prowadzić",
            "wchodzić",
            "wejść",
            "wybuchnąć",
            "wybuchać",
            "wylądować",
            "lądować",
            "wyruszyć",
            "wyruszać",
            "wystrzelić",
            "strzelać",
            "zachodzić",
            "zbliżać się",
            "zbliżyć się",
            "odliczać",
            "odliczyć",
            "latać",
            "lecieć",
            "przyciągać",
            "kolidować",
            "zderzać",
            "uderzać",
            "uderzyć"
    );
    
    /**
     * Lista wszystkich przymiotników.
     */
    static final List<String> przymiotniki = Arrays.asList(
            "astrologiczny",
            "astronomiczny",
            "badawczy",
            "bezzałogowy",
            "galaktyczny",
            "gwieździsty",
            "gwiezdny",
            "kosmiczny",
            "księżycowy",
            "nieskończony",
            "nieważki",
            "słoneczny",
            "załogowy",
            "nowoczesny",
            "rakietowy",
            "naukowy",
            "odkrywczy",
            "przełomowy",
            "zawrotny",
            "ultrafioletowy",
            "spadający",
            "atmosferyczny",
            "księżycowy",
            "planetarny",
            "naziemny",
            "radiowy",
            "świetlny"
    );
    
    /**
     * Zwraca nazwę części mowy na podstawie wartości enumeracji
     * @param kind części mowy
     * @return String reprezentujący tą część mowy
     */
    static String getWordTypeName(WordType kind)
    {
        switch (kind) {
            case RZECZOWNIK:
                return "Rzeczownik";
            case CZASOWNIK:
                return "Czasownik";
            case PRZYMIOTNIK:
                return "Przymiotnik";
            default:
                return "Nieznany";
        }
    }
    
    /**
     * Zwraca wylosowane słowo zadanej części mowy
     * @param kind Część mowy, z którego należy wylosować
     * @return Wylosowane słowo
     */
    static String getWord(WordType kind)
    {
        Random r = ThreadLocalRandom.current();
        
        switch (kind)
        {
            case RZECZOWNIK:
                return rzeczowniki.get(r.nextInt(rzeczowniki.size()));
            case CZASOWNIK:
                return czasowniki.get(r.nextInt(czasowniki.size()));
            case PRZYMIOTNIK:
                return przymiotniki.get(r.nextInt(przymiotniki.size()));
            default:
                return "Błąd, nieznany rodzaj słowa";
        }
    }
}
