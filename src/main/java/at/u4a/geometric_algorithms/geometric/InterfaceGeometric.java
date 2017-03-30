package at.u4a.geometric_algorithms.geometric;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGeometricPainterVisitor;

public interface InterfaceGeometric {
    
    boolean contains(Point p);
    
    InterfaceGeometric getContains(Point p);
    
    //boolean contains(Point p, float epsilon);
    
    double distance(Point p);
    
    void translate(Point p);
    
    void accept(InterfaceGeometricPainterVisitor visitor);

}
