package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;

import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;

public interface InterfaceAlgorithmBuilder {

    String getName();

    AbstractLayer builder(AbstractList<AbstractLayer> layers);

    boolean canApply(AbstractList<AbstractLayer> layers);

}