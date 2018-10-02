package easyvero;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EasyVero extends Application {

    public final static String SELECT_ID = "select";
    public final static String BREAK_ID = "break";
    public final static String WIRE_ID = "wire";
    public final static String DIL_ID = "dil";

    private final static ToggleGroup toolGroup = new ToggleGroup();

    public static String getSelectedTool() {
        return (String) (toolGroup.getSelectedToggle().getUserData());
    }

    public static ObjectMapper objectMapper = new ObjectMapper();

    private Board board;
    private BorderPane main;

    @Override
    public void start(Stage stage) {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        //objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

        main = new BorderPane();

        // The design area in the centre
        board = new Board(30, 20);
        Pane boardGroup = board.getGroup();
        BorderPane.setMargin(boardGroup, new Insets(20, 20, 20, 20));
        main.setCenter(boardGroup);

        // Toolbar
        RadioButton selectButton = new RadioButton("Select");
        selectButton.setToggleGroup(toolGroup);
        selectButton.setUserData(SELECT_ID);

        RadioButton breakButton = new RadioButton("Break");
        breakButton.setToggleGroup(toolGroup);
        breakButton.setUserData(BREAK_ID);

        RadioButton wireButton = new RadioButton("Wire");
        wireButton.setToggleGroup(toolGroup);
        wireButton.setUserData(WIRE_ID);

        RadioButton DILButton = new RadioButton("DIL");
        DILButton.setToggleGroup(toolGroup);
        DILButton.setUserData(DIL_ID);

        toolGroup.selectToggle(selectButton);
        ToolBar toolBar = new ToolBar(selectButton, breakButton, wireButton, DILButton);

        // Menu
        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(event -> save());
        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(event -> open());
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(saveItem, openItem);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu);

        main.setTop(new VBox(menuBar, toolBar));

        Scene scene = new Scene(main);
        scene.setOnKeyPressed(keyEvent -> {
            board.handleKeyPressed(keyEvent);
        });

        stage.setTitle("EasyVero");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    private void save() {
        try (OutputStream output = new FileOutputStream("/Users/geoffday/Desktop/board.ev")) {
            board.save(output);
        } catch (IOException e) {
            System.err.print(e.getMessage());
        }
    }

    private void open() {
        try (InputStream input = new FileInputStream("/Users/geoffday/Desktop/board.ev")) {
            board = objectMapper.readValue(input, Board.class);
            Pane boardGroup = board.getGroup();
            BorderPane.setMargin(boardGroup, new Insets(20, 20, 20, 20));
            main.setCenter(boardGroup);
        } catch (IOException e) {
            System.err.print(e.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
