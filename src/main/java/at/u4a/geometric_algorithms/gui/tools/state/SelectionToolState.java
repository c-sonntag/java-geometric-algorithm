package at.u4a.geometric_algorithms.gui.tools.state;


import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class SelectionToolState extends ToolState {

    private double x, y;

    @Override
    public void mousePressed(DrawerContext context, MouseEvent event) {
        //context.setState(new CircleDrawerState1(x, y));
    }

    @Override
    public void mouseReleased(DrawerContext context, MouseEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved(DrawerContext context, MouseEvent event) {
        this.x = event.getX();
        this.y = event.getY();
        context.repaint();
    }

    @Override
    public void paint(InterfaceGraphicVisitor painter) {
        GraphicsContext context = painter.getGraphicsContext();
        context.setStroke(Color.BLACK);
        context.strokeLine(x - 5, y, x + 5, y);
        context.strokeLine(x, y - 5, x, y + 5);
        context.strokeRect(x, y, 200, 200);
        context.strokeLine(x, y - 5, x, y + 5);
    }

}
