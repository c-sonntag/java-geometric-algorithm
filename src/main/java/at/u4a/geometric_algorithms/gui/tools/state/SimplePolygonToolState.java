package at.u4a.geometric_algorithms.gui.tools.state;

import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.element.Drawer;
import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

public class SimplePolygonToolState extends ToolState {

    private Boolean inPlace = false;
    private final Polygon poly = new Polygon();

    private Point currentPlacedPoint = new Point();
    private Point currentPointToPlace = null;

    // private double x, y;

    /* */

    public void valid(Drawer drawer) {
        // Nothing
    }

    public void cancel(Drawer drawer) {
        // Nothing
    }

    public Boolean needValidOperation() {
        return true;
    }

    /* */

    public void mouseEntered(Drawer drawer) {
        drawer.setCursor(Cursor.NONE);
    }

    /* */

    private void addPlace(DrawerContext context, double x, double y) {
        currentPlacedPoint = new Point(x, y);
        poly.convertToOrigin(currentPlacedPoint);
        // currentPlacedPoint = poly.getByOrigin(x, y);
        poly.addPoint(currentPlacedPoint); // event.getX(), event.getY());
        inPlace = true;
        context.repaint();
    }

    @Override
    public void mousePressed(DrawerContext context, MouseEvent event) {
        if (!isLeftClick(event))
            return;
        if (currentState != State.Started)
            return;
        //
        addPlace(context, event.getX(), event.getY());
    }

    @Override
    public void mouseReleased(DrawerContext context, MouseEvent event) {
        if (!isLeftClick(event))
            return;
        //
        if (currentState == State.Waiting) {
            currentState = State.Started;
            poly.origin.set(event.getX(), event.getY());
            addPlace(context, event.getX(), event.getY());
        }
        inPlace = false;
    }

    @Override
    public void mouseMoved(DrawerContext context, MouseEvent event) {
        if (inPlace) {
            currentPlacedPoint.set(event.getX(), event.getY());
            poly.convertToOrigin(currentPlacedPoint);
        }
        else {
            if (currentPointToPlace == null)
                currentPointToPlace = new Point();
            currentPointToPlace.set(event.getX(), event.getY());
        }
        context.repaint();
    }

    @Override
    public void paint(InterfaceGraphicVisitor painter) {
        painter.visit(poly);
        if (!inPlace)
            if (currentPointToPlace != null)
                painter.visit(currentPointToPlace);

        /*
         * context.setStroke(Color.BLACK); context.strokeLine(x - 5, y, x + 5,
         * y); context.strokeLine(x, y - 5, x, y + 5);
         */
    }

}
