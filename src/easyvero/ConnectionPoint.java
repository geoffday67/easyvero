/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyvero;

/**
 *
 * @author geoffday
 */
public class ConnectionPoint {
    /*
    A point where the component connects to the board.
    Coordinates are relative to the top left of the component
    */
    
    public int x;
    public int y;

    public ConnectionPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    
}
