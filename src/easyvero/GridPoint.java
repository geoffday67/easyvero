package easyvero;

import javafx.scene.Node;
import javafx.scene.shape.Circle;

public class GridPoint {
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public GridPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public GridPoint(Object source) {
        if (source instanceof Circle) {
            x = (int) ((Circle) source).getCenterX();
            y = (int) ((Circle) source).getCenterY();
        } else {
            x = (int) ((Node) source).getLayoutX();
            y = (int) ((Node) source).getLayoutY();
        }
    }
}
