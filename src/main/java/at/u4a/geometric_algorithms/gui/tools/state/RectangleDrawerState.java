package at.u4a.geometric_algorithms.gui.tools.state;

import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Rectangle;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import at.u4a.geometric_algorithms.gui.tools.ToolState.State;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class RectangleDrawerState extends ToolState {

    private final Rectangle rectangle = new Rectangle();

    @Override
    public void mousePressed(DrawerContext context, MouseEvent event) {
        if (!isLeftClick(event))
            return;
        //
        if (currentState == State.Waiting) {
            currentState = State.Started;
            rectangle.origin.set(event.getX(), event.getY());
        }
    }

    @Override
    public void mouseReleased(DrawerContext context, MouseEvent event) {
        if (!isLeftClick(event))
            return;
        //
        currentState = State.Finish;
        setSize(event.getX(), event.getY());

    }

    @Override
    public void mouseMoved(DrawerContext context, MouseEvent event) {
        if (currentState != State.Started)
            return;
        //
        setSize(event.getX(), event.getY());
        context.repaint();
    }

    private void setSize(double x, double y) {
        rectangle.size.set(x - rectangle.origin.x, y - rectangle.origin.y);
    }

    @Override
    public void paint(InterfaceGraphicVisitor painter) {
        painter.visit(rectangle);
        // painter.visit(currentPointToPlace);

        /*
         * context.setStroke(Color.BLACK); context.strokeLine(x - 5, y, x + 5,
         * y); context.strokeLine(x, y - 5, x, y + 5);
         */
    }

}
