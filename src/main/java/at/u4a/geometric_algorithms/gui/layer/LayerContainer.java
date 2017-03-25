package at.u4a.geometric_algorithms.gui.layer;

import at.u4a.geometric_algorithms.geometric.AbstractShape;

public abstract class LayerContainer extends AbstractLayer {

    @Override
    public AbstractShape getShape() {
        return null;
    }
    
    @Override
    public LayerCategory getCategory() {
        return LayerCategory.Container;
    }

    @Override
    public Boolean isContener() {
        return true;
    }


}
