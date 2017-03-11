package at.u4a.geometric_algorithms.gui.element;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import at.u4a.geometric_algorithms.gui.tools.Tool;

public class ToolButton extends JToggleButton {
    private static final long serialVersionUID = -7250482020445164281L;
    private final Tool tool;

    public ToolButton(Tool tool) {
        this.tool = tool;

        String shortcut = tool.getKeyCodeStr();
        setToolTipText(tool.tip + (shortcut == null ? "" : (" (" + shortcut + ")")));
        setIcon(new ImageIcon(Tool.iconRessource + tool.icon));
        setFocusPainted(false);
        
        if (tool.supplier == null)
            setEnabled(false);
    }

    public Tool getTool() {
        return tool;
    }

    public void setActive(boolean b) {
        setSelected(b);
    }

}