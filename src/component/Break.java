package component;

import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class Break extends Component {

    private Path outline;

    public Break() {
        draw();
    }

    private void draw() {
        groupOutline.getChildren().clear();
        outline = new Path();
        outline.setLayoutX(-0.35);
        outline.setLayoutY(-0.35);
        outline.getElements().add(new MoveTo(0, 0));
        outline.getElements().add(new LineTo(0.7, 0.7));
        outline.getElements().add(new MoveTo(0.7, 0));
        outline.getElements().add(new LineTo(0, 0.7));
        outline.setStroke(Color.BLACK);
        outline.setStrokeWidth(0.2);
        groupOutline.getChildren().add(outline);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        outline.setStroke(selected ? Color.BLUE : Color.BLACK);
    }
}
