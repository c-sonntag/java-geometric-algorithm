package at.u4a.geometric_algorithms.gui.tools.state;


import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SelectionToolState extends ToolState {

    //private double x, y;

    @Override
    public void mousePressed(DrawerContext context, double x, double y) {
        //context.setState(new CircleDrawerState1(x, y));
    }

    @Override
    public void mouseReleased(DrawerContext context, double x, double y) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved(DrawerContext context, double x, double y) {
        /*this.x = x;
        this.y = y;
        context.drawer().repaint();*/
        //

    }

    @Override
    public void paint(GraphicsContext context) {
        /*context.setStroke(Color.BLACK);
        context.strokeLine(x - 5, y, x + 5, y);
        context.strokeLine(x, y - 5, x, y + 5);*/
    }

}
