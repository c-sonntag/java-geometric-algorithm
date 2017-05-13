package at.u4a.geometric_algorithms.graphic_visitor;

import java.util.AbstractList;

import at.u4a.geometric_algorithms.geometric.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public interface InterfaceGraphicVisitor extends InterfaceShapePainterVisitor, InterfaceGeometricPainterVisitor  {

    public void setOverlay(boolean overlay) ;
    public void setSelected(boolean selected) ;
    public void setColor(Color color) ;
    
    public void setWorkedConstruct(boolean b);
    public void setDotted(boolean b);
    public boolean getDotted();
    
    //public void drawTip(String t,Point p);
    public void drawEdgeTipFromList(AbstractShape as, AbstractList<Point> lp);
    
    public GraphicsContext getGraphicsContext();
    //public void setGraphicsContext(GraphicsContext gc);
    
    
}