/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rf;

/**
 * Reprezentuje pozycje w przeszłości
 * 
 * Używana w klasie Rocket jako lista historii pozycji, na podstawie której
 * możemy skonstruować PreviousRocket, która tą historię bedzie odtwarzać.
 * @author donata
 */
public class Position {
    double x;
    double y;
    double t;
    double angle;
    
    Position(double x, double y, double t, double angle)
    {
        this.x = x;
        this.y = y;
        this.t = t;
        this.angle = angle;
    }
    
}
