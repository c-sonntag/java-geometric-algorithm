package at.u4a.geometric_algorithms.gui.element;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Drawer extends Canvas {

    private final List<Shape> shapes; // * seul le pointer est fianl, la liste
                                      // peut encore changer !*/
    private final DrawerContext context;

    public Drawer(double width, double height) {
        super(width, height);
        shapes = new ArrayList<Shape>();
        context = new DrawerContext(this);
        //
        setFocusTraversable(true);
        /*
        setOnMouseMoved(new EventHandle<MouseEvent>() {
            public void handle(MouseEvent event) {
                context.mouseMoved(event);
            }
        });
        */
        setOnMouseMoved(context::mouseMoved);
        setOnMouseDragged(context::mouseMoved);
        setOnMousePressed(context::mousePressed);
        setOnMouseReleased(context::mouseReleased);
        setOnKeyPressed(context::keyPressed);
        
    }

    public void add(Shape shape) {
        shapes.add(shape);
    }

    public void repaint() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        for (Shape shape : shapes)
            shape.paint(gc);
        this.context.paint(gc);
    }

    public Shape shapeContainning(double x, double y) {
        for (int i = shapes.size(); i >= 0; i--) {
            Shape shape = shapes.get(i);
            if (shape.contains(x, y))
                return shape;
        }
        return null;
    }

}