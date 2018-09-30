package easyvero;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.scene.transform.Translate;

public class EasyVero extends Application {

    public final static String SELECT_ID = "select";
    public final static String WIRE_ID = "wire";
    public final static String DIL_ID = "dil";

    private final static ToggleGroup toolGroup = new ToggleGroup();

    public static String getSelectedTool() {
        return (String) (toolGroup.getSelectedToggle().getUserData());
    }

    @Override
    public void start(Stage stage) {

        BorderPane main = new BorderPane();

        // The design area in the centre
        Board board = new Board(30, 20);
        BorderPane.setMargin(board, new Insets(20, 20, 20, 20));
        main.setCenter(board);

        // A toolbar at the top
        RadioButton selectButton = new RadioButton("Select");
        selectButton.setToggleGroup(toolGroup);
        selectButton.setUserData(SELECT_ID);

        RadioButton wireButton = new RadioButton("Wire");
        wireButton.setToggleGroup(toolGroup);
        wireButton.setUserData(WIRE_ID);

        RadioButton DILButton = new RadioButton("DIL");
        DILButton.setToggleGroup(toolGroup);
        DILButton.setUserData(DIL_ID);

        toolGroup.selectToggle(selectButton);

        main.setTop(new ToolBar(selectButton, wireButton, DILButton));

        Scene scene = new Scene(main);

        scene.setOnKeyPressed(keyEvent -> {
            board.handleKeyPressed(keyEvent);
        });

        stage.setTitle("EasyVero");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
