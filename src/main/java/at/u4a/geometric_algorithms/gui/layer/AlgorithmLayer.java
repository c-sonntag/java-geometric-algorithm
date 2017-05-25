package at.u4a.geometric_algorithms.gui.layer;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.ImageIcon;

import at.u4a.geometric_algorithms.algorithm.AbstractAlgorithm;
import at.u4a.geometric_algorithms.algorithm.Algorithm;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer.ColorSet;

public class AlgorithmLayer<TAlgorithm extends AbstractAlgorithm> extends AbstractLayer {

    private final TAlgorithm algorithm;
    private final Algorithm algorithmType;

    private final AbstractList<AbstractLayer> subLayer;

    private final ColorSet algorithmColor;

    public AlgorithmLayer(TAlgorithm algorithm, Algorithm algorithmType, AbstractList<AbstractLayer> subLayer) {
        super(getAuthorizationForAlgorithmLayer());
        this.algorithm = algorithm;
        this.algorithmType = algorithmType;

        //
        this.subLayer = new Vector<AbstractLayer>();
        this.subLayer.addAll(subLayer);

        //
        this.algorithmColor = new ColorSet(getLayerType() + " color", ColorSet.rand());
        this.colors = new Vector<ColorSet>();
        this.colors.add(algorithmColor);

    }

    @Override
    public AbstractShape getShape() {
        return algorithm.getCompositeShape();
    }

    @Override
    public AbstractAlgorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public LayerCategory getCategory() {
        return LayerCategory.Algorithm;
    }

    @Override
    public void restoreAuthorization() {
        authorized.clear();
        authorized.addAll(getAuthorizationForAlgorithmLayer());
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
    public AbstractList<AbstractLayer> getSubLayer() {
        return subLayer;
    }

    @Override
    public void accept(InterfaceGraphicVisitor visitor) {
        if (isActive()) {
            for (AbstractLayer al : subLayer)
                al.accept(visitor);
            //
            visitor.setColor(algorithmColor.color);
            algorithm.accept(subLayer, visitor);
        }
    }

    @Override
    public int hashCode() {
        return algorithm.hashCode();
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
