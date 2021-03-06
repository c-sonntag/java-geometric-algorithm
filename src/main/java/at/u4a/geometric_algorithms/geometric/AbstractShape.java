package at.u4a.geometric_algorithms.geometric;

import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGeometricPainterVisitor;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;
import at.u4a.geometric_algorithms.utils.Mutable;

public abstract class AbstractShape implements InterfaceGeometric {

    final public Point origin;

    public AbstractShape(Point origin) {
        this.origin = origin;
    }

    public AbstractShape() {
        origin = new Point(0, 0);
    }

    public void translate(Point newOrigin) {
        origin.translate(newOrigin);
    }
    
    public void convertToOrigin(Point p) {
        p.x = p.x - origin.x;
        p.y = p.y - origin.y;
    }
    
    public void convertToStandard(Point p) {
        p.x = p.x + origin.x;
        p.y = p.y + origin.y;
    }
    
    //public boolean contains(Point p) {
    //    return contains(p, 0);
    //}
    
    
    @Override
    public void accept(InterfaceGeometricPainterVisitor visitor) {
        // Nothing here !
    }
    
    /* VISITOR */
    
    public abstract void accept(InterfaceShapePainterVisitor visitor);
    
    /* MAPPED */
    
    //public abstract List<InterfaceMapper> getMappedComposition();
    
    public abstract InterfaceMapper getContainMappedComposition(Point p);

}