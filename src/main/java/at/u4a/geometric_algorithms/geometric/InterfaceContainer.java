package at.u4a.geometric_algorithms.geometric;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGeometricPainterVisitor;

public interface InterfaceContainer<TIter> extends Iterable<TIter> {
    
    void clear();

}
