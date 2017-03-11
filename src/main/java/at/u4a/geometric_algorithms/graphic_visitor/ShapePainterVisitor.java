package at.u4a.geometric_algorithms.graphic_visitor;

import at.u4a.geometric_algorithms.geometric.*;

public interface ShapePainterVisitor {

    public void visit(Polygon poly, boolean selected);

    public void visit(CloudOfSegments clouds, boolean selected);

}
