package at.u4a.geometric_algorithms.gui.layer;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.ImageIcon;

import at.u4a.geometric_algorithms.algorithm.AbstractAlgorithm;
import at.u4a.geometric_algorithms.algorithm.Algorithm;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;

public class AlgorithmLayer<TAlgorithm extends AbstractAlgorithm> extends AbstractLayer {

    private final TAlgorithm algorithm;
    private final Algorithm algorithmType;

    private final Vector<AbstractLayer> subLayer;

    public AlgorithmLayer(TAlgorithm algorithm, Algorithm algorithmType, AbstractLayer... subLayer) {
        this.algorithm = algorithm;
        this.algorithmType = algorithmType;
        this.subLayer = new Vector<AbstractLayer>(Arrays.asList(subLayer));
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
        return subLayer;
    }

    @Override
    public void accept(InterfaceShapePainterVisitor visitor) {
        for (AbstractLayer al : subLayer)
            al.accept(visitor);
        //
        algorithm.accept(subLayer, visitor);
    }

    @Override
    public boolean contains(Point p) {
        for (AbstractLayer al : subLayer)
            if (al.contains(p))
                return true;
        return false;
    }

    @Override
    public InterfaceMapper getTopContainMappedComposition(Point p) {
        for (AbstractLayer al : subLayer)
            if (al.isActive()) {
                InterfaceMapper im = al.getTopContainMappedComposition(p);
                if (im != null)
                    return im;
            }
        return null;
    }

}
