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
        final Point oppositeSide = getOppositeSide();
        return
        /* MinHorizontal */ (Math.min(origin.x, oppositeSide.x) >= p.x) &&
        /* MinVertical */ (Math.min(origin.y, oppositeSide.y) >= p.y) &&
        /* TopHorizontal */ (Math.max(origin.x, oppositeSide.x) < p.y) &&
        /* TopVertical */ (Math.max(origin.y, oppositeSide.y) < p.y);
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
