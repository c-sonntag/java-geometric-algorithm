package at.u4a.geometric_algorithms.graphic_visitor;

import at.u4a.geometric_algorithms.geometric.*;


public interface InterfaceShapePainterVisitor {
    
    public void visit(Polygon poly);
    
    public void visit(Rectangle poly);

    public void visit(CloudOfSegments clouds);

}
