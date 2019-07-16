/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rf;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Klasa przechowująca pojedynczy wynik gracza
 * 
 * Zawiera znacznik czasu (datę i godzinę) oraz uzyskany wynik
 * @author donata
 */
class HighScore
{
    String date;
    int score;
    
    HighScore(String date, int score)
    {
        this.date = date;
        this.score = score;
    }
    
    /**
     * Wczytuje tabelę wyników z pliku o podanej nazwie
     * 
     * @param name Nazwa pliku do odczytania
     * @return Lista wczytanych wyników
     */
    static List<HighScore> loadAllFromFile(String name)
    {
        List<HighScore> scores = new ArrayList<HighScore>();
        
        BufferedReader r;
        
        try
        {
            r = new BufferedReader(new FileReader(name));
            while (true)
            {
                String s = r.readLine();
                if (s == null) {
                    break;
                }
                String[] parts = s.split(" ");
                HighScore score = new HighScore(parts[0] + " " + parts[1], Integer.parseInt(parts[2]));
                
                scores.add(score);
            }
        }
        catch (IOException io)
        {
            System.err.println(io.toString());
        }
        
        return scores;
    }
    
    /**
     * Zapisuje podane wyniki do pliku o podanej nazwie
     * @param name Nazwa pliku wynikowego
     * @param scores Tabela wyników
     */
    static void saveAllToFile(String name, List<HighScore> scores)
    {
        BufferedWriter f = null;
        try
        {
            f = new BufferedWriter(new FileWriter(name));
            for (HighScore score : scores) {
                f.write(score.date + " " + score.score + "\n");
            }
        } catch(IOException io)
        {
            System.err.println(io.toString());
        }
        if (f != null) {
            try {
                f.close();
            } catch (IOException ex) {
                Logger.getLogger(HighScore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

/**
 *
 * @author donata
 */
public class Main extends JFrame implements KeyListener {
    /**
     * @param args the command line arguments
     */
    GameState currentPanel;
    
    List<HighScore> scores;
    
    Main()
    {
        setSize(GamePanel.WIDTH, GamePanel.HEIGHT+54);
        setTitle("Rocket Fever");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        
        scores = HighScore.loadAllFromFile("wyniki.txt");

        changePanel(new MainMenu(this));
    }
    
    /**
     * Dodaje nowy wynik do tabeli wyników
     * @param score Uzyskany wynik
     */
    public void addHighScore(int score)
    {
        // Tworzenie aktualnej daty w formie tekstowej
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String dateString = dateFormat.format(date);
        
        scores.add(new HighScore(dateString, score));
    }
    
    /**
     * Zapisuje wyniki uzyskane w grze do pliku o nazwie "wyniki.txt"
     * 
     * Pośrednio korzysta z metody HighScore.saveAllToFile
     */
    public void saveHighScores()
    {
        HighScore.saveAllToFile("wyniki.txt", scores);
    }
    
    // Metoda pozwalająca zmieniać okna (ogólnie)
    // Pomysł jest taki, że jednocześnie pokazujemy jedno okno na raz
    // Za każdym wywołaniem tej funkcji kasujemy aktualne
    // i ustawiamy na całość nowe

    /**
     * Przełącza ekran aplikacji na podany GameState
     * @param newPanel Nowy ekran, który zastąpi obecny
     */
    public void changePanel(GameState newPanel)
    {
        if (currentPanel != null) {
            this.getContentPane().remove(currentPanel);
        }
        currentPanel = newPanel;
        this.getContentPane().add(newPanel);
        this.invalidate();
        this.validate();
    }
    
    /**
     * Główna funkcja programu
     * @param args
     */
    public static void main(String[] args) {
        Main m = new Main();
        m.setVisible(true);
    }

    /**
     *
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Przekazuje naciśnięcie klawisza na klawiaturze podrzędnemu GameState
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        currentPanel.keyPressed(e);
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Przekazuje puszczenie klawisza na klawiaturze podrzędnemu GameState
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        currentPanel.keyReleased(e);
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
