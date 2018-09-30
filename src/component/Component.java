package component;

import easyvero.Board;
import easyvero.ConnectionPoint;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Translate;

public abstract class Component extends Parent {

    protected Translate positionTranslate = new Translate();
    protected List<ConnectionPoint> connections;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Group groupConnections = new Group();
    protected Group groupOutline = new Group();

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void showDebug(boolean show) {}
    
    public void setPosition(int x, int y) {
        // Remember the setting and set the translation to match
        this.x = x;
        this.y = y;
        positionTranslate.setX(x);
        positionTranslate.setY(y);
    }
    
    public void setSize (int width, int height) {
        this.width = width;
        this.height = height;
    }

    protected Component() {
        // Create a translation transform for positioning the component
        positionTranslate = new Translate();
        getTransforms().add(positionTranslate);

        // Create a list of the connection points
        connections = new ArrayList<>();
        
        getChildren().add(groupConnections);
        getChildren().add(groupOutline);
    }

    protected void setConnectionDrawables() {
        List<Node> children = groupConnections.getChildren();
        children.clear();
        for (ConnectionPoint connection : connections) {
            Circle pad = new Circle(connection.x, connection.y, Board.HOLE_RADIUS);
            pad.setFill(Board.PAD_COLOUR);
            children.add(pad);
        }
    }

    public void configure(Object configObject) {
    }

    public void setSelected(boolean selected) {
    }

    public Pane getDialog() {
        return null;
    }

    public Object getConfigFromDialog(Node dialog) {
        return null;
    }
}
