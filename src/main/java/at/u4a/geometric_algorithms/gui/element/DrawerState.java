package at.u4a.geometric_algorithms.gui.element;

import javafx.scene.canvas.GraphicsContext;

public interface DrawerState {
    
    void mousePressed(DrawerContext context, double x, double y);
    void mouseReleased(DrawerContext context, double x, double y);
    void mouseMoved(DrawerContext context, double x, double y);
    void paint(GraphicsContext context);
    

}
