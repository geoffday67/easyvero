package component;

import easyvero.Board;
import easyvero.ConnectionPoint;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class Break extends Component {

    private Path outline;

    public Break() {
        draw();
    }

    @Override
    protected List<ConnectionPoint> getConnections(ConnectionPoint connection) {
        return new ArrayList<>();
    }

    private void draw() {
        connections.clear();
        connections.add(new ConnectionPoint(0, 0));

        groupOutline.getChildren().clear();
        outline = new Path();
        outline.setLayoutX(-35);
        outline.setLayoutY(-35);
        outline.getElements().add(new MoveTo(0, 0));
        outline.getElements().add(new LineTo(70, 70));
        outline.getElements().add(new MoveTo(70, 0));
        outline.getElements().add(new LineTo(0, 70));
        outline.setStroke(Board.PAD_COLOUR);
        outline.setStrokeWidth(10);
        groupOutline.getChildren().add(outline);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        outline.setStroke(selected ? Color.BLUE : Board.PAD_COLOUR);
    }
}
