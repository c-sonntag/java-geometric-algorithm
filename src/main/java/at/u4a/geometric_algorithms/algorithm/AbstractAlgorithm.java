package at.u4a.geometric_algorithms.algorithm;

import java.util.Vector;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;

public abstract class AbstractAlgorithm {
    
    public abstract void accept(Vector<AbstractLayer> v, InterfaceShapePainterVisitor visitor);

}
