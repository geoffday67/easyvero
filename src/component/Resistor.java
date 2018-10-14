package component;

import easyvero.Board;
import easyvero.ConnectionPoint;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

public class Resistor extends Component {

    private Path outline;

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        draw();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        draw();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        draw();
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        outline.setStroke(selected ? Color.BLUE : Board.COMPONENT_COLOR);
        outline.setStrokeWidth(selected ? 20 : 10);
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        drawValue();
    }

    private int getEndX() {
        return width - 1;
    }

    private int getEndY() {
        return height - 1;
    }

    private void draw() {
        if (width == 0 || height == 0) {
            return;
        }

        connections.clear();
        connections.add(new ConnectionPoint(0, 0));
        connections.add(new ConnectionPoint(width - 1, height - 1));
        setConnectionDrawables();

        double dx = width - 1;
        double dy = height - 1;

        outline = new Path();

        // First segment from start
        outline.getElements().add(new MoveTo(0, 0));
        outline.getElements().add(getRelativeLine(dx / 4, dy / 4));

        // Centre section
        outline.getElements().add(getRelativeMove(dy / 20, -dx / 20));
        outline.getElements().add(getRelativeLine(-dy / 10, dx / 10));
        outline.getElements().add(getRelativeLine(dx / 2, dy / 2));
        outline.getElements().add(getRelativeLine(dy / 10, -dx / 10));
        outline.getElements().add(getRelativeLine(-dx / 2, -dy / 2));

        // Last segment
        outline.getElements().add(new MoveTo(dx * 100, dy * 100));
        outline.getElements().add(getRelativeLine(-dx / 4, -dy / 4));

        outline.setStrokeWidth(10);
        outline.setStroke(Board.PAD_COLOUR);
        groupOutline.getChildren().clear();
        groupOutline.getChildren().add(outline);

        drawValue();
    }

    private LineTo getRelativeLine(double dx, double dy) {
        LineTo result = new LineTo();
        result.setAbsolute(false);
        result.setX(dx * 100);
        result.setY(dy * 100);
        return result;
    }

    private MoveTo getRelativeMove(double dx, double dy) {
        MoveTo result = new MoveTo();
        result.setAbsolute(false);
        result.setX(dx * 100);
        result.setY(dy * 100);
        return result;
    }

    private void drawValue() {
        if (width == 0 || height == 0) {
            return;
        }

        Text text = new Text(value);
        text.setFont(valueFont);
        text.setFill(Board.VALUE_COLOUR);

        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);

        double length = Math.sqrt((getEndX() * getEndX()) + (getEndY() * getEndY()));
        box.setMinWidth(length * 100);
        box.setMinHeight(text.getBoundsInLocal().getHeight() + 80);

        double angle = Math.acos(getEndX() / length);
        box.getTransforms().add(new Rotate(Math.toDegrees(angle), 0, 0));

        box.getChildren().add(text);
        groupValue.getChildren().clear();
        groupValue.getChildren().add(box);
    }

    @Override
    public Pane getDialog() {
        GridPane result = new GridPane();
        result.setHgap(10);
        result.setVgap(10);

        result.add(new javafx.scene.control.Label("Value"), 0, 0);
        TextField valueText = new TextField(value);
        valueText.setId("value");
        result.add(valueText, 1, 0);

        Platform.runLater(() -> valueText.requestFocus());

        return result;
    }

    @Override
    public void configureFromDialog(Node dialog) {
        setValue(((TextField) dialog.lookup("#value")).getText());
    }
}
