package at.u4a.geometric_algorithms.gui.element;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;

import at.u4a.geometric_algorithms.gui.tools.Tool;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import at.u4a.geometric_algorithms.gui.tools.state.CircleDrawerState;
import at.u4a.geometric_algorithms.gui.tools.state.NullToolState;
import at.u4a.geometric_algorithms.gui.tools.state.RectangleDrawerState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class DrawerContext {

    private class ToolControl {
        public final JButton btn;

        ToolControl(JButton btn) {
            this.btn = btn;
        }
    }

    private Map<Tool, ToolControl> toolControl = new HashMap<Tool, ToolControl>();

    private ToolState currentState;
    
    private Drawer drawer;

    public DrawerContext(Drawer drawer) {
        this.drawer = drawer;
        setState(new NullToolState());
    }

    public void setState(ToolState dc) {
        for(Entry<Tool, ToolControl> toolControlEntry : toolControl.entrySet()) {
            
        }
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
        ToolState choose = Tool.get(event.getCode());
        if (choose != null)
            setState(choose);
        /*
         * switch (event.getCode()) { case R: setState(new
         * RectangleDrawerState0()); break;
         * 
         * case C: setState(new CircleDrawerState0()); break;
         * 
         * case ESCAPE: setState(new NullDrawerState()); break;
         * 
         * }
         */
        drawer.repaint();
    }

    public Drawer drawer() {
        return drawer;
    }

    public void addToolControl(Tool tool, JButton btnTool) {
        toolControl.put(tool, new ToolControl(btnTool));

    }

}
