package component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import easyvero.Board;
import easyvero.ConnectionPoint;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.transform.Translate;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
public abstract class Component {

    static protected Font valueFont = new Font(80);
    static protected Border testBorder = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(10)));
    
    // Members which define how to create this component (save these)
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected String value = "";

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        positionTranslate.setX(x * 100);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        positionTranslate.setY(y * 100);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    // Members which help draw this component (don't save these, they'll be recreated during load)
    @JsonIgnore
    protected boolean selected = false;
    
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private Translate positionTranslate;
    
    protected Group groupComponent;
    protected Group groupConnections;
    protected Group groupOutline;
    protected Group groupValue;

    @JsonIgnore
    public Node getDrawable() {
        return groupComponent;
    }

    public void setPosition(int x, int y) {
        setX(x);
        setY(y);
    }

    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    protected Component() {
        // Create the drawing groups
        groupComponent = new Group();
        groupConnections = new Group();
        groupOutline = new Group();
        groupValue = new Group();

        // Add the connections and the outline to the component drawing
        groupComponent.getChildren().addAll(groupConnections, groupOutline, groupValue);

        // Create a translation transform for positioning the component
        positionTranslate = new Translate();
        groupComponent.getTransforms().add(positionTranslate);
    }

    /**
     * Create drawables to represent the connection points. Call this after the
     * connections are created.
     */
    protected void setConnectionDrawables(List<ConnectionPoint> connections) {
        groupConnections.getChildren().clear();
        for (ConnectionPoint connection : connections) {
            Circle pad = new Circle(connection.x * 100, connection.y * 100, Board.HOLE_RADIUS);
            pad.setFill(Board.PAD_COLOUR);
            groupConnections.getChildren().add(pad);
        }
    }

    /**
     * Get a dialog pane for configuring the component
     *
     * @return The Pane or null if none if needed
     */
    @JsonIgnore
    public Pane getDialog() {
        return null;
    }

    /**
     * Convert the completed dialog to a component-specific configuration object
     */
    public void configureFromDialog(Node dialog) {
    }
}
