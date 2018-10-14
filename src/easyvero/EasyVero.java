package easyvero;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class EasyVero extends Application {

    public final static String SELECT_ID = "select";
    public final static String BREAK_ID = "break";
    public final static String WIRE_ID = "wire";
    public final static String DIL_ID = "dil";
    public final static String SIL_ID = "sil";
    public final static String TEXT_ID = "text";
    public final static String RESISTOR_ID = "resistor";

    private final static ToggleGroup toolGroup = new ToggleGroup();

    public static String getSelectedTool() {
        return (String) (toolGroup.getSelectedToggle().getUserData());
    }

    public static ObjectMapper objectMapper = new ObjectMapper();

    private Stage stage;
    private Board board;
    private BorderPane main;
    private File boardFile;

    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    public void showError(String title, Throwable exception) {
        showError(title, exception.getMessage());
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

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

        RadioButton SILButton = new RadioButton("SIL");
        SILButton.setToggleGroup(toolGroup);
        SILButton.setUserData(SIL_ID);

        RadioButton TextButton = new RadioButton("Text");
        TextButton.setToggleGroup(toolGroup);
        TextButton.setUserData(TEXT_ID);

        RadioButton ResistorButton = new RadioButton("Resistor");
        ResistorButton.setToggleGroup(toolGroup);
        ResistorButton.setUserData(RESISTOR_ID);

        toolGroup.selectToggle(selectButton);
        ToolBar toolBar = new ToolBar(selectButton, breakButton, wireButton, DILButton, SILButton, TextButton, ResistorButton);

        // Menu
        MenuItem newItem = new MenuItem("New");
        newItem.setOnAction(event -> newBoard());
        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(event -> open());
        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(event -> save());
        MenuItem saveAsItem = new MenuItem("Save as");
        saveAsItem.setOnAction(event -> saveAs());
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(newItem, new SeparatorMenuItem(), openItem, new SeparatorMenuItem(), saveItem, saveAsItem);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu);

        main.setTop(new VBox(menuBar, toolBar));

        Scene scene = new Scene(main);
        scene.setOnKeyPressed(keyEvent -> {
            board.handleKeyPressed(keyEvent);
        });

        stage.setResizable(false);
        stage.setTitle("EasyVero");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setOnCloseRequest(ignore -> handleClose());
        stage.show();
        
        loadBoardFile();
    }

    private void handleClose() {
        // Show choice to save changes or discard before quitting
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Save changes?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result != null && result.get() == ButtonType.OK) {
            save();
        }
    }

    private void setBoardFile(File file) {
        boardFile = file;
        Preferences.userRoot().put("boardfile", file.getAbsolutePath());
    }

    private void loadBoardFile() {
        String filename = Preferences.userRoot().get("boardfile", "");
        if (filename.length() > 0) {
            loadFromFile(new File(filename));
        }

    }

    private void setBoard() {
        // Add the board to the centre of the main pane
        Pane boardGroup = board.getGroup();
        BorderPane.setMargin(boardGroup, new Insets(20, 20, 20, 20));
        main.setCenter(boardGroup);

        // Size the display area to match
        stage.sizeToScene();
    }

    private void newBoard() {
        GridPane content = new GridPane();
        content.setHgap(10);
        content.setVgap(10);

        content.add(new Label("Width"), 0, 0);
        TextField width = new TextField();
        content.add(width, 1, 0);

        content.add(new Label("Height"), 0, 1);
        TextField height = new TextField();
        content.add(height, 1, 1);

        content.setPadding(new Insets(20));
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.setTitle("New board");
        Optional<ButtonType> result = dialog.showAndWait();

        if (result != null && result.isPresent() && result.get() == ButtonType.OK) {
            int new_width = Integer.parseInt(width.getText());
            int new_height = Integer.parseInt(height.getText());
            System.out.printf("Creating board %d x %d\n", new_width, new_height);
            board = new Board(new_width, new_height);
            setBoard();
        }
    }

    private void saveToFile(File file) {
        System.out.printf("Saving to %s\n", file.getPath());

        try (OutputStream output = new FileOutputStream(file)) {
            board.save(output);
            setBoardFile(file);
        } catch (IOException e) {
            showError("Save board", e);
        }
    }

    private void loadFromFile(File file) {
        System.out.printf("Loading %s\n", file.getPath());

        try (InputStream input = new FileInputStream(file)) {
            board = objectMapper.readValue(input, Board.class);
            setBoard();
            setBoardFile(file);
        } catch (IOException e) {
            showError("Open board", e);
        }
    }

    private void saveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save board as");
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }

        saveToFile(file);
    }

    private void save() {
        if (boardFile == null) {
            saveAs();
            return;
        }

        saveToFile(boardFile);
    }

    private void open() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open board");
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        loadFromFile(file);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
