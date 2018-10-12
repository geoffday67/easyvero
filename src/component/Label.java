package component;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Label extends Component {

    private Text text = new Text("Geoff is great!");

    public Label() {
        text.setFont(new Font(120));
        
        draw();
    }

    private void draw() {
        groupOutline.getChildren().clear();
        groupOutline.getChildren().add(text);
    }
}
