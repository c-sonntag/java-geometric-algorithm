package at.u4a.geometric_algorithms.gui.layer;

import java.util.Vector;

import javax.swing.ImageIcon;

import at.u4a.geometric_algorithms.algorithm.AbstractAlgorithm;
import at.u4a.geometric_algorithms.algorithm.Algorithm;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;
import at.u4a.geometric_algorithms.gui.tools.Tool;

public class AlgorithmLayer<TAlgorithm extends AbstractAlgorithm> extends AbstractLayer {

    private final TAlgorithm algorithm;
    private final Algorithm algorithmType;

    public AlgorithmLayer(TAlgorithm algorithm, Algorithm algorithmType) {
        this.algorithm = algorithm;
        this.algorithmType = algorithmType;
    }
    
    @Override
    public AbstractShape getShape() {
        return null;
    }
    
    @Override
    public LayerCategory getCategory() {
        return LayerCategory.Algorithm;
    }

    @Override
    public boolean isContener() {
        return true;
    }
    
    public String toString() {
        return "[" + getLayerName() + "]";
    }

    @Override
    public String getLayerType() {
        return algorithm.getClass().getSimpleName();
    }

    @Override
    public ImageIcon getLayerTypeIcon() {
        return algorithmType.getImageIcon();
    }

    @Override
    public boolean isDeletable() {
        return true;
    }

    @Override
    public Vector<AbstractLayer> getSubLayer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void accept(InterfaceShapePainterVisitor visitor) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean contains(Point p) {
        // TODO Auto-generated method stub
        return false;
    }


}
