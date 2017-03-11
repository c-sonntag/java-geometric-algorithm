package at.u4a.geometric_algorithms.geometric;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGeometricPainterVisitor;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;

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
    
    @Override
    public void accept(InterfaceGeometricPainterVisitor visitor) {
        // Nothing here !
    }
    
    public abstract void accept(InterfaceShapePainterVisitor visitor);
    
    

}