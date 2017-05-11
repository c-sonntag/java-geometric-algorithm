package at.u4a.geometric_algorithms.gui.layer;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.ImageIcon;

import at.u4a.geometric_algorithms.algorithm.AbstractAlgorithm;
import at.u4a.geometric_algorithms.algorithm.Algorithm;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;

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
        String subLayerChain = "";
        for (AbstractLayer al : subLayer)
            subLayerChain += (subLayerChain.isEmpty() ? "" : ",") + al.getLayerName();
        return getLayerName() + "[" + subLayerChain + "]";
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
    public void accept(InterfaceGraphicVisitor visitor) {
        if (isActive()) {
            for (AbstractLayer al : subLayer)
                al.accept(visitor);
            //
            algorithm.accept(subLayer, visitor);
        }
    }

    @Override
    public boolean contains(Point p) {
        if (isActive())
            for (AbstractLayer al : subLayer)
                if (al.contains(p))
                    return true;
        return false;
    }

    @Override
    public InterfaceMapper getTopContainMappedComposition(Point p) {
        if (isActive())
            for (AbstractLayer al : subLayer) {
                InterfaceMapper im = al.getTopContainMappedComposition(p);
                if (im != null)
                    return im;
            }
        return null;
    }

    @Override
    public void translate(Point p) {
        for (AbstractLayer al : subLayer)
            al.translate(p);
    }

}
