package at.u4a.geometric_algorithms.gui.tools.state;


import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Rectangle;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import at.u4a.geometric_algorithms.gui.tools.ToolState.State;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RectangleDrawerState extends ToolState {

    private ToolState.State currentState = State.Waiting;
    private final Rectangle rectangle = new Rectangle();

    @Override
    public void mousePressed(DrawerContext context, double x, double y) {
        if (currentState == State.Waiting) {
            currentState = State.Started;
            rectangle.origin.set(x, y);
        }
    }

    @Override
    public void mouseReleased(DrawerContext context, double x, double y) {
        rectangle.size.set(x, y);
    }

    @Override
    public void mouseMoved(DrawerContext context, double x, double y) {
        rectangle.size.set(x, y);
        context.repaint();
    }

    @Override
    public void paint(InterfaceGraphicVisitor painter) {
        painter.visit(rectangle);
        //painter.visit(currentPointToPlace);

        /*
         * context.setStroke(Color.BLACK); context.strokeLine(x - 5, y, x + 5,
         * y); context.strokeLine(x, y - 5, x, y + 5);
         */
    }
    
    
}
