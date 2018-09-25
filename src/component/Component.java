package component;

import easyvero.Board;
import easyvero.ConnectionPoint;
import easyvero.GridPoint;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Translate;

public abstract class Component extends Parent {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected int x;
    protected int y;

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        positionTranslate.setX(x);
        positionTranslate.setY(y);
    }

    protected Translate positionTranslate;
    protected List<ConnectionPoint> connections;

    protected Component(int x, int y) {
        // Create a translation transform for positioning the component
        positionTranslate = new Translate(x, y);
        getTransforms().add(positionTranslate);

        // Create a list of the connection points
        connections = new ArrayList<>();
    }
    
    protected Component(GridPoint start, GridPoint end) {
        this(start.getX(), start.getY());
    }

    protected void setConnectionDrawables() {
        List<Node> children = getChildren();
        children.clear();
        for (ConnectionPoint connection : connections) {
            Circle pad = new Circle(connection.x, connection.y, Board.HOLE_RADIUS);
            pad.setFill(Board.PAD_COLOR);
            children.add(pad);
        }
    }

    public void configure(Object configObject) {
    }

    public void setSelected(boolean selected) {
    }

    public Pane getComponentDialog() {
        return null;
    }

    public Object getConfigFromDialog(Node dialog) {
        return null;
    }
}
