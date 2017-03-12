package at.u4a.geometric_algorithms.geometric;

import java.util.ArrayList;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;

public class Rectangle extends AbstractShape {

    public final Point oppositeSide;

    public Rectangle() {
        super();
        this.oppositeSide = new Point();
    }

    public Rectangle(Point origin, Point oppositeSide) {
        super(origin);
        this.oppositeSide = oppositeSide;
    }

    public boolean contains(Point p) {
        return
        /* MinHorizontal */ (Math.min(origin.x, oppositeSide.x) >= p.x) &&
        /* minVertical */ (Math.min(origin.y, oppositeSide.y) >= p.y) &&
        /* TopHorizontal */ (Math.max(origin.x, oppositeSide.x) < p.y) &&
        /* TopVertical */ (Math.max(origin.y, oppositeSide.y) < p.y);
    }

    public double distance(Point p) {
        return 0; /** < @TODO */
    }

    @Override
    public void accept(InterfaceShapePainterVisitor visitor) {
        visitor.visit(this);
    }

}
