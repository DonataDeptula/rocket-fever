/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Przeszkoda na planszy
 * @author donata
 */
public class Obstacle {
    /// Pozycja przeszkody
    double x, y;
    /// Promień przeszkody
    double radius;
    /// Kolor przeszkody
    Color color;
    
    /**
     * Konstruuje przeszkodę losując jej kolor
     * 
     * @param x wsp. x przeszkody
     * @param y wsp. y przeszkody
     * @param radius promień przeszkody
     */
    Obstacle(double x, double y, double radius)
    {
        this.x = x;
        this.y = y;
        this.radius = radius;
        
        Random r = ThreadLocalRandom.current();
        color = new Color(100+r.nextInt(155), 100+r.nextInt(155), 100+r.nextInt(155), 255);
    }
    
    /**
     * Rysuje przeszkodę na planszy
     * @param g 
     */
    void paint(Graphics2D g)
    {
        g.setColor(color);
        g.fillOval(
                (int)(x-radius), //x
                (int)(y-radius), //y
                (int)(2*radius), //szer
                (int)(2*radius));// wys
    }
}
