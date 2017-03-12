package at.u4a.geometric_algorithms.gui.tools.state;

import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.element.Drawer;
import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.scene.Cursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

public class SimplePolygonToolState extends ToolState {

    private ToolState.State currentState = State.Waiting;
    private final Polygon poly = new Polygon();

    private final Point currentPointToPlace = new Point();

    // private double x, y;

    public void mouseEntered(Drawer drawer) {
        drawer.setCursor(Cursor.DISAPPEAR);
    }

    @Override
    public void mousePressed(DrawerContext context, MouseEvent event) {

    }

    @Override
    public void mouseReleased(DrawerContext context, MouseEvent event) {
        if (!event.isPrimaryButtonDown())
            return;
        //
        if (currentState == State.Waiting) {
            currentState = State.Started;
            poly.origin.set(event.getX(), event.getY());
        }
        poly.addPoint(event.getX(), event.getY());
    }

    @Override
    public void mouseMoved(DrawerContext context, MouseEvent event) {
        currentPointToPlace.set(event.getX(), event.getY());
        context.repaint();
    }

    @Override
    public void paint(InterfaceGraphicVisitor painter) {
        painter.visit(poly);
        painter.visit(currentPointToPlace);

        /*
         * context.setStroke(Color.BLACK); context.strokeLine(x - 5, y, x + 5,
         * y); context.strokeLine(x, y - 5, x, y + 5);
         */
    }

}
