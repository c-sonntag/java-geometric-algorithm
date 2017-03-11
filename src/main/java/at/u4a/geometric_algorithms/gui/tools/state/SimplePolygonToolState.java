package at.u4a.geometric_algorithms.gui.tools.state;

import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;

import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.scene.canvas.GraphicsContext;

public class SimplePolygonToolState extends ToolState {

    private ToolState.State currentState = State.Waiting;
    private final Polygon poly = new Polygon();

    private final Point currentPointToPlace = new Point();

    // private double x, y;

    @Override
    public void mousePressed(DrawerContext context, double x, double y) {
        if (currentState == State.Waiting) {
            currentState = State.Started;
            poly.origin.set(x, y);
        }
    }

    @Override
    public void mouseReleased(DrawerContext context, double x, double y) {
        poly.addPoint(x, y);
    }

    @Override
    public void mouseMoved(DrawerContext context, double x, double y) {
        currentPointToPlace.set(x, y);
        context.repaint();
    }

    @Override
    public void paint(GraphicsContext context) {
        
        
        /*
         * context.setStroke(Color.BLACK); context.strokeLine(x - 5, y, x + 5,
         * y); context.strokeLine(x, y - 5, x, y + 5);
         */
    }

}
