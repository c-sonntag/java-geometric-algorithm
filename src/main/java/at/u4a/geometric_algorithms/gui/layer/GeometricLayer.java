package at.u4a.geometric_algorithms.gui.layer;

import java.util.Vector;

import javax.swing.ImageIcon;

import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;

public class GeometricLayer<TShape extends AbstractShape> extends AbstractLayer {

    private TShape shape;

    public GeometricLayer(TShape shape) {
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
        return shape.getClass().getSimpleName();
        //return "An Geometric shape ..."; 
        /** @TODO Gerer le type */
    }

    @Override
    public ImageIcon getLayerTypeIcon() {
        return new ImageIcon("R:\\Java_Shared\\java-licence-3-informatique\\GeometricAlgorithms\\icons\\tools\\shape_circle.png");
        //return "test.png";               
        /** @TODO Gerer l'icone */
    }

    @Override
    public void accept(InterfaceShapePainterVisitor visitor) {
        shape.accept(visitor);
    }

    @Override
    public boolean contains(Point p) {
        return shape.contains(p);
    }

}
