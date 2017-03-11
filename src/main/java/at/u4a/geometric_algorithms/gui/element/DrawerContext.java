package at.u4a.geometric_algorithms.gui.element;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import at.u4a.geometric_algorithms.gui.tools.Tool;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class DrawerContext {

    private Map<Tool, ToolButton> toolsItems = new HashMap<Tool, ToolButton>();

    private Tool currentTool;
    private ToolState currentState;

    private Drawer drawer;

    public DrawerContext(Drawer drawer) {
        this.drawer = drawer;
        setTool(Tool.Selection);
    }

    /**
     * @todo gerer l'outil pour terminer son travail (valid, cancel)
     * @param tool
     */
    public void setTool(Tool tool) {

        for (Entry<Tool, ToolButton> toolControlEntry : toolsItems.entrySet()) {
            toolControlEntry.getValue().setActive(tool.equals(toolControlEntry.getKey()));
        }
        
        if (tool == currentTool)
            return;

        //
        /*
         * for (Entry<Tool, ToolControl> toolControlEntry :
         * toolsControl.entrySet()) {
         * toolControlEntry.getValue().setActive(tool.equals(toolControlEntry.
         * getKey()));
         * 
         * if (tool.equals(toolControlEntry.getKey())) {
         * toolControlEntry.getValue().data.btn.setSelected(true); } else {
         * toolControlEntry.getValue().data.btn.setSelected(false); } }
         */

        this.currentTool = tool;
        // this.currentState = dc;

        //
        drawer.repaint();
    }
    
    public void refresh() {
        setTool(currentTool);
    }

    public void paint(GraphicsContext context) {
        currentState.paint(context);
    }

    /* */

    public void mouseEntered(MouseEvent event) {
        currentState.mouseEntered(drawer);
    }

    public void mouseExited(MouseEvent event) {
        currentState.mouseExited(drawer);
    }

    /* */

    public void mousePressed(MouseEvent event) {
        currentState.mousePressed(this, event.getX(), event.getY());
    }

    public void mouseReleased(MouseEvent event) {
        currentState.mouseReleased(this, event.getX(), event.getY());
    }

    public void mouseMoved(MouseEvent event) {
        currentState.mouseMoved(this, event.getX(), event.getY());
    }

    /* */

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
            setTool(toolChoose);
    }

    public Drawer drawer() {
        return drawer;
    }

    public void addToolButton(ToolButton btnTool) {
        
        toolsItems.put(btnTool.getTool(), btnTool);
        
        btnTool.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setTool(btnTool.getTool());
            }
        });
        
        // TODO Auto-generated method stub
        
    }

}
