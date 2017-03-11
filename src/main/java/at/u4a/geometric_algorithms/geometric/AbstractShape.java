package at.u4a.geometric_algorithms.geometric;

import at.u4a.geometric_algorithms.graphic_visitor.GeometricPainterVisitor;
import at.u4a.geometric_algorithms.graphic_visitor.ShapePainterVisitor;

public abstract class AbstractShape implements InterfaceGeometric {

    final public Point origin;

    public AbstractShape(Point origin) {
        this.origin = origin;
    }

    public AbstractShape() {
        origin = new Point(0, 0);
    }

    public void translate(Point newOrigin) {
        origin.set(newOrigin);
    }
    
    public abstract void accept(ShapePainterVisitor visitor, boolean selected);

}