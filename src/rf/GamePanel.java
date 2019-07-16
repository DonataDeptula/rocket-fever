/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rf;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import rf.Dictionary.WordType;
import static rf.Rocket.RADIUS;

/**
 * Reprezentuje możliwe sterowanie i jego stan.
 * @author donata
 */
class Controls
{
    boolean left;
    boolean right;
    
    Controls()
    {
        left = false;
        right = false;
    }
    
    /**
     * Ustawia stan przycisku 'w lewo'
     * @param value 
     */
    public void setLeft(boolean value)
    {
        left = value;
    }
    
    /**
     * Ustawia stan przycisku 'w prawo'
     * @param value 
     */
    public void setRight(boolean value)
    {
        right = value;
    }
}

/**
 * Rezultat kolizji z funkcji GamePanel.collision
 * @author donata
 */
class CollisionResult
{
    /// Rodzaj obiektu, z którym została wykryta kolizja
    GamePanel.ObjectType objectType;
    /**
     * Objekt, z którym została wykryta kolizja
     * 
     * Jeśli wykryto kolizję ze światem lub brak kolizji, będzie to null
     */
    Object object;
    
    CollisionResult(GamePanel.ObjectType objectType, Object object)
    {
        this.objectType = objectType;
        this.object = object;
    }
}

/**
 * Przechowuje i przetwarza całą logikę gry
 * @author donata
 */
public class GamePanel extends GameState implements Runnable {
    
    /**
     * Rodzaj obiektu w grze
     * 
     * Wykorzystywane przy wykrywaniu kolizji.
     */
    enum ObjectType {
        NONE,
        OBSTACLE,
        ROCKET,
        WORLD_END,
        BONUS
    };
    
    /// Informuje o tym, czy gra postępuje
    boolean running = false;
    
    /// Rakieta sterowana przez gracza
    Rocket r;

    /**
     * Wszystkie przeszkody na planszy
     */
    public List<Obstacle> obstacles;

    /**
     * Poprzednie rakiety
     */
    public List<PreviousRocket> previousRocket;

    /**
     * Bonusy na planszy
     * 
     * Na początku rozgrywki są 3, po zebraniu przez gracza są one usuwane
     * z tej listy.
     */
    public List<Bonus> bonuses;
    /**
     * Obrazek tła
     */
    BufferedImage bg;
    /**
     * Referencja do głównej aplikacji, przechowywana w celu przechodzenia do
     * innych okien (do okna Menu Głównego), ponieważ tą funkcjonalność obsługuje
     * właśnie ta klasa
     */
    Main main;
    
    /**
     * Reprezentuje punkt na osi czasu, w którym rozpoczeła się obecna runda
     * 
     * Pozwala odmierzać czas względem jej początku.
     */
    private double roundStartTime;
    
    /**
     * Opóźnienie, z którym ruszy gracz, aby uniknąć kolizji z rakietami poprzednimi
     */
    private double rocketDelay = 0;

    /**
     * Pojedyncze opóźnienie rakiety, w przypadku pojawienia się na polu, z którego
     * startuje N rakiet, opóźnienie wyniesie N*ROCKET_DELAY
     */
    public static final double ROCKET_DELAY = 0.75;

    /**
     * Bazowe opóźnienie rakiety, umożliwie zapoznanie się z zadanym słowem
     * i znalezienie odpowiedniego wyjścia na planszy.
     */
    public static final double BASE_ROCKET_DELAY = 4.0;
    
    JButton returnButton;
    JButton restartButton;
    JLabel timeLabel;
    JLabel scoreLabel;
    JLabel wordLabel;
    String currentWord;
    
    /**
     * Szerokość okna gry
     */
    public static final int WIDTH = 1024;

    /**
     * Wysokość okna gry
     */
    public static final int HEIGHT = 728-40;
    private static final Font font = new Font("Arial", Font.PLAIN, 21);

    /**
     * Strefy wejścia/wyjścia z planszy
     */
    public List<Zone> zones = Arrays.asList(
          new Zone(450, 0, 115, 90, Math.PI/2),            //up
          new Zone(0, 380, 90, 115, 0),                    //left
          new Zone(WIDTH-90, 300, 90, 115, Math.PI),        //right
          new Zone(512, HEIGHT-90, 115, 90, Math.PI*3/2)     //down
    );
    
    /**
     * Strefa początkowa dla gracza w danej rundzia
     * 
     * Zachowana aby móc powtórzyć rundę i powrócić gracza do strefy, z której
     * zaczynał.
     */
    public Zone startZone;

    /**
     * Strefa docelowa dla gracza.
     * 
     * Przechowuje ona tą część mowy, do której pasuje wylosowane słowo i jest
     * celem gracza.
     */
    public Zone targetZone;
    
    /**
     * Podstawowa długość gry.
     * 
     * Zbieranie bonusów może wydłużyć czas rozgrywki.
     */
    private double gameTime = 60.0;
    /**
     * Aktualny wynik gracza.
     */
    private int gameScore = 0;
    /**
     * Informuje o tym, czy rakieta gracza powinna się poruszać.
     */
    private boolean shouldRun = true;
    
    /**
     * Wykorzystywana do wyświetlania wyrazów
     */
    Font wordFont = new Font("Arial", Font.BOLD, 64);
    /**
     * Wykorzystywana do wyświetlania odliczania
     */
    Font bigFont = new Font("Arial", Font.BOLD, 128);
    
    /**
     * Creates new form GameFrame2
     * @param main
     */
    public GamePanel(Main main) {
        initComponents();
        
        {
            int x = 0;
            returnButton = new JButton("POWRÓT");
            returnButton.setBounds(x, 0, 128, 24);
            x+=128;
            add(returnButton);
            returnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                main.changePanel(new MainMenu(main));
            }
            });
            
            
            restartButton = new JButton("RESTART RUNDY");
            restartButton.setBounds(x, 0, 192, 24);
            x+=192;
            add(restartButton);
            restartButton.setFocusable(false);

            restartButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    main.setFocusable(true);
                    restartRound();
                    main.requestFocus();
                    main.requestFocusInWindow();
                    main.setVisible(true);
                }
            });
            
            scoreLabel = new JLabel("WYNIK: 0");
            scoreLabel.setBounds(x, 0, 128, 24);
            x+=128;
            add(scoreLabel);
            
            timeLabel = new JLabel("CZAS: 60");
            timeLabel.setBounds(WIDTH-128, 0, 128, 24);
            x+=128;
            add(timeLabel);
            
            wordLabel = new JLabel("SŁOWO: banana");
            wordLabel.setBounds(490, 0, 256, 24);
            wordLabel.setFont(new Font("Arial", Font.BOLD, 16));
            x+=128;
            add(wordLabel);
            invalidate();
    
        }
        
        this.main = main;
        //o = new Obstacle(200, 200, 32);
        int[][] obstacles_config = {
            {100, 200, 32},
            {200, 300, 64},
            {300, 150, 24},
            {50, 150, 29},
           // {10, 10, 50},
            {150, 50, 41},
            {240, 60, 45},
            {400, 50, 35},
            {650, 60, 55},
          //  {780, 80, 45},
            {890, 45, 35},
            {200, 500, 35},
            {250, 450, 20},
           // {460, 350, 60},
            {440, 200, 20},
            {470, 140, 10},
            {550, 250, 10},
            {740, 350, 8},
            {730, 320, 8},
         //   {755, 280, 9},
            {900, 200, 18},
            {950, 700, 35},
            {975, 540, 40},
            {900, 500, 30},
            {600, 450, 40},
            {610, 550, 25},
            {760, 670, 10},
            {40, 700, 30},
            {400, 600, 24},
            {340, 670, 28},
        };
        
        
        obstacles = new ArrayList<Obstacle>();        
        bonuses = new ArrayList<Bonus>();
        previousRocket = new ArrayList<PreviousRocket>();
        
        for (int[] o: obstacles_config) {
            obstacles.add(new Obstacle(o[0], o[1], o[2]));
        }
        
        for (int i = 0; i < 3; i++) {
            Random rand = ThreadLocalRandom.current();
            double x, y;
            do {
                x = rand.nextInt(WIDTH-(int)Bonus.RADIUS*2)+(int)Bonus.RADIUS;
                y = rand.nextInt(HEIGHT-(int)Bonus.RADIUS*2)+(int)Bonus.RADIUS;
            } while (collision(x, y, Bonus.RADIUS).objectType != ObjectType.NONE);
            
            bonuses.add(new Bonus(x, y));
        }

        this.startNewRound(null);
        
        try {
            bg = ImageIO.read(new File("res/space.jpg"));
        } catch (IOException e)
        {
            System.out.println("Couldn't load background image.");
        }
        
        start();
        
    }
    
    /**
     * Zwraca czas od poczatku rundy
     * @return
     */
    public double getRoundTime()
    {
        return System.currentTimeMillis()/1000.0 - roundStartTime;
    }
    
    /**
     * Rozpoczyna nową rundę.
     * 
     * Jest to złożona procedura:
     * - losowana jest nowa rakieta dla gracza
     * - losowane są strefy początkowa, docelowa i reszta wraz z ich częściami
     *   mowy
     * - poprzednia trasa (otrzymywana w 'positions') jest używana do stworzenia
     *   'nagranej' rakiety
     * @param positions Trasa wykonana przez gracza w poprzedniej rundzie.
     */
    public void startNewRound(List<Position> positions)
    {
        Random generator = new Random(System.currentTimeMillis());
        
        List<WordType> kinds = Arrays.asList(
                WordType.RZECZOWNIK,
                WordType.CZASOWNIK,
                WordType.PRZYMIOTNIK
        );
        Collections.shuffle(kinds);
        Collections.shuffle(zones);
        
        startZone = zones.get(0);
        startZone.setText("");
        
        targetZone = zones.get(1);
        for (int i=1; i<4; i++) {
            WordType kind = kinds.get(i-1);
            zones.get(i).setText(Dictionary.getWordTypeName(kind));
        }
        currentWord = Dictionary.getWord(kinds.get(0));
        wordLabel.setText("SŁOWO: "+currentWord);
        

        
        
        roundStartTime = System.currentTimeMillis()/1000.0;     //chwila startu rundy

        startZone.rocketDelay += ROCKET_DELAY;
        rocketDelay = startZone.rocketDelay;
        
        
        
        
        r = new Rocket(this,
                startZone.x + startZone.width/2,
                startZone.y + startZone.height/2,
                startZone.angle,
                100 + generator.nextDouble()*60);
        
        for(PreviousRocket pr : previousRocket)
        {
            pr.reset();
        }
        
        if (positions == null)
        {
            return;
        }
        
        gameScore += 1;
        scoreLabel.setText("WYNIK: "+gameScore);
        PreviousRocket pr = new PreviousRocket(this, positions);
        previousRocket.add(pr);
        
    }
    
    /**
     * Powtarza aktualną runde.
     * 
     * Cofa gracza do strefy, z której zaczynał.
     */
    void restartRound()
    {
        roundStartTime = System.currentTimeMillis()/1000.0;     //chwila startu rundy
        r = new Rocket(this,
                startZone.x + startZone.width/2,
                startZone.y + startZone.height/2,
                startZone.angle,
                r.fastspeed);
        
        for(PreviousRocket pr : previousRocket)
        {
            pr.reset();
        }
        
    }
    
    /**
     * Rysuje aktualny stan gry
     * @param g
     */
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(0, 24);
        // System.out.println("paint");
        // Czyścimy całe okno gry
        // Bo np przechodząc z Menu do Gry zostawały stare rzeczy
        // narysowane
        g.clearRect(0, 0, WIDTH, HEIGHT);
        g.drawImage(bg, 0, 0, WIDTH, HEIGHT, null);
        
        for (Obstacle o : obstacles) {
            o.paint(g2d);
        }

        
        for (int i = 0; i<4; i++) {
            zones.get(i).paint(g2d);
         }
        
        // targetZone.paintTargetZone(g2d);
        
        for (Bonus b : bonuses) {
            b.paint(g2d);
        }
                
        for (PreviousRocket pr : previousRocket)
        {
            pr.paint(g2d);
        }
        
        r.paint(g2d);
        
        if (!shouldRun) {
            g2d.setColor(new Color(0, 0, 0, 64));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(font);
            
            String text = "Koniec gry! Twój wynik to: " + gameScore;
            FontMetrics fm = g2d.getFontMetrics();
            
            g2d.drawString(
                    text,
                    WIDTH/2 - fm.stringWidth(text)/2, 
                    HEIGHT/2 + fm.getHeight()/2);

        }
        
        if (shouldRun && getRoundTime() < rocketDelay) {
            g2d.setFont(bigFont);
            FontMetrics fm = g2d.getFontMetrics();
            
            String text = String.valueOf((int)(rocketDelay - getRoundTime()));
            
            g2d.setColor(Color.RED);
            g2d.drawString(
                    text,
                    WIDTH/2-fm.stringWidth(text)/2+24,
                    HEIGHT/2-fm.getHeight()/2+100
            );
            
            g2d.setFont(wordFont);
            fm = g2d.getFontMetrics();
            
            text = currentWord;
            g2d.drawString(text,
                    WIDTH/2-fm.stringWidth(text)/2+24,
                    HEIGHT/2-fm.getHeight()/2+120);
        }
        
    }
    
    /**
     * Sprawdza czy podana pozycja koliduje z jakimkolwiek obiektem na planszy
     * @param nx współrzędna x sprawdzanej pozycji
     * @param ny współrzędna y sprawdzanej pozycji
     * @param radius promień obiektu, który miałby kolidować z tymi na planszy
     * @return
     */
    public CollisionResult collision(double nx, double ny, double radius)
    {
        if (nx > WIDTH || nx < 0 || ny > HEIGHT || ny < 0)
        {
            return new CollisionResult(ObjectType.WORLD_END, null);
        }
        
        for(Obstacle o : obstacles)
        {
            double dx = o.x - nx;
            double dy = o.y - ny;
            double distance = sqrt(dx * dx + dy * dy);
            if(distance <= radius + o.radius)
            {
                return new CollisionResult(ObjectType.OBSTACLE, o);
            } 
        } 
        
        for (PreviousRocket pr : previousRocket)
        {
            Position pos = pr.getPosition();
            double dx = pos.x - nx;
            double dy = pos.y - ny;
            double distance = sqrt(dx * dx + dy * dy);
            if(distance <= Rocket.RADIUS + radius)
            {
                return new CollisionResult(ObjectType.ROCKET, pr);
            } 
        }
        
        for (Bonus b : bonuses) {
            double dx = b.x - nx;
            double dy = b.y - ny;
            double distance = sqrt(dx * dx + dy * dy);
            if(distance <= Bonus.RADIUS + radius)
            {
                return new CollisionResult(ObjectType.BONUS, b);
            }   
        }
        
        return new CollisionResult(ObjectType.NONE, null);
    }
    
    /**
     * Wywoływana kiedy gracz zbierze bonus.
     * 
     * Dodaje mu odpowiednią ilość czasu i obsługuje skasowanie bonusu.
     * @param b Bonus, który ma być zebrany
     */
    void collectBonus(Bonus b)
    {
        gameTime+=10.0;
        bonuses.remove(b);
    }
    
    /**
     * Obsługuje naciśnięcie klawiatury.
     * @param e
     */
    public void keyPressed(KeyEvent e)
    {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                main.changePanel(new MainMenu(main));
                break;
            
            case KeyEvent.VK_LEFT:
                r.controls.setLeft(true);
                break;
            
            case KeyEvent.VK_RIGHT:
                r.controls.setRight(true);
                break;
                
            case KeyEvent.VK_R:
                restartRound();
                break;
        }
    }
    
    /**
     * Obsługuje puszczenie klawisza na klawiaturze
     * @param e
     */
    public void keyReleased(KeyEvent e)
    {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                r.controls.setLeft(false);
                break;
            
            case KeyEvent.VK_RIGHT:
                r.controls.setRight(false);
                break;
        }
    }
    
    /**
     * Inicjalizuje wątek, w którym aktualizowana będzie gra.
     */
    public void start()
    {
        running = true;
        new Thread(this, "Rocket Fever").start();
        setVisible(true);
    }

    /**
     * Główna pętla gry
     */
    @Override
    public void run() {
        while(running){      
            update(1.0/60.0);
            this.repaint();
            
            Toolkit.getDefaultToolkit().sync();
            try
            {
                /// Oddaje czas procesorowi na inne zadania systmeu operacyjnego
                Thread.sleep(16);
            } catch (InterruptedException e)
            {
                
            }

        }
        stop();
    }
    
    /**
     * Zatrzymuje aplikacje
     */
    public void stop() {
        System.exit(0);
    }
    
    /**
     * Wykonuje czynności związane z końcem gry.
     * 
     * Obecnie jest to jedynie dodanie nowego wyniku do tabeli wyników.
     */
    void onGameFinish()
    {
        main.addHighScore(gameScore);
    }
    
    /**
     * Aktualizacja stanu świata w grze.
     * @param dt
     */
    public void update(double dt) {
        if (!shouldRun) {
            return;
        }
        
        if(getRoundTime() > rocketDelay)
        {
            gameTime -= dt;
            r.setReady(true);
            
            if (gameTime <= 0.0) {
                gameTime = 0.0;
                shouldRun = false;
                onGameFinish();
            }
        }
        r.update(dt);
        for(PreviousRocket pr : previousRocket)
        {
            pr.update(dt);
        }
        
         timeLabel.setText("CZAS: " + (int)gameTime);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
