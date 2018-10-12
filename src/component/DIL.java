package component;

import easyvero.Board;
import static easyvero.Board.SCALE_FACTOR;
import easyvero.ConnectionPoint;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

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
        outline.setX(-50);
        outline.setY(-40);
        outline.setWidth((span + 2) * 100);
        outline.setHeight((pins / 2) * 100);
        outline.setFill(Color.WHITE);
        outline.setOpacity(0.8);
        outline.setStroke(Board.COMPONENT_COLOR);
        outline.setStrokeWidth(10);
        groupOutline.getChildren().add(outline);

        Text text = new Text(outline.getX() - 1000, 100, value);
        text.setFill(Board.COMPONENT_COLOR);
        text.setFont(labelFont);
        text.setOpacity(0.8);
        text.setWrappingWidth(outline.getWidth() + 2000);
        text.setTextAlignment(TextAlignment.CENTER);
        groupValue.getChildren().clear();
        groupValue.getChildren().add(text);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        outline.setStroke(selected ? Color.BLUE : Board.COMPONENT_COLOR);
        outline.setStrokeWidth(selected ? 20 : 10);
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

        result.add(new Label("Value"), 0, 2);
        TextField value = new TextField("geoff");
        value.setId("value");
        result.add(value, 1, 2);

        return result;
    }

    @Override
    public void configureFromDialog(Node dialog) {
        pins = ((Spinner<Integer>) dialog.lookup("#pins")).getValue();
        span = ((Spinner<Integer>) dialog.lookup("#span")).getValue();
        value = ((TextField) dialog.lookup("#value")).getText();

        draw();
    }
}
