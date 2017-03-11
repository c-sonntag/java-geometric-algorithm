package at.u4a.geometric_algorithms.gui.tools;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;
import at.u4a.geometric_algorithms.gui.element.Drawer;
import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import javafx.scene.Cursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

public abstract class ToolState {
    
    public enum State { Waiting, Started, Finish };
    
    abstract public void mousePressed(DrawerContext context, MouseEvent event);
    abstract public void mouseReleased(DrawerContext context, MouseEvent event);
    abstract public void mouseMoved(DrawerContext context, MouseEvent event);
    abstract public void paint(InterfaceGraphicVisitor painter);
    
    public void mouseEntered(Drawer drawer) {
        drawer.setCursor(Cursor.CROSSHAIR);
    }
    
    public void mouseExited(Drawer drawer) {
        drawer.setCursor(Cursor.DEFAULT);
    }


}
