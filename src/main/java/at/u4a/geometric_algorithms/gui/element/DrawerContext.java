package at.u4a.geometric_algorithms.gui.element;

import at.u4a.geometric_algorithms.gui.tools.state.CircleDrawerState;
import at.u4a.geometric_algorithms.gui.tools.state.NullDrawerState;
import at.u4a.geometric_algorithms.gui.tools.state.RectangleDrawerState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class DrawerContext {

    private DrawerState currentState;
    private Drawer drawer;

    public DrawerContext(Drawer drawer) {
        this.drawer = drawer;
        setState(new NullDrawerState());
    }

    public void setState(DrawerState dc) {
        this.currentState = dc;
    }

    public void paint(GraphicsContext context) {
        currentState.paint(context);
    }

    public void mousePressed(MouseEvent event) {
        currentState.mousePressed(this, event.getX(), event.getY());
    }

    public void mouseReleased(MouseEvent event) {
        currentState.mouseReleased(this, event.getX(), event.getY());
    }

    public void mouseMoved(MouseEvent event) {
        currentState.mouseMoved(this, event.getX(), event.getY());
    }

    public void keyPressed(KeyEvent event) {
        DrawerState choose = Tool.get(event.getCode());
        if(choose!=null)
            setState(choose);
        /*switch (event.getCode()) {
        case R:
            setState(new RectangleDrawerState0());
            break;

        case C:
            setState(new CircleDrawerState0());
            break;

        case ESCAPE:
            setState(new NullDrawerState());
            break;
            
        }*/
        drawer.repaint();
    }

    public Drawer drawer() {
        return drawer;
    }

}
