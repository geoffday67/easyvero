package easyvero;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import component.Break;
import component.Component;
import component.DIL;
import component.Wire;
import static easyvero.EasyVero.objectMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;

public class Board {

    public static final double SCALE_FACTOR = 20.0;
    public static final double PAD_SIZE = 0.8;
    public static final double HOLE_RADIUS = 0.3;
    public static final Color ROW_COLOUR = Color.LIGHTCORAL;
    public static final Color PAD_COLOUR = Color.GREEN;
    public static final Color COMPONENT_COLOR = Color.GREEN;

    @JsonIgnore
    private Pane boardGroup;

    @JsonIgnore
    public Pane getGroup() {
        return boardGroup;
    }

    private int width;
    private int height;

    private List<Component> components;

    public List<Component> getComponents() {
        return components;
    }

    @JsonSetter("components")
    public void setComponents(List<Component> value) {
        for (Component component : value) {
            addComponent(component);
        }
    }

    @JsonIgnore
    private GridPoint startDrag;
    @JsonIgnore
    private boolean moveInProgress = false;
    @JsonIgnore
    private double moveStartX;
    @JsonIgnore
    private double moveStartY;
    @JsonIgnore
    private int componentStartX;
    @JsonIgnore
    private int componentStartY;

    @JsonCreator
    Board(@JsonProperty("width") int width, @JsonProperty("height") int height) {
        // Construct the display region
        boardGroup = new Pane();

        // And the list of components
        components = new ArrayList<>();

        this.width = width;
        this.height = height;

        // Draw the horizontal rows
        for (int h = 0; h < height; h++) {
            Line line = new Line(0, h, width - 1, h);
            line.setStroke(ROW_COLOUR);
            line.setStrokeWidth(0.75);
            boardGroup.getChildren().add(line);
        }

        // Draw the holes, and mouse handlers
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                Circle hole = new Circle(w, h, HOLE_RADIUS, Color.WHITE);

                // Start dragging to create a component
                hole.setOnDragDetected(event -> {
                    startDrag = new GridPoint(event.getSource());
                    hole.startFullDrag();
                });

                // (Use DragEntered to draw the drag path)
                // Stop dragging to create a component
                hole.setOnMouseDragReleased(event -> {
                    GridPoint position = new GridPoint(event.getSource());
                    Component component = createComponent(startDrag.getX(), startDrag.getY(), position.getX() - startDrag.getX() + 1, position.getY() - startDrag.getY() + 1);
                    if (component != null) {
                        addComponent(component);
                    }
                });

                // Click to create a component, or select nothing
                hole.setOnMouseClicked(event -> {
                    GridPoint position = new GridPoint(event.getSource());
                    Component component = createComponent(position.getX(), position.getY());
                    if (component != null) {
                        addComponent(component);
                    }
                });

                boardGroup.getChildren().add(hole);
            }
        }

        // Scale the board for display
        Scale scale = new Scale(SCALE_FACTOR, SCALE_FACTOR, 0.0, 0.0);
        boardGroup.getTransforms().add(scale);

        boardGroup.setPrefWidth((width - 1) * SCALE_FACTOR);
        boardGroup.setPrefHeight((height - 1) * SCALE_FACTOR);

        /*Wire wire = new Wire();
        wire.setPosition(2, 2);
        wire.setSize(3, 3);
        addComponent(wire);*/
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void selectNone() {
        for (Component component : components) {
            component.setSelected(false);
        }
    }

    private void selectComponent(Component target) {
        for (Component component : components) {
            component.setSelected(component == target);
        }
    }

    private Component getSelectedComponent() {
        for (Component component : components) {
            if (component.isSelected()) {
                return component;
            }
        }

        return null;
    }

    private void addComponent(Component component) {
        component.getDrawable().setOnMousePressed(event -> {
            selectComponent(component);
        });

        // Store initial positions so we can move the component later
        component.getDrawable().setOnDragDetected(event -> {
            // Get the drag start position (scaled)
            moveStartX = event.getSceneX();
            moveStartY = event.getSceneY();

            // Get the component position at drag start (unscaled)
            componentStartX = component.getX();
            componentStartY = component.getY();

            moveInProgress = true;
        });

        // Move the component based on the difference between now and the start
        component.getDrawable().setOnMouseDragged(event -> {
            if (moveInProgress) {
                int dx = (int) Math.floor(((event.getSceneX() - moveStartX) / SCALE_FACTOR));
                int dy = (int) Math.floor(((event.getSceneY() - moveStartY) / SCALE_FACTOR));
                //System.out.printf("Moving by %d, %d\n", dx, dy);
                component.setPosition(componentStartX + dx, componentStartY + dy);
            }
        });

        // Stop moving when the drag is stopped
        component.getDrawable().setOnMouseReleased(event -> {
            moveInProgress = false;
        });

        components.add(component);
        boardGroup.getChildren().add(component.getDrawable());
    }

    private void deleteComponent(Component component) {
        components.remove(component);
        boardGroup.getChildren().remove(component);
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case DELETE:
                Component component = getSelectedComponent();
                if (component != null) {
                    deleteComponent(component);
                }
                break;
        }
    }

    private Component createComponent(int x0, int y0) {
        return createComponent(x0, y0, 0, 0);
    }

    private Component createComponent(int x0, int y0, int x1, int y1) {
        Component target = null;
        switch (EasyVero.getSelectedTool()) {
            case EasyVero.BREAK_ID:
                target = new Break();
                break;

            case EasyVero.DIL_ID:
                target = new DIL();
                break;

            case EasyVero.WIRE_ID:
                target = new Wire();
                break;
        }
        if (target == null) {
            return null;
        }

        target.setPosition(x0, y0);
        target.setSize(x1, y1);

        final Dialog<ButtonType> dialog = new Dialog<>();

        // Get the component's config dialog if any
        final Pane content = target.getDialog();
        if (content == null) {
            return target;
        }

        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.setTitle("New component");
        Optional<ButtonType> result = dialog.showAndWait();

        if (result != null && result.isPresent() && result.get() == ButtonType.OK) {
            target.configureFromDialog(content);
            return target;
        }

        return null;
    }

    public void save(OutputStream output) throws IOException {
        try {
            objectMapper.writeValue(output, this);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Component.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
