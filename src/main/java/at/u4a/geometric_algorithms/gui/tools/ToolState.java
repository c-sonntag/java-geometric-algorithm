package at.u4a.geometric_algorithms.gui.tools;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.element.Drawer;
import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public abstract class ToolState {

    /* */
    
    public static final Color TOOL_PAINT_COLOR= Color.BLUE;
    
    /* STATE ACTION */

    public enum State {
        Waiting, Started, Finish
    };

    protected ToolState.State currentState = State.Waiting;

    public ToolState.State getState() {
        return currentState;
    }

    /* DrawerAction Command */

    public void init(Drawer drawer) {
        // Nothing
    }

    public void valid(Drawer drawer) {
        // Nothing
    }

    public void cancel(Drawer drawer) {
        // Nothing
    }

    public Boolean needValidOperation() {
        return false;
    }

    /* ACTION IN CANVAS */

    abstract public void mousePressed(DrawerContext context, MouseEvent event);

    abstract public void mouseReleased(DrawerContext context, MouseEvent event);

    abstract public void mouseMoved(DrawerContext context, MouseEvent event);

    /* ACTION GENERAL */

    public void mouseEntered(Drawer drawer) {
        drawer.setCursor(Cursor.NONE);
        // drawer.setCursor(Cursor.CROSSHAIR);
    }

    public void mouseExited(Drawer drawer) {
        drawer.setCursor(Cursor.DEFAULT);
    }

    /* PAINT */

    abstract public void paint(InterfaceGraphicVisitor painter);

    /* ADD ADAPTOR */

    protected Boolean isLeftClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY)
            return true;
        if (event.isPrimaryButtonDown())
            return true;
        return false;
    }

    protected Boolean isRightClick(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY)
            return true;
        if (event.isSecondaryButtonDown())
            return true;
        return false;
    }

    protected Boolean isMiddleClick(MouseEvent event) {
        if (event.getButton() == MouseButton.MIDDLE)
            return true;
        if (event.isMiddleButtonDown())
            return true;
        return false;
    }

}
