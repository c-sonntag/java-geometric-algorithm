package at.u4a.geometric_algorithms.gui.tools.state;

import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.geometric.Rectangle;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.element.Drawer;
import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import at.u4a.geometric_algorithms.gui.layer.GeometricLayer;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.scene.input.MouseEvent;

public class RectangleDrawerState extends ToolState {

    /* CONST STATIC */

    private static final Point MIN_SIZE = new Point(5, 5);

    /* */

    private Rectangle rectangle = new Rectangle();
    private Point currentPointToPlace;

    /* */

    public void mouseExited(Drawer drawer) {
        currentPointToPlace = null;
        super.mouseExited(drawer);
    }

    /* */

    static int RectangleCount = 1;

    public void valid(Drawer drawer) {

        //
        if (((Math.abs(rectangle.size.x) < MIN_SIZE.x) && (Math.abs(rectangle.size.y) < MIN_SIZE.y)))
            init(drawer);

        //
        GeometricLayer<Rectangle> rectangleLayer = new GeometricLayer<Rectangle>(rectangle);
        rectangleLayer.setLayerName("r" + String.valueOf(RectangleCount));
        RectangleCount++;

        drawer.getDS().getLayerMannager().addLayer(rectangleLayer);
        currentState = State.Finish;

        //
        rectangle = new Rectangle();
        init(drawer);
    }

    public void init(Drawer drawer) {
        rectangle.origin.set(0, 0);
        rectangle.size.set(0, 0);
        currentState = State.Waiting;
    }

    public void cancel(Drawer drawer) {
        init(drawer);
    }

    /* */

    @Override
    public void mousePressed(DrawerContext context, MouseEvent event) {
        if (!isLeftClick(event)) {
            cancel(null);
            return;
        }

        //
        if (currentState == State.Waiting) {
            currentState = State.Started;
            rectangle.origin.set(event.getX(), event.getY());
        }
    }

    @Override
    public void mouseReleased(DrawerContext context, MouseEvent event) {
        if (!isLeftClick(event) || (currentState != State.Started)) {
            cancel(null);
            return;
        }

        //
        setSize(event.getX(), event.getY());

        //
        valid(context.getDrawer());
    }

    @Override
    public void mouseMoved(DrawerContext context, MouseEvent event) {
        if (currentState != State.Started) {
            if (currentPointToPlace == null)
                currentPointToPlace = new Point();
            currentPointToPlace.set(event.getX(), event.getY());  
        }
        else {
            setSize(event.getX(), event.getY());
        }
        context.repaint();
    }

    private void setSize(double x, double y) {
        rectangle.size.set(x - rectangle.origin.x, y - rectangle.origin.y);
    }

    @Override
    public void paint(InterfaceGraphicVisitor painter) {
        
        if (currentState != State.Waiting)
            painter.visit(rectangle);
        else if (currentPointToPlace != null)
            painter.visit(currentPointToPlace);
    }

}
