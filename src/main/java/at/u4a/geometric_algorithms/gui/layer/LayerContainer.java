package at.u4a.geometric_algorithms.gui.layer;

import java.util.EnumSet;

import at.u4a.geometric_algorithms.geometric.AbstractShape;

public abstract class LayerContainer extends AbstractLayer {

    public LayerContainer(EnumSet<AuthorizedAction> authorized) {
        super(authorized);
    }

    @Override
    public AbstractShape getShape() {
        return null;
    }
    
    @Override
    public LayerCategory getCategory() {
        return LayerCategory.Container;
    }

    @Override
    public boolean isContener() {
        return true;
    }


}
