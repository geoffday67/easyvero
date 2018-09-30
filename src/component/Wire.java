package component;

import easyvero.Board;
import easyvero.ConnectionPoint;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Wire extends Component {

    private final Line outline = new Line();

    @Override
    public void setSize(int width, int height) {
        // 'Size' for us represents the end point, set the connection points and outline
        // No further configuration supported

        super.setSize(width, height);
        
        connections.clear();
        connections.add(new ConnectionPoint(0, 0));
        connections.add(new ConnectionPoint(width - 1, height - 1));
        setConnectionDrawables();

        groupOutline.getChildren().clear();
        outline.setStartX(0);
        outline.setStartY(0);
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
