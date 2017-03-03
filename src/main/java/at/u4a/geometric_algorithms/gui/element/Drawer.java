package at.u4a.geometric_algorithms.gui.element;

import java.util.ArrayList;
import java.util.List;

import at.u4a.geometric_algorithms.graphic_adaptor.InterfaceShapePainter;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.LayerMannager;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Drawer extends Canvas {

    private final LayerMannager layers; 

    private final DrawerContext context;

    //public Drawer(double width, double height) {
    public Drawer() {
        //super(width, height);
        super();
        layers = new LayerMannager();
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

    /*public void add(Shape shape) {
        shapes.add(shape);
    }*/

    public void repaint() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        for (AbstractLayer layer : layers)
            layer.getShapePainter().paint(gc);
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