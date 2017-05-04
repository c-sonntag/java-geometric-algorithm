package at.u4a.geometric_algorithms.geometric.mapper;

import java.util.function.Consumer;

import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.InterfaceGeometric;
import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGeometricPainterVisitor;

public class MappedPoint extends Point implements InterfaceMapper {

    private final Consumer<Point> pointActualiser;
    private final Consumer<Point> pointModifier;

    public MappedPoint(Consumer<Point> pointActualiser, Consumer<Point> pointModifier) {
        this.pointActualiser = pointActualiser;
        this.pointModifier = pointModifier;
        pointActualiser.accept(this);
    }
    
    /* */
    
    public void actualiser() {
        pointActualiser.accept(this);
    }
    
    public void modifier() {
        pointModifier.accept(this);
    }

    /* */

    public void set(Point other) {
        super.set(other);
        //pointModifier.accept(this);
    }

    public void set(double x, double y) {
        super.set(x, y);
        //pointModifier.accept(this);
    }
    
    @Override
    public void translate(Point p) {
        pointActualiser.accept(this);
        super.translate(p);
        pointModifier.accept(this);
    }

    /* */
    
    public boolean contains(Point p) {
        pointActualiser.accept(this);
        return super.contains(p);
    }

    @Override
    public InterfaceGeometric getContains(Point p) {
        pointActualiser.accept(this);
        return super.getContains(p);
    }

    @Override
    public double distance(Point p) {
        pointActualiser.accept(this);
        return super.distance(p);
    }

    @Override
    public void accept(InterfaceGeometricPainterVisitor visitor) {
        pointActualiser.accept(this);
        super.accept(visitor);
    }

}
