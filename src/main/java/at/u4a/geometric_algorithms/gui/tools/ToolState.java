package at.u4a.geometric_algorithms.gui.tools;

import at.u4a.geometric_algorithms.gui.element.Drawer;
import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import javafx.scene.Cursor;
import javafx.scene.canvas.GraphicsContext;

public abstract class ToolState {
    
    public enum State { Waiting, Started, Finish };
    
    abstract public void mousePressed(DrawerContext context, double x, double y);
    abstract public void mouseReleased(DrawerContext context, double x, double y);
    abstract public void mouseMoved(DrawerContext context, double x, double y);
    abstract public void paint(InterfaceShapePainterVisitor context);
    
    public void mouseEntered(Drawer drawer) {
        drawer.setCursor(Cursor.CROSSHAIR);
    }
    
    public void mouseExited(Drawer drawer) {
        drawer.setCursor(Cursor.DEFAULT);
    }
    

}
