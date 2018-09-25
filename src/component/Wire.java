package component;

import easyvero.Board;
import easyvero.ConnectionPoint;
import easyvero.GridPoint;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Wire extends Component {

    private Line outline = null;

    public Wire(int x, int y) {
        super(x, y);
    }

    public Wire (GridPoint start, GridPoint end) {
        super(start, end);
        
        connections.add(new ConnectionPoint(0, 0));
        connections.add(new ConnectionPoint(end.getX() - start.getX(), end.getY() - start.getY()));

        setConnectionDrawables();

        outline = new Line(0, 0, end.getX() - start.getX(), end.getY() - start.getY());
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
}
