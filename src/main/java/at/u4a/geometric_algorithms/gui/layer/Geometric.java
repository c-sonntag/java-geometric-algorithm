package at.u4a.geometric_algorithms.gui.layer;

import java.util.Vector;

import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;

public class Geometric<TShape extends AbstractShape> extends AbstractLayer {

    private TShape shape;

    public Geometric(TShape shape) {
        this.shape = shape;
    }

    @Override
    public LayerCategory getCategory() {
        return LayerCategory.Geometry;
    }

    @Override
    public Boolean isContener() {
        return false;
    }

    @Override
    public Boolean isDeletable() {
        return true;
    }

    @Override
    public Vector<AbstractLayer> getSubLayer() {
        return null;
    }

    @Override
    public TShape getShape() {
        return shape;
    }

    @Override
    public String getLayerType() {
        return "An Geometric shape ..."; /** @TODO Gerer le type */
    }

    @Override
    public String getLayerTypeIconName() {
        return "test.png";               /** @TODO Gerer l'icone */
    }

    @Override
    public void accept(InterfaceShapePainterVisitor visitor) {
        shape.accept(visitor);
    }

}
