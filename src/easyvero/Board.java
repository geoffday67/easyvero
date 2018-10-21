package easyvero;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import component.Break;
import component.Component;
import component.DIL;
import component.Label;
import component.Resistor;
import component.SIL;
import component.Terminal;
import component.Wire;
import static easyvero.EasyVero.objectMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
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
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.transform.Scale;

public class Board {

    public static final double SCALE_FACTOR = 0.2f;
    public static final double PAD_SIZE = 80;
    public static final double HOLE_RADIUS = 30;
    public static final Color ROW_COLOUR = Color.BLANCHEDALMOND;
    public static final Color PAD_COLOUR = Color.GREEN;
    public static final Color COMPONENT_COLOR = Color.GREEN;
    public static final Color VALUE_COLOUR = Color.BLUE;

    private Pane boardGroup;
    private Group traceGroup;

    @JsonIgnore
    public Pane getGroup() {
        return boardGroup;
    }

    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

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

    private int dragStartX;
    private int dragStartY;
    private boolean moveInProgress = false;
    private double moveStartX;
    private double moveStartY;
    private int componentStartX;
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
            Line line = new Line(0, h * 100, (width - 1) * 100, h * 100);
            line.setStroke(ROW_COLOUR);
            line.setStrokeWidth(75);
            boardGroup.getChildren().add(line);
        }

        // Draw the holes, add mouse handlers
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                Circle hole = new Circle(w * 100, h * 100, HOLE_RADIUS, Color.WHITE);

                // Start dragging to create a component
                hole.setOnDragDetected(event -> {
                    dragStartX = snapX(event.getX());
                    dragStartY = snapY(event.getY());
                    hole.startFullDrag();
                });

                // (Use DragEntered to draw the drag path)
                // Stop dragging to create a component
                hole.setOnMouseDragReleased(event -> {
                    int dragEndX = snapX(event.getX());
                    int dragEndY = snapY(event.getY());
                    Component component = createComponent(dragStartX, dragStartY, dragEndX - dragStartX + 1, dragEndY - dragStartY + 1);
                    if (component != null) {
                        addComponent(component);
                    }
                });

                // Click to create a component
                hole.setOnMouseClicked(event -> {
                    int x = snapX(event.getX());
                    int y = snapY(event.getY());
                    if (EasyVero.getSelectedTool() == EasyVero.TRACE_ID) {
                        trace(x, y);
                    } else {
                        Component component = createComponent(x, y);
                        if (component != null) {
                            addComponent(component);
                        }
                    }

                    selectNone();
                });

                boardGroup.getChildren().add(hole);
            }
        }

        traceGroup = new Group();
        //traceGroup.getTransforms().add(new Scale(100, 100, 0, 0));
        boardGroup.getChildren().add(traceGroup);

        // Scale the board for display
        Scale scale = new Scale(SCALE_FACTOR, SCALE_FACTOR, 0.0, 0.0);
        boardGroup.getTransforms().add(scale);

        boardGroup.setPrefWidth((width - 1) * 100 * SCALE_FACTOR);
        boardGroup.setPrefHeight((height - 1) * 100 * SCALE_FACTOR);
    }

    private int snapX(double raw) {
        return (int) (raw + 50) / 100;
    }

    private int snapY(double raw) {
        return (int) (raw + 50) / 100;
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

        component.getDrawable().setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                configureComponent(component);
            }
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
                int dx = (int) Math.floor(((event.getSceneX() - moveStartX) / SCALE_FACTOR / 100));
                int dy = (int) Math.floor(((event.getSceneY() - moveStartY) / SCALE_FACTOR / 100));
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
        boardGroup.getChildren().remove(component.getDrawable());
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case BACK_SPACE:
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

            case EasyVero.SIL_ID:
                target = new SIL();
                break;

            case EasyVero.WIRE_ID:
                target = new Wire();
                break;

            case EasyVero.TEXT_ID:
                target = new Label();
                break;

            case EasyVero.RESISTOR_ID:
                target = new Resistor();
                break;

            case EasyVero.TERMINAL_ID:
                target = new Terminal();
                break;
        }
        if (target == null) {
            return null;
        }

        target.setPosition(x0, y0);
        target.setSize(x1, y1);

        if (configureComponent(target)) {
            return target;
        }

        return null;
    }

    private boolean configureComponent(Component component) {
        final Pane content = component.getDialog();
        if (content == null) {
            return true;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.setTitle("Configure component");
        Optional<ButtonType> result = dialog.showAndWait();

        if (result != null && result.isPresent() && result.get() == ButtonType.OK) {
            component.configureFromDialog(content);
            return true;
        }

        return false;
    }

    public void save(OutputStream output) throws IOException {
        try {
            objectMapper.writeValue(output, this);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Component.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    List<HorzTraceSegment> traceFound = new ArrayList<>();

    private void trace(int x, int y) {
        traceGroup.getChildren().clear();
        traceFound.clear();
        traceRow(x, y);
    }

    private void traceRow(int x, int y) {
        traceRowSideways(x, y, -1);
        traceRowSideways(x, y, 1);
    }

    private void traceRowSideways(int x, int y, int dx) {
        Path segment = new Path();
        segment.setStroke(Color.RED);
        segment.setStrokeWidth(10);
        segment.getElements().add(new MoveTo(x * 100, y * 100));
        HorzTraceSegment trace = new HorzTraceSegment(y);
        trace.start = x;

        List<GridPoint> newPoints = new ArrayList<>();

        while (true) {
            // Look at the point to the side of where we are, see how many points to add the trace, removing those which are already known.
            List<GridPoint> points = getTraceablePoints(x + dx, y);
            Iterator<GridPoint> iterator = points.iterator();
            while (iterator.hasNext()) {
                GridPoint point = iterator.next();
                for (HorzTraceSegment existing : traceFound) {
                    if (existing.contains(point.getX(), point.getY())) {
                        iterator.remove();
                    }
                }
            }

            if (points.isEmpty()) {
                // None, we've finished with this row
                break;
            }

            // Look at the connected points, if there are any from another row then start tracing that row later.
            for (GridPoint point : points) {
                if (point.getY() != y) {
                    newPoints.add(point);
                }
            }

            x += dx;
        }

        segment.getElements().add(new LineTo(x * 100, y * 100));
        traceGroup.getChildren().add(segment);

        trace.end = x;
        traceFound.add(trace);

        // Trace new rows after we add this row.
        for (GridPoint point : newPoints) {
            traceRow(point.getX(), point.getY());
        }
    }

    /**
     * Get a list of points to add to the trace. Could be zero, one or more
     * points.
     *
     * @param x The point to test.
     * @param y The point to test.
     * @return The list of points (most likely just this one).
     */
    private List<GridPoint> getTraceablePoints(int x, int y) {
        List<GridPoint> result = new ArrayList<>();
        boolean foundComponent = false;

        if (x < 0 || x >= width) {
            // This point is off the board so return the empty list
            return result;
        }

        // Check the components on the board for any which have a connection point here
        for (Component component : components) {
            int component_x = x - component.getX();
            int component_y = y - component.getY();
            List<ConnectionPoint> points = component.getConnectedPoints(component_x, component_y);

            if (points != null) {
                // The component has something to say, because this point matched one of its connection points
                for (ConnectionPoint point : points) {
                    int grid_x = point.x + component.getX();
                    int grid_y = point.y + component.getY();
                    result.add(new GridPoint(grid_x, grid_y));
                }
                foundComponent = true;
            }
        }

        // If the components had nothing to contribute just return the test point
        if (!foundComponent) {
            result.add(new GridPoint(x, y));
        }

        return result;
    }
}
