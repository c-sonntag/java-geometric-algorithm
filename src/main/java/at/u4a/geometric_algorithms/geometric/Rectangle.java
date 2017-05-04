package at.u4a.geometric_algorithms.geometric;

import java.util.ArrayList;
import java.util.List;

import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import at.u4a.geometric_algorithms.geometric.mapper.MappedLine;
import at.u4a.geometric_algorithms.geometric.mapper.MappedPoint;
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
        /* MinHorizontal */ (Math.min(origin.x, oppositeSide.x) <= (p.x + EPSILON)) &&
        /* MinVertical */ (Math.min(origin.y, oppositeSide.y) <= (p.y + EPSILON)) &&
        /* TopHorizontal */ (Math.max(origin.x, oppositeSide.x) > (p.x - EPSILON)) &&
        /* TopVertical */ (Math.max(origin.y, oppositeSide.y) > (p.y - EPSILON));
    }

    @Override
    public InterfaceGeometric getContains(Point p) {
        return contains(p) ? this : null;
    }

    public final Point getOppositeSide() {
        return new Point(origin.x + size.x, origin.y + size.y);
    }

    /** @see https://gamedev.stackexchange.com/questions/44483/how-do-i-calculate-distance-between-a-point-and-an-axis-aligned-rectangle */
    public double distance(Point p) {
        double cx = Math.max(Math.min(p.x, origin.x + size.x), origin.x);
        double cy = Math.max(Math.min(p.y, origin.y + size.y), origin.y);
        return Math.sqrt((p.x - cx) * (p.x - cx) + (p.y - cy) * (p.y - cy));
    }

    @Override
    public void accept(InterfaceShapePainterVisitor visitor) {
        visitor.visit(this);
    }

    /* */

    @Override
    public List<InterfaceMapper> getMappedComposition() {
        List<InterfaceMapper> l = new ArrayList<InterfaceMapper>();

        // Top left
        MappedPoint topLeftPoint = new MappedPoint((o) -> {
            o.set(this.origin);
        }, (o) -> {
            this.origin.set(o);
        });
        l.add(topLeftPoint);

        // Top right
        MappedPoint topRightPoint =new MappedPoint((o) -> {
            o.set(this.origin.x + this.size.x, this.origin.y);
        }, (o) -> {
            this.origin.set(o.x - this.origin.x, o.y);
        });
        l.add(topRightPoint);

        // Bottom left
        MappedPoint bottomLeftPoint =new MappedPoint((o) -> {
            o.set(this.origin.x, this.origin.y + this.size.y);
        }, (o) -> {
            this.origin.set(o.x, o.y - this.origin.y);
        });
        l.add(bottomLeftPoint);

        // Bottom right
        MappedPoint bottomRightPoint =new MappedPoint((o) -> {
            o.set(this.origin.x + this.size.x, this.origin.y + this.size.y);
        }, (o) -> {
            this.origin.set(o.x - this.origin.x, o.y - this.origin.y);
        });
        l.add(bottomRightPoint);

        // Lines
        l.add(new MappedLine(topLeftPoint,topRightPoint));
        l.add(new MappedLine(topRightPoint,bottomRightPoint));
        l.add(new MappedLine(bottomRightPoint,bottomLeftPoint));
        l.add(new MappedLine(bottomLeftPoint,topLeftPoint));

        //
        return l;
    }

}
