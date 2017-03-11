package at.u4a.geometric_algorithms.gui.layer;

import at.u4a.geometric_algorithms.geometric.AbstractShape;

public abstract class  AbstractAlgorithm extends AbstractLayer {


    @Override
    public AbstractShape getShape() {
        return null;
    }
    
    @Override
    public LayerCategory getCategory() {
        return LayerCategory.Algorithm;
    }

    @Override
    public Boolean isContener() {
        return true;
    }


}
