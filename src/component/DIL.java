package component;

import easyvero.Board;
import easyvero.ConnectionPoint;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DIL extends Component {

    private Rectangle outline = new Rectangle();

    @Override
    public void configure(Object configObject) {
        Config config = (Config) configObject;

        connections.clear();
        for (int n = 0; n < config.size / 2; n++) {
            connections.add(new ConnectionPoint(0, n));
        }
        for (int n = 0; n < config.size / 2; n++) {
            connections.add(new ConnectionPoint(config.gap + 1, n));
        }
        setConnectionDrawables();

        groupOutline.getChildren().clear();
        outline.setX(-0.5);
        outline.setY(-0.5);
        outline.setWidth(config.gap + 2.0);
        outline.setHeight(config.size / 2);
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

        result.add(new Label("Gap"), 0, 1);
        Spinner gap = new Spinner(1, 100, 2);
        gap.setId("gap");
        result.add(gap, 1, 1);

        return result;
    }

    @Override
    public Object getConfigFromDialog(Node dialog) {
        Config config = new Config();
        config.size = ((Spinner<Integer>) dialog.lookup("#pins")).getValue();
        config.gap = ((Spinner<Integer>) dialog.lookup("#gap")).getValue();
        return config;
    }

    public static class Config {

        private int size;
        private int gap;
    }
}
