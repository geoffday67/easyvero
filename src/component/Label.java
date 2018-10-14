package component;

import easyvero.Board;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Label extends Component {

    private HBox box = new HBox();
    private Text text = new Text();

    public Label() {
        text.setFont(valueFont);
        text.setFill(Board.VALUE_COLOUR);
        
        box.setMinHeight(200);
        box.setLayoutY(-100);
        box.setLayoutX(50);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().add(text);

        groupOutline.getChildren().add(box);
    }

    private void draw() {
        text.setText(value);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        text.setFill(selected ? Color.BLUE : Board.VALUE_COLOUR);
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        draw();
    }    
    
    @Override
    public Pane getDialog() {
        GridPane result = new GridPane();
        result.setHgap(10);
        result.setVgap(10);

        result.add(new javafx.scene.control.Label("Text"), 0, 0);
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
