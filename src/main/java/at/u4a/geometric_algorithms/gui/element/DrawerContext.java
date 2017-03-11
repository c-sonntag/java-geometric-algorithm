package at.u4a.geometric_algorithms.gui.element;

import java.util.EnumSet;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.tools.Tool;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class DrawerContext {

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
        Tool toolChoose = Tool.getByKeyCode(eventKeys);
        if (toolChoose != null)
            ds.setTool(toolChoose);
    }

    /* */

    public void paint(InterfaceGraphicVisitor visitor) {
        ds.getCurrentState().paint(visitor);
    }
    
    public void repaint() {
        drawer.repaint();
    }

    /* */

    public void mouseEntered(MouseEvent event) {
        ds.getCurrentState().mouseEntered(drawer);
    }

    public void mouseExited(MouseEvent event) {
        ds.getCurrentState().mouseExited(drawer);
    }

    /* */

    public void mousePressed(MouseEvent event) {
        ds.getCurrentState().mousePressed(this, event);
    }

    public void mouseReleased(MouseEvent event) {
        ds.getCurrentState().mouseReleased(this, event.getX(), event.getY());
    }

    public void mouseMoved(MouseEvent event) {
        ds.getCurrentState().mouseMoved(this, event.getX(), event.getY());
    }

}
