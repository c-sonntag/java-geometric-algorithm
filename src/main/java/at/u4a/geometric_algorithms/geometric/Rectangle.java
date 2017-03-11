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
        return (p.x >= origin.y) && (p.y >= origin.y) && (p.x < (size.x - origin.x)) && (p.x < (size.x - origin.x));
    }

    public double distance(Point p) {
        return 0; /** < @TODO */
    }

    @Override
    public void accept(InterfaceShapePainterVisitor visitor) {
        visitor.visit(this);
    }

}
