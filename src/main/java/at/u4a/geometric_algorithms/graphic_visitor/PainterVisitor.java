package at.u4a.geometric_algorithms.graphic_visitor;

import at.u4a.geometric_algorithms.geometric.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class PainterVisitor implements InterfaceGraphicVisitor {

    protected boolean isSelected = false;
    protected boolean isOverlay = false;
    
    protected Color color = null; /* strockeColor, fillColor, selected Color ... */
    
    protected GraphicsContext gc = null;

    public void setOverlay(boolean overlay) {
        isOverlay = overlay;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }

    public void setGraphicsContext(GraphicsContext gc) {
        this.gc = gc;
    }
}