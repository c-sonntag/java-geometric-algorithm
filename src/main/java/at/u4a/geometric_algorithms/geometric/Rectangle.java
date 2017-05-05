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

    private List<InterfaceMapper> mappedComposition = null;

    @Override
    public List<InterfaceMapper> getMappedComposition() {
        //
        if (mappedComposition != null)
            return mappedComposition;

        //
        mappedComposition = new ArrayList<InterfaceMapper>();

        // Top left
        MappedPoint topLeftPoint = new MappedPoint((o) -> {
            o.set(this.origin);
        }, (o) -> {
            size.set(size.x - (o.x - origin.x), size.y - (o.y - origin.y));
            origin.set(o);
        });
        mappedComposition.add(topLeftPoint);

        // Top right
        MappedPoint topRightPoint = new MappedPoint((o) -> {
            o.set(this.origin.x + this.size.x, this.origin.y);
        }, (o) -> {
            size.set(o.x - origin.x, size.y - (o.y - origin.y));
            origin.set(origin.x, o.y);
        });
        mappedComposition.add(topRightPoint);

        // Bottom left
        MappedPoint bottomLeftPoint = new MappedPoint((o) -> {
            o.set(this.origin.x, this.origin.y + this.size.y);
        }, (o) -> {
            size.set(size.x - (o.x - origin.x), o.y - origin.y);
            origin.set(o.x, origin.y);
        });
        mappedComposition.add(bottomLeftPoint);

        // Bottom right
        MappedPoint bottomRightPoint = new MappedPoint((o) -> {
            o.set(this.origin.x + this.size.x, this.origin.y + this.size.y);
        }, (o) -> {
            size.set(o.x - origin.x, o.y - origin.y);
        });
        mappedComposition.add(bottomRightPoint);

        // Lines

        /** @tofo faire les lignes */
        //mappedComposition.add(new MappedLine(topLeftPoint, topRightPoint));
        //mappedComposition.add(new MappedLine(topRightPoint, bottomRightPoint));
        //mappedComposition.add(new MappedLine(bottomRightPoint, bottomLeftPoint));
        //mappedComposition.add(new MappedLine(bottomLeftPoint, topLeftPoint));

        //
        return mappedComposition;
    }

}
