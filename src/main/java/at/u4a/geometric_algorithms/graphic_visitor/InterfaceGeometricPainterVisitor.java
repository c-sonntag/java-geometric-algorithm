package at.u4a.geometric_algorithms.graphic_visitor;

import at.u4a.geometric_algorithms.geometric.*;


public interface InterfaceGeometricPainterVisitor {

    public void visit(Point p);

    public void visit(Line l);

    public void visit(Segment s);
}