package at.u4a.geometric_algorithms.gui.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JButton;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainter;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.LayerMannager;
import at.u4a.geometric_algorithms.gui.tools.Tool;
import javafx.embed.swing.JFXPanel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Drawer extends Canvas {

    private final DrawerScene ds;
    private final DrawerContext context;
    
    // public Drawer(double width, double height) {
    public Drawer(DrawerScene ds) {
        // super(width, height);
        super();
        this.ds = ds;
        this.context= new DrawerContext(this);
        //
        setFocusTraversable(true);
        /*
         * setOnMouseMoved(new EventHandle<MouseEvent>() { public void
         * handle(MouseEvent event) { context.mouseMoved(event); } });
         */
        setOnMouseEntered(context::mouseEntered);
        setOnMouseExited(context::mouseExited);

        setOnMouseMoved(context::mouseMoved);
        setOnMouseDragged(context::mouseMoved);
        setOnMousePressed(context::mousePressed);
        setOnMouseReleased(context::mouseReleased);
        setOnKeyPressed(context::keyPressed);

        //
        repaint();
    }

    /*
     * public void add(Shape shape) { shapes.add(shape); }
     */

    public void repaint() {
        if (context == null)
            return;
        //
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        for (AbstractLayer layer : ds.getLayers())
            layer.getShapePainter().paint(gc);
        this.context.paint(gc);
    }

    public AbstractLayer shapeContainning(double x, double y) {
        return null;
    }

    public DrawerScene getDS() {
        return ds;
    }

}