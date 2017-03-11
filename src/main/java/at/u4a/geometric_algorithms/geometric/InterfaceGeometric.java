package at.u4a.geometric_algorithms.geometric;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGeometricPainterVisitor;

public interface InterfaceGeometric {
    
    boolean contains(Point p);
    
    double distance(Point p);
    
    void accept(InterfaceGeometricPainterVisitor visitor);

}
