package component;

import easyvero.Board;
import easyvero.ConnectionPoint;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DIL extends Component {

    private int pins;   // Total number of pins, so must be a even number
    private int span;   // The number of holes between the legs
    
    private Rectangle outline;

    public DIL() {
        outline = new Rectangle();
    }

    public int getPins() {
        return pins;
    }
  
    public void setPins(int pins) {
        this.pins = pins;
        draw();
    }
    
    public int getSpan() {
        return span;
    }
    
    public void setSpan(int span) {
        this.span = span;
        draw();
    }

    private void draw() {
        List<ConnectionPoint> connections = new ArrayList<>();
        for (int n = 0; n < pins / 2; n++) {
            connections.add(new ConnectionPoint(0, n));
        }
        for (int n = 0; n < pins / 2; n++) {
            connections.add(new ConnectionPoint(span + 1, n));
        }
        setConnectionDrawables(connections);

        groupOutline.getChildren().clear();
        outline.setX(-0.5);
        outline.setY(-0.5);
        outline.setWidth(span + 2.0);
        outline.setHeight(pins / 2);
        outline.setFill(Color.WHITE);
        outline.setOpacity(0.8);
        outline.setStroke(Board.COMPONENT_COLOR);
        outline.setStrokeWidth(0.1);
        groupOutline.getChildren().add(outline);        
    }
    
    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        outline.setStroke(selected ? Color.BLUE : Board.COMPONENT_COLOR);
        outline.setStrokeWidth(selected ? 0.2 : 0.1);
    }

    @Override
    public Pane getDialog() {
        GridPane result = new GridPane();
        result.setHgap(10);
        result.setVgap(10);

        result.add(new Label("Pins"), 0, 0);
        Spinner pins = new Spinner(6, 100, 8, 2);
        pins.setId("pins");
        pins.setEditable(true);
        result.add(pins, 1, 0);

        result.add(new Label("Span"), 0, 1);
        Spinner span = new Spinner(1, 100, 2);
        span.setId("span");
        result.add(span, 1, 1);

        return result;
    }

    @Override
    public void configureFromDialog(Node dialog) {
        pins = ((Spinner<Integer>) dialog.lookup("#pins")).getValue();
        span = ((Spinner<Integer>) dialog.lookup("#span")).getValue();
        
        draw();
    }
}
