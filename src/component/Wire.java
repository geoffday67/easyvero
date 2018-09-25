package component;

import easyvero.Board;
import easyvero.ConnectionPoint;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Wire extends Component {

    private Line outline = null;

    public Wire(int x, int y) {
        super(x, y);
    }

    @Override
    public void configure(Object configObject) {
        Config config = (Config) configObject;

        connections.clear();
        connections.add(new ConnectionPoint(0, 0));
        connections.add(new ConnectionPoint(0, 3));

        setConnectionDrawables();

        outline = new Line(0, 0, 0, 3);
        outline.setOpacity(0.8);
        outline.setStroke(Board.COMPONENT_COLOR);
        outline.setStrokeWidth(0.1);
        getChildren().add(outline);
    }

    @Override
    public void setSelected(boolean selected) {
        if (outline != null) {
            outline.setStroke(selected ? Color.BLUE : Board.COMPONENT_COLOR);
            outline.setStrokeWidth(selected ? 0.2 : 0.1);
        }
    }

    @Override
    public Pane getComponentDialog() {
        GridPane result = new GridPane();
        result.setHgap(10);
        result.setVgap(10);

        result.add(new Label("Geoff is great!"), 0, 0);

        return result;
    }

    @Override
    public Object getConfigFromDialog(Node dialog) {
        Config config = new Config();
        return config;
    }

    /*public static Wire createComponentFromConfig(int x, int y, Object config) {
        Wire wire = new Wire (x, y);
        wire.configure((Wire.Config) config);
        return wire;
    }*/

    public static class Config {
    }
}
