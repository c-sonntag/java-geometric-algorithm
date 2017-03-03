package at.u4a.geometric_algorithms.gui.layer;

import java.util.Vector;

public abstract class AbstractGeometric extends AbstractLayer  {

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


}
