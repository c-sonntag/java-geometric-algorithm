package at.u4a.geometric_algorithms.gui.element;

import java.util.EnumSet;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.tools.Tool;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class DrawerContext {

    EnumSet<KeyCode> validKey = EnumSet.of(KeyCode.ENTER);
    EnumSet<KeyCode> cancelKey = EnumSet.of(KeyCode.ESCAPE);

    private final Drawer drawer;
    private final DrawerScene ds;

    DrawerContext(Drawer drawer) {
        this.drawer = drawer;
        this.ds = drawer.getDS();
    }

    public void keyPressed(KeyEvent event) {
        EnumSet<KeyCode> eventKeys = EnumSet.of(event.getCode());

        //
        if (event.isAltDown())
            eventKeys.add(KeyCode.ALT);
        if (event.isControlDown())
            eventKeys.add(KeyCode.CONTROL);
        if (event.isShiftDown())
            eventKeys.add(KeyCode.SHIFT);

        //
        if (eventKeys.equals(validKey))
            valid();
        else if (eventKeys.equals(cancelKey))
            cancel();
        else {
            Tool toolChoose = Tool.getByKeyCode(eventKeys);
            if (toolChoose != null)
                ds.setTool(toolChoose);
        }
    }

    /* */

    public Drawer getDrawer() {
        return drawer;
    }

    /* */

    public void paint(InterfaceGraphicVisitor visitor) {
        if (ds.getCurrentState() != null) {
            visitor.setColor(ToolState.TOOL_PAINT_COLOR);
            ds.getCurrentState().paint(visitor);
        }
    }

    public void repaint() {
        drawer.repaint();
    }

    /* */

    public void valid() {
        if (ds.getCurrentState() != null)
            ds.getCurrentState().valid(drawer);
        repaint();
    }

    public void cancel() {
        if (ds.getCurrentState() != null)
            ds.getCurrentState().cancel(drawer);
        repaint();
    }

    public Boolean needValidOperation() {
        return (ds.getCurrentState() != null) ? ds.getCurrentState().needValidOperation() : false;
    }

    /* */

    public void mouseEntered(MouseEvent event) {
        if (ds.getCurrentState() != null)
            ds.getCurrentState().mouseEntered(drawer);
    }

    public void mouseExited(MouseEvent event) {
        if (ds.getCurrentState() != null)
            ds.getCurrentState().mouseExited(drawer);
    }

    /* */

    public void mousePressed(MouseEvent event) {
        if (ds.getCurrentState() != null)
            ds.getCurrentState().mousePressed(this, event);
    }

    public void mouseReleased(MouseEvent event) {
        if (ds.getCurrentState() != null)
            ds.getCurrentState().mouseReleased(this, event);
    }

    public void mouseMoved(MouseEvent event) {
        if (ds.getCurrentState() != null)
            ds.getCurrentState().mouseMoved(this, event);
    }

}
