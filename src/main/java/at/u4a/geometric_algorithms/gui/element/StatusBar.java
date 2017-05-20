package at.u4a.geometric_algorithms.gui.element;

import java.awt.Dimension;

import javax.swing.JLabel;

public class StatusBar extends JLabel {

    /* STATIC */

    private static StatusBar active = null;

    public static void setActiveMessage(String message) {
        if (active != null)
            StatusBar.active.setMessage(message);
    }

    public static void clearActive() {
        if (active != null)
            StatusBar.active.clear();
    }

    /*  */

    /** Creates a new instance of StatusBar */
    public StatusBar() {
        super();
        super.setPreferredSize(new Dimension(100, 16));
        this.setMessage("Ready");
        StatusBar.active = this;
    }

    public void setMessage(String message) {
        setText(" " + message);
    }

    public void clear() {
        setText(" ");
    }
}