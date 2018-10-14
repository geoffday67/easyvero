package component;

import static component.Component.valueFont;
import easyvero.Board;
import easyvero.ConnectionPoint;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

public class SIL extends Component {

    private int pins = 0;
    private Rectangle outline = new Rectangle();

    public int getPins() {
        return pins;
    }

    public void setPins(int pins) {
        this.pins = pins;
        draw();
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        drawValue();
    }

    private void draw() {
        List<ConnectionPoint> connections = new ArrayList<>();
        for (int n = 0; n < pins; n++) {
            connections.add(new ConnectionPoint(0, n));
        }
        setConnectionDrawables(connections);

        outline = new Rectangle();
        outline.setX(-50);
        outline.setY(-40);
        outline.setWidth(100);
        outline.setHeight(pins * 100);
        outline.setFill(Color.TRANSPARENT);
        outline.setStroke(Board.COMPONENT_COLOR);
        outline.setStrokeWidth(10);
        groupOutline.getChildren().clear();
        groupOutline.getChildren().add(outline);

        drawValue();
    }

    private void drawValue() {
        if (pins == 0) {
            return;
        }

        Text text = new Text(value);
        text.setFont(valueFont);
        text.setFill(Board.VALUE_COLOUR);

        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.setMinWidth((pins - 1) * 100);
        box.setMinHeight(text.getBoundsInLocal().getHeight() + 100);
        box.getTransforms().add(new Rotate(90));
        box.getChildren().add(text);

        groupValue.getChildren().clear();
        groupValue.getChildren().add(box);
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

        result.add(new javafx.scene.control.Label("Pins"), 0, 0);
        Spinner<Integer> pinsSpinner = new Spinner(2, 100, 3, 1);
        pinsSpinner.setId("pins");
        pinsSpinner.setEditable(true);
        if (pins > 0) {
            pinsSpinner.getValueFactory().setValue(pins);
        }
        result.add(pinsSpinner, 1, 0);

        result.add(new javafx.scene.control.Label("Value"), 0, 2);
        TextField valueText = new TextField(value);
        valueText.setId("value");
        result.add(valueText, 1, 2);

        Platform.runLater(() -> pinsSpinner.requestFocus());

        return result;
    }

    @Override
    public void configureFromDialog(Node dialog) {
        setPins(((Spinner<Integer>) dialog.lookup("#pins")).getValue());
        setValue(((TextField) dialog.lookup("#value")).getText());
    }
}
