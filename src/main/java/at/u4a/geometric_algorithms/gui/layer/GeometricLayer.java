package at.u4a.geometric_algorithms.gui.layer;

import java.util.Vector;

import javax.swing.ImageIcon;

import at.u4a.geometric_algorithms.algorithm.AbstractAlgorithm;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer.ColorSet;
import at.u4a.geometric_algorithms.gui.tools.Tool;

public class GeometricLayer<TShape extends AbstractShape> extends AbstractLayer {

    private final TShape shape;
    private final Tool toolType;
    
    private final ColorSet shapeColor;

    public GeometricLayer(TShape shape, Tool toolType) {
        super(getAuthorizationForGeometricLayer());
        this.shape = shape;
        this.toolType = toolType;
        
        //
        this.shapeColor = new ColorSet(getLayerType() + " color", ColorSet.rand() );
        this.colors = new Vector<ColorSet>();
        this.colors.add( shapeColor );
        
    }

    @Override
    public LayerCategory getCategory() {
        return LayerCategory.Geometry;
    }
    
    @Override
    public void restoreAuthorization() {
        authorized.clear();
        authorized.addAll(getAuthorizationForGeometricLayer());
    }

    @Override
    public boolean isContener() {
        return false;
    }

    @Override
    public boolean isDeletable() {
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
    public AbstractAlgorithm getAlgorithm() {
        return null;
    }

    @Override
    public String getLayerType() {
        return shape.getClass().getSimpleName();
    }

    @Override
    public ImageIcon getLayerTypeIcon() {
        return toolType.getImageIcon();
    }

    @Override
    public void accept(InterfaceGraphicVisitor visitor) {
        if (isActive()){
            visitor.setColor(shapeColor.color);
            shape.accept((InterfaceShapePainterVisitor) visitor);
        }
    }
    
    @Override
    public int hashCode() {
        return shape.hashCode();
    }

    @Override
    public boolean contains(Point p) {
        return (isActive()) ? shape.contains(p) : false;
    }

    @Override
    public InterfaceMapper getTopContainMappedComposition(Point p) {
        return (isActive()) ? shape.getContainMappedComposition(p) : null;
    }

    @Override
    public void translate(Point p) {
        shape.translate(p);
    }

}
