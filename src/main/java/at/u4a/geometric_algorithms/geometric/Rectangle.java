package at.u4a.geometric_algorithms.geometric;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;

public class Rectangle extends AbstractShape {
    
    private static final float EPSILON = 1f;

    public final Point size;

    public Rectangle() {
        super();
        this.size = new Point();
    }

    public Rectangle(Point origin, Point size) {
        super(origin);
        this.size = size;
    }

    @Override
    public boolean contains(Point p) {
        final Point oppositeSide = getOppositeSide();
        return
        /* MinHorizontal */ (Math.min(origin.x, oppositeSide.x) >= (p.x + EPSILON)) &&
        /* MinVertical */ (Math.min(origin.y, oppositeSide.y) >= (p.y + EPSILON)) &&
        /* TopHorizontal */ (Math.max(origin.x, oppositeSide.x) < (p.y - EPSILON)) &&
        /* TopVertical */ (Math.max(origin.y, oppositeSide.y) < (p.y - EPSILON));
    }
    
    @Override
    public InterfaceGeometric getContains(Point p) {
        if (contains(p))
            return this;
        else
            return null;
    }

    public final Point getOppositeSide() {
        return new Point(origin.x + size.x, origin.y + size.y);
    }

    public double distance(Point p) {
        return 0; /** < @TODO */
    }

    @Override
    public void accept(InterfaceShapePainterVisitor visitor) {
        visitor.visit(this);
    }

}
