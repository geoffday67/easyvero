package component;

import easyvero.Board;
import easyvero.ConnectionPoint;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Wire extends Component {

    private final Line outline = new Line();

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

    private void draw() {
        List<ConnectionPoint> connections = new ArrayList<>();
        connections.add(new ConnectionPoint(0, 0));
        connections.add(new ConnectionPoint(width - 1, height - 1));
        setConnectionDrawables(connections);

        groupOutline.getChildren().clear();
        outline.setEndX(width - 1);
        outline.setEndY(height - 1);
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
}
