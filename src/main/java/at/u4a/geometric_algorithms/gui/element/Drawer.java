package at.u4a.geometric_algorithms.gui.element;

import at.u4a.geometric_algorithms.graphic_visitor.GraphicPainter;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * 
 * @author Christophe Sonntag
 * @see Editeur de forme
 *      http://pageperso.lif.univ-mrs.fr/~bertrand.estellon/javaL3/td7.pdf
 * @see ResizableCanvas
 *      http://dlsc.com/2014/04/10/javafx-tip-1-resizable-canvas/
 *
 */

public class Drawer extends Canvas {

    private final DrawerScene ds;
    private final DrawerContext context;

    private final GraphicPainter gp = new GraphicPainter(getGraphicsContext2D());

    public Drawer(DrawerScene ds) {
        this.ds = ds;
        this.context = new DrawerContext(this);
        //
        setFocusTraversable(true);

        setOnMouseEntered(context::mouseEntered);
        setOnMouseExited(context::mouseExited);

        setOnMouseMoved(context::mouseMoved);
        setOnMouseDragged(context::mouseMoved);
        setOnMousePressed(context::mousePressed);
        setOnMouseReleased(context::mouseReleased);
        setOnKeyPressed(context::keyPressed);

        // Redraw canvas when size changes.
        widthProperty().addListener(evt -> repaint());
        heightProperty().addListener(evt -> repaint());

        // Add DrawerAction
        ds.getDrawerAction().addDrawerActionListenerOfValid(context::valid);
        ds.getDrawerAction().addDrawerActionListenerOfCancel(context::cancel);

        ds.getLayerManager().clear();

        //
        repaint();
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    /*
     * public void add(Shape shape) { shapes.add(shape); }
     */

    public void repaint() {
        if (context == null)
            return;
        //
        double width = getWidth();
        double height = getHeight();

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        //
        for (AbstractLayer layer : ds.getLayerManager()) {
            layer.accept(gp);
        }

        this.context.paint(gp);
    }

    public AbstractLayer shapeContainning(double x, double y) {
        return null;
    }

    public DrawerScene getDS() {
        return ds;
    }

    public InterfaceDrawerAction getDA() {
        return ds.getDrawerAction();
    }

}