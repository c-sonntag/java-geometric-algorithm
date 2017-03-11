package at.u4a.geometric_algorithms.graphic_visitor;

import at.u4a.geometric_algorithms.geometric.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public interface InterfaceGraphicVisitor extends InterfaceShapePainterVisitor, InterfaceGeometricPainterVisitor  {

    public void setOverlay(boolean overlay) ;
    public void setSelected(boolean selected) ;
    public void setColor(Color color) ;
    public void setGraphicsContext(GraphicsContext gc);
    
    public GraphicsContext getGraphicsContext();
    
}