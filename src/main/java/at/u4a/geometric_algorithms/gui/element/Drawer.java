package at.u4a.geometric_algorithms.gui.element;

import javax.swing.JOptionPane;

import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Rectangle;
import at.u4a.geometric_algorithms.graphic_visitor.GraphicPainter;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.Geometric;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * 
 * @author Christophe Sonntag
 * @see ResizableCanvas (
 *      http://dlsc.com/2014/04/10/javafx-tip-1-resizable-canvas/ )
 *
 */

public class Drawer extends Canvas {

    private final DrawerScene ds;
    private final DrawerContext context;

    private final GraphicPainter gp = new GraphicPainter();

    // public Drawer(double width, double height) {
    public Drawer(DrawerScene ds) {
        // super(width, height);
        //super(400, 400);
        this.ds = ds;
        this.context = new DrawerContext(this);
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

        // Redraw canvas when size changes.
        widthProperty().addListener(evt -> repaint());
        heightProperty().addListener(evt -> repaint());

        ds.getLayerMannager().addLayer(new Geometric<Rectangle>(new Rectangle(new Point(10, 10), new Point(100, 100))));

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

        /** @TODO dessiner avec l'adaptor */
        gp.setGraphicsContext(gc);

        //
        for (AbstractLayer layer : ds.getLayerMannager()) {
            layer.accept(gp);

            /*
             * layer. AbstractShape shape = layer.getShape();
             * 
             * if( shape.contains(p) ) .accept(sp);
             */
        }

        this.context.paint(gp);
    }

    public AbstractLayer shapeContainning(double x, double y) {
        return null;
    }

    public DrawerScene getDS() {
        return ds;
    }

}