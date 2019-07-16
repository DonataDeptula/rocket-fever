/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rf;

import java.awt.event.KeyEvent;
import javax.swing.JPanel;

/**
 * Stan gry, którym to może być Menu Główne i sama Gra
 * 
 * @author donata
 */
abstract public class GameState extends JPanel {
    abstract void keyPressed(KeyEvent e);
    abstract void keyReleased(KeyEvent e);
}
