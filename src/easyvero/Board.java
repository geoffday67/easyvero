package easyvero;

import component.Component;
import component.DIL;
import component.Wire;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;

public class Board extends Region {

    public static final double SCALE_FACTOR = 20.0;
    public static final double PAD_SIZE = 0.8;
    public static final double HOLE_RADIUS = 0.3;
    public static final Color ROW_COLOUR = Color.LIGHTCORAL;
    public static final Color PAD_COLOUR = Color.GREEN;
    public static final Color COMPONENT_COLOR = Color.GREEN;

    private static final double INVALID_POSITION = -99.0;

    private int width;
    private int height;
    private List<Component> components;

    private GridPoint startDrag;

    private boolean moveInProgress = false;
    private double moveStartX;
    private double moveStartY;
    private int componentStartX;
    private int componentStartY;

    Board(int width, int height) {
        components = new ArrayList<>();

        this.width = width;
        this.height = height;

        // Draw the horizontal rows
        for (int h = 0; h < height; h++) {
            Line line = new Line(0, h, width - 1, h);
            line.setStroke(ROW_COLOUR);
            line.setStrokeWidth(0.75);
            getChildren().add(line);
        }

        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                Circle hole = new Circle(w, h, HOLE_RADIUS, Color.WHITE);

                // Start dragging to create a component
                hole.setOnDragDetected(event -> {
                    startDrag = new GridPoint(event.getSource());
                    hole.startFullDrag();
                });

                // Use DragEntered to draw the drag path
                // Stop dragging to create a component
                hole.setOnMouseDragReleased(event -> {
                    GridPoint position = new GridPoint(event.getSource());
                    Component component = createComponent(startDrag.getX(), startDrag.getY(), position.getX() - startDrag.getX() + 1, position.getY() - startDrag.getY() + 1);
                    if (component != null) {
                        addComponent(component);
                    }
                });

                // Click to create a component
                hole.setOnMouseClicked(event -> {
                    GridPoint position = new GridPoint(event.getSource());
                    Component component = createComponent(position.getX(), position.getY());
                    if (component != null) {
                        addComponent(component);
                    }
                });

                getChildren().add(hole);
            }
        }

        Scale scale = new Scale(SCALE_FACTOR, SCALE_FACTOR, 0.0, 0.0);
        getTransforms().add(scale);
    }

    @Override
    protected double computePrefWidth(double height) {
        return (width - 1) * SCALE_FACTOR;
    }

    @Override
    protected double computePrefHeight(double width) {
        return (height - 1) * SCALE_FACTOR;
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

    private void addComponent(Component component) {
        component.setOnMousePressed(event -> {
            selectComponent(component);
        });

        // Store initial positions so we can move the component later
        component.setOnDragDetected(event -> {
            // Get the drag start position (scaled)
            moveStartX = event.getSceneX();
            moveStartY = event.getSceneY();

            // Get the component position at drag start (unscaled)
            componentStartX = component.getX();
            componentStartY = component.getY();

            moveInProgress = true;
        });

        // Move the component based on the difference between now and the start
        component.setOnMouseDragged(event -> {
            if (moveInProgress) {
                int dx = (int) Math.floor(((event.getSceneX() - moveStartX) / SCALE_FACTOR));
                int dy = (int) Math.floor(((event.getSceneY()- moveStartY) / SCALE_FACTOR));
                //System.out.printf("Moving by %d, %d\n", dx, dy);
                component.setPosition(componentStartX + dx, componentStartY + dy);
            }
        });

        // Stop moving when the drag is stopped
        component.setOnMouseReleased(event -> {
            moveInProgress = false;
        });

        components.add(component);
        getChildren().add(component);
    }

    private Component createComponent(int x0, int y0) {
        return createComponent(x0, y0, 0, 0);
    }

    private Component createComponent(int x0, int y0, int x1, int y1) {
        Component target = null;
        switch (EasyVero.getSelectedTool()) {
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

        final Component final_target = target;
        final Dialog<Object> dialog = new Dialog<>();

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
        //dialog.setHeaderText("DIL");
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return final_target.getConfigFromDialog(content);
            } else {
                return null;
            }
        });
        Optional<Object> result = dialog.showAndWait();

        if (result != null && result.isPresent()) {
            target.configure(result.get());
            return target;
        }

        return null;
    }
}
