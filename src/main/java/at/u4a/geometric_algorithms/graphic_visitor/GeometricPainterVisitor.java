package at.u4a.geometric_algorithms.graphic_visitor;

import at.u4a.geometric_algorithms.geometric.*;

public interface GeometricPainterVisitor {

    public void visit(Point p, boolean selected);

    public void visit(Line l, boolean selected);
}