/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyvero;

import component.Component;
import component.DIL8;
import component.Wire;
import java.awt.Point;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.Node;
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
    public static final Color PAD_COLOR = Color.GREEN;
    public static final Color COMPONENT_COLOR = Color.GREEN;

    private int width;
    private int height;
    private List<Component> components;

    private GridPoint start;
    private GridPoint end;

    Board(int width, int height) {
        components = new ArrayList<>();

        this.width = width;
        this.height = height;

        for (int h = 0; h < height; h++) {
            Line l = new Line(0, h, width - 1, h);
            l.setStroke(Color.LIGHTCORAL);
            l.setStrokeWidth(0.75);
            getChildren().add(l);
        }

        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                Circle hole = new Circle(w, h, HOLE_RADIUS, Color.WHITE);

                /*
                In 'select' mode watch for drag start on a component.
                    Use an event handler on the component itself or on the board?
                Show component under the drag as selected.
                 */
 /*
                MouseEntered = once per hole
                MouseDragged = starts after clicking, continues to generate events at all positions on original hole
                OnMouseDragEntered = no events (might need triggering?)
                 */
                hole.setOnMouseClicked(event -> {
                    System.out.printf("Clicked for %s at %f, %f\n", event.getSource().toString(), event.getX(), event.getY());
                });

                hole.setOnMouseDragReleased(event -> {
                    end = new GridPoint(event.getSource());
                    System.out.printf("Ended at %d, %d\n", end.getX(), end.getY());

                    Wire wire = new Wire(start, end);
                    components.add(wire);
                    getChildren().add(wire);
                });

                hole.setOnMouseDragEntered(event -> {
                    System.out.printf("Entered for %s at %f, %f\n", event.getSource().toString(), event.getX(), event.getY());
                });

                hole.setOnDragDetected(event -> {
                    start = new GridPoint(event.getSource());
                    System.out.printf("Started at %d, %d\n", start.getX(), start.getY());
                    hole.startFullDrag();
                });

                /*hole.setOnMouseClicked(event -> {
                    int x = (int) Math.floor(event.getX() + HOLE_RADIUS);
                    int y = (int) Math.floor(event.getY() + HOLE_RADIUS);
                    Component component = createComponent(x, y);
                    if (component != null) {
                        component.setName("Some IC");

                        component.setOnMousePressed((e) -> {
                            selectComponent(component);
                        });

                        components.add(component);
                        getChildren().add(component);
                    }
                });*/
                getChildren().add(hole);
            }
        }

        Scale scale = new Scale(SCALE_FACTOR, SCALE_FACTOR, 0.0, 0.0);
        getTransforms().add(scale);
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

    private int gridFromEvent(double e) {
        return (int) (Math.floor(e) + HOLE_RADIUS);
    }

    @Override
    protected double computePrefWidth(double height) {
        return (width - 1) * SCALE_FACTOR;
    }

    @Override
    protected double computePrefHeight(double width) {
        return (height - 1) * SCALE_FACTOR;
    }

    private Component createComponent(int x, int y) {
        Component target = null;
        switch (EasyVero.getSelectedTool()) {
            case EasyVero.WIRE_ID:
                target = new Wire(x, y);
                break;
            case EasyVero.DIL_ID:
                target = new DIL8(x, y);
                break;
        }
        if (target == null) {
            return null;
        }

        final Component final_target = target;

        Dialog<Object> dialog = new Dialog<>();

        // Get the content of the dialog as provided by the component class, add it to the dialog
        final Pane content = target.getComponentDialog();
        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.setTitle("New component");
        //dialog.setHeaderText("DIL");
        dialog.setResultConverter(button -> {
            return final_target.getConfigFromDialog(content);
        });
        Optional<Object> result = dialog.showAndWait();

        if (result != null && result.isPresent()) {
            target.configure(result.get());
            return target;
        }

        return null;
    }
}
