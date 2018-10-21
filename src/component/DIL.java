package component;

import easyvero.Board;
import easyvero.ConnectionPoint;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class DIL extends Component {

    private int pins = 0;   // Total number of pins, so must be a even number
    private int span = 0;   // The number of holes between the legs
    private Rectangle outline;

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

    @Override
    public void setValue(String value) {
        super.setValue(value);
        drawValue();
    }

    private void draw() {
        if (pins == 0 || span == 0) {
            return;
        }

        connections.clear();
        for (int n = 0; n < pins / 2; n++) {
            connections.add(new ConnectionPoint(0, n));
        }
        for (int n = 0; n < pins / 2; n++) {
            connections.add(new ConnectionPoint(span + 1, n));
        }
        setConnectionDrawables();

        outline = new Rectangle();
        outline.setX(-50);
        outline.setY(-40);
        outline.setWidth((span + 2) * 100);
        outline.setHeight((pins / 2) * 100);
        outline.setFill(Color.TRANSPARENT);
        outline.setStroke(Board.COMPONENT_COLOR);
        outline.setStrokeWidth(10);
        groupOutline.getChildren().clear();
        groupOutline.getChildren().add(outline);

        drawValue();
    }

    private void drawValue() {
        if (pins == 0 || span == 0) {
            return;
        }

        Text text = new Text(outline.getX() - 1000, 100, value);
        text.setFill(Board.VALUE_COLOUR);
        text.setFont(valueFont);
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
        Spinner<Integer> pinsSpinner = new Spinner<>(6, 100, 6, 2);
        pinsSpinner.setId("pins");
        //pinsSpinner.setEditable(true);
        if (pins > 0) {
            pinsSpinner.getValueFactory().setValue(pins);
        }
        result.add(pinsSpinner, 1, 0);

        result.add(new Label("Span"), 0, 1);
        Spinner<Integer> spanSpinner = new Spinner<>(1, 100, 2);
        spanSpinner.setId("span");
        //spanSpinner.setEditable(true);
        if (span > 0) {
            spanSpinner.getValueFactory().setValue(span);
        }
        result.add(spanSpinner, 1, 1);

        result.add(new Label("Value"), 0, 2);
        TextField valueText = new TextField(value);
        valueText.setId("value");
        result.add(valueText, 1, 2);

        Platform.runLater(() -> pinsSpinner.requestFocus());

        return result;
    }

    @Override
    public void configureFromDialog(Node dialog) {
        setPins (((Spinner<Integer>) dialog.lookup("#pins")).getValue());
        setSpan (((Spinner<Integer>) dialog.lookup("#span")).getValue());
        setValue (((TextField) dialog.lookup("#value")).getText());
    }
}
