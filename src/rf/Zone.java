/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rf;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 * Sterfa docelowa dla gracza
 * @author donata
 */
public class Zone {
    double x;
    double y;
    double width;
    double height;
    double angle;
    String text;

    /**
     * Opóźnienie występujące w tej strefie.
     * 
     * Im więcej rakiet startowało z tej strefy, tym będzie ono większe, celem
     * uniknięcia kolizji wzajemnej pomiędzy rakietami w tej samej strefie.
     */
    public double rocketDelay=GamePanel.BASE_ROCKET_DELAY;
    private Font font = new Font("Arial", Font.BOLD, 14);
    
    Zone(double x, double y, double width, double height, double angle)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.angle = angle;
    }
    
    /**
     * Zmienia wyświetlany na strefie tekst
     * @param newText Nowy tekst do wyświetlenia
     */
    void setText(String newText)
    {
        this.text = newText;
    }
    
    /**
     * Rysuje strefę i jej tekst.
     * @param g 
     */
    void paint(Graphics2D g)
    {
        g.setColor(new Color(250, 250, 110, 255));
        g.fillRect((int)x, (int)y, (int)width, (int)height);
        
        g.setFont(font);
        g.setColor(Color.BLACK);
        
        FontMetrics fm = g.getFontMetrics();
        int tx = fm.stringWidth(this.text);
        g.drawString(this.text, (int)x+(int)width/2-tx/2, (int)y+16);
        g.drawString(this.text, (int)x+(int)width/2-tx/2, (int)y+(int)height-8);
    }
    
}
