package at.u4a.geometric_algorithms.algorithm;

import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;

public interface InterfaceAlgorithmBuilder {

    String getName();

    AbstractLayer builder(AbstractLayer l);

    boolean canApply(AbstractLayer l);

}