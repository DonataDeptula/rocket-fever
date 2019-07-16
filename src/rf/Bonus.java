/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rf;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import static java.lang.Math.sin;

/**
 * Bonus czasowy do zebrania na planszy
 * @author donata
 */
public class Bonus {
    double x, y; /// Położenie na planszy
    static final double RADIUS = 22.0; /// Promień kolizji
    
    private static Font font = new Font("Arial", Font.BOLD, 14);
    Bonus(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Rysuje bonus na planszy jako migające i pulsujące koło
     * @param g 
     */
    void paint(Graphics2D g)
    {
        double t = System.currentTimeMillis()/1000.0;

        Color c = new Color(
                (int)(255-(sin(9*t)+1)/2*70),
                (int)(255-(sin(9*t)+1)/2*70),
                0,
                255);
        g.setColor(c);
        double radius = Bonus.RADIUS + sin(8*t)*3;
        g.fillOval((int)(x-radius), (int)(y-radius), (int)radius*2, (int)radius*2);
        
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawString("+10", (int)(x)-12, (int)(y)+4);
        
    }
}
