package at.u4a.geometric_algorithms.gui.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JButton;

import at.u4a.geometric_algorithms.graphic_adaptor.InterfaceShapePainter;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.LayerMannager;
import at.u4a.geometric_algorithms.gui.tools.Tool;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Drawer extends Canvas {

    private final LayerMannager layers = new LayerMannager();

    private final DrawerContext context = new DrawerContext(this);

    // public Drawer(double width, double height) {
    public Drawer() {
        // super(width, height);
        super();
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

    }

    /*
     * public void add(Shape shape) { shapes.add(shape); }
     */
    
    public void refresh() {
        context.refresh();
    }

    public void repaint() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        for (AbstractLayer layer : layers)
            layer.getShapePainter().paint(gc);
       // this.context.paint(gc);
    }

    public AbstractLayer shapeContainning(double x, double y) {
        return null;
    }
    
    public void addToolControl(Tool tool, JButton toolControl) {
        
    }

    public void addToolButton(ToolButton btnTool) {
        // TODO Auto-generated method stub
        context.addToolButton(btnTool);
    }

}