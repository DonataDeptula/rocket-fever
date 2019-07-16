/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import static rf.Rocket.image;

/**
 * Rakieta podązająca za wcześniej przebytą przez gracza trasą.
 * @author donata
 */
public class PreviousRocket {

    /**
     * Historia pozycji, którą rakieta będzie odtwarzać
     */
    public List<Position> positions = new ArrayList<>();
   /**
    * Indeks w historii pozycji aktualnie przetwarzanej.
    */
   int i;
   /**
    * Referencja do gry celem odczytywania czasu rundy.
    */
   GamePanel gamePanel;
   
   /**
    * Obraz PreviousRocket to biała rakieta
    */
   static BufferedImage image = null;

    /**
     *
     * @param gamePanel
     * @param positions
     */
    public PreviousRocket(GamePanel gamePanel, List<Position> positions)
    {
        this.gamePanel = gamePanel;
        this.positions = positions;
        Position lp = this.positions.get(this.positions.size()-1);
        this.positions.add(new Position(-100.0, -100.0, lp.t, 0.0));
        
        this.i = 0;
        
        if (image == null) {
            try {
                image = ImageIO.read(new File("res/previous_rocket.png"));
            } catch (IOException e)
            {
                System.out.println("Couldn't load previous rocket's image!");
            }
        }
    }
    
    /**
     * Zwraca aktualną pozycję PreviousRocket
     * 
     * Potrzebne w celu wykrywania kolizji.
     * @return
     */
    public Position getPosition()
    {
        return positions.get(i);
    }
    
    /**
     * Cofa rakietę na początek historii pozycji.
     */
    public void reset()
    {
        i = 0;
    }
    
    /**
     * Aktualizacja przetwarzanej pozycji
     * @param dt różnica czasu pomiędzy klatkami w grez
     */
    void update(double dt)
    {
        double t = gamePanel.getRoundTime();
        
        while (i < positions.size()-1 && positions.get(i).t < t)
        {
            this.i += 1;
        }
        
    }
    
    /**
     * Rysuje PreviousRocket
     * @param g 
     */
    void paint(Graphics2D g)
    {
        Position p = positions.get(i);
        double x = p.x;
        double y = p.y;
        double angle = p.angle;
        double RADIUS = Rocket.RADIUS;
        
        AffineTransform xform = 
            AffineTransform.getTranslateInstance(
                    x,
                    y);
        // Skalujemy obrazek rakiety czterokrotnie
        xform.scale(4.0, 4.0);
        xform.translate(-image.getWidth()/2, -image.getHeight()/2);
        // Obracamy rakiete wedlug jej obrotu
        xform.rotate(angle, image.getWidth()/2, image.getHeight()/2);
        
        g.drawImage(image, xform, null);
        
        g.setColor(new Color(0,255,255,128));
    }
   
}
