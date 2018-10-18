package component;

import static component.Component.valueFont;
import easyvero.Board;
import easyvero.ConnectionPoint;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/*
A two pin terminal covers 3 x 5 with pins at (1, 1) and (1, 3)
*/

public class Terminal extends Component {

    private int pins = 0;
    private Rectangle outline;

    public int getPins() {
        return pins;
    }

    public void setPins(int pins) {
        this.pins = pins;
        draw();
    }

    private void draw() {
        if (pins == 0) {
            return;
        }

        connections.clear();
        for (int n = 0; n < pins; n++) {
            connections.add(new ConnectionPoint(1, (n * 2) + 1));
        }
        setConnectionDrawables();

        outline = new Rectangle();
        outline.setX(0);
        outline.setY(0);
        outline.setWidth(2 * 100);
        outline.setHeight((pins * 2) * 100);
        outline.setFill(Color.TRANSPARENT);
        outline.setStroke(Board.COMPONENT_COLOR);
        outline.setStrokeWidth(10);
        groupOutline.getChildren().clear();
        groupOutline.getChildren().add(outline);
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
        Spinner<Integer> pinsSpinner = new Spinner(2, 100, 2, 1);
        pinsSpinner.setId("pins");
        pinsSpinner.setEditable(true);
        if (pins > 0) {
            pinsSpinner.getValueFactory().setValue(pins);
        }
        result.add(pinsSpinner, 1, 0);

        Platform.runLater(() -> pinsSpinner.requestFocus());

        return result;
    }

    @Override
    public void configureFromDialog(Node dialog) {
        setPins(((Spinner<Integer>) dialog.lookup("#pins")).getValue());
    }
}
