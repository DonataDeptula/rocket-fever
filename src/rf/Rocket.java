/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import rf.GamePanel.ObjectType;

/**
 * Rakieta sterowana przez gracza.
 * @author donata
 */
public class Rocket {
    double x, y;
    double angle;
    double speed;
    double fastspeed;
    double slowspeed;

    /**
     * Historia przebytej drogi, na podstawie której zostanie skonstruowana
     * PreviousRocket
     */
    public List<Position> positions = new ArrayList<>();
    GamePanel gamePanel;
    /**
     * Wielkość rakiety w rozpatrywanych kolizjach.
     */
    static final double RADIUS=20;
    /**
     * Obraz rakiety to czerwona rakieta
     */
    static BufferedImage image = null;
    boolean ready = false;

    /**
     * Sterowanie rakietą, pobudzane klawiaturą.
     */
    public Controls controls;
    
    Rocket(GamePanel gp, double x, double y, double angle, double speed)
    {
        this.gamePanel = gp;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.speed = speed;
        this.fastspeed = speed;
        this.slowspeed = speed/2.0;
        this.controls = new Controls();
        if (image == null) {
            try {
                image = ImageIO.read(new File("res/rocket.png"));
            } catch (IOException e)
            {
                System.out.println("Couldn't load rocket image!");
            }
        }
    }
    
    /**
     * Uruchamia lub zatrzymuje bieg rakiety
     * @param ready 
     */
    void setReady(boolean ready)
    {
        this.ready = ready;
    }
    
    void paint(Graphics2D g)
    {
        // Obiekt transformacji ukladu wspolrzednych
        // pozwala nam narysowac rakiete w dowolnym miejscu, dowolnie obrocona
        // i przeskalowana
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
        
        g.setColor(new Color(0,255,0,128));
    }

    /**
     * Aktualizuje pozycję rakiety i uwzględnia aktualne sterowanie.
     * 
     * Ma tutaj miejsce wykrywanie kolizji, w przypadku której prędkość rakiety
     * ulega zmniejszeniu.
     * @param dt 
     */
    void update(double dt)
    {
        if (!ready)
        {
            return;
        }
        
        if (controls.left) {
            angle -= 2*dt;
        }
        
        if (controls.right) {
            angle += 2*dt;
        }
        double nx = x + Math.cos(angle)*speed*dt;
        double ny = y + Math.sin(angle)*speed*dt;
        
        // Przeiterować po wszystkich obstaclach
        // sprawdzić, czy koła reprezentujące rakiete i obstacla
        // nachodzą się, jeśli tak to ustawić speed na 0
        CollisionResult col = gamePanel.collision(nx, ny, Rocket.RADIUS);
        
        if (col.objectType != ObjectType.NONE && col.objectType != ObjectType.BONUS)
        {
                speed = this.slowspeed;
                nx = x;
                ny = y;
        } else if (col.objectType == ObjectType.BONUS) {
            Bonus b = (Bonus)col.object;
            
            gamePanel.collectBonus(b);
        }
        
        x = nx;
        y = ny;

        
        if ((this.x >= gamePanel.targetZone.x && 
             this.x <= (gamePanel.targetZone.x + gamePanel.targetZone.width)) && 
            (this.y >= gamePanel.targetZone.y &&
             this.y <= (gamePanel.targetZone.y + gamePanel.targetZone.height))){
            
            gamePanel.startNewRound(new ArrayList<Position>(positions));
        }
        positions.add(new Position(x, y, gamePanel.getRoundTime(), angle));
        
    }
    
    
}
