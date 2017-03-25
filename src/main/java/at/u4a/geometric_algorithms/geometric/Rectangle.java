package at.u4a.geometric_algorithms.geometric;

import java.util.ArrayList;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;

public class Rectangle extends AbstractShape {

    public final Point size;

    public Rectangle() {
        super();
        this.size = new Point();
    }

    public Rectangle(Point origin, Point size) {
        super(origin);
        this.size = size;
    }

    public boolean contains(Point p) {
        return contains(p, 0);
    }

    @Override
    public boolean contains(Point p, float epsilon) {
        final Point oppositeSide = getOppositeSide();
        return
        /* MinHorizontal */ (Math.min(origin.x, oppositeSide.x) >= (p.x + epsilon)) &&
        /* MinVertical */ (Math.min(origin.y, oppositeSide.y) >= (p.y + epsilon)) &&
        /* TopHorizontal */ (Math.max(origin.x, oppositeSide.x) < (p.y - epsilon)) &&
        /* TopVertical */ (Math.max(origin.y, oppositeSide.y) < (p.y - epsilon));
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
