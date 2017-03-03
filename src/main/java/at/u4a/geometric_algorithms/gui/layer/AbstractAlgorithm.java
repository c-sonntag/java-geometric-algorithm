package at.u4a.geometric_algorithms.gui.layer;


public abstract class  AbstractAlgorithm extends AbstractLayer {


    @Override
    public LayerCategory getCategory() {
        return LayerCategory.Algorithm;
    }

    @Override
    public Boolean isContener() {
        return true;
    }


}
