package at.u4a.geometric_algorithms.algorithm;

import java.util.Vector;

import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.element.StatusBar;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;

public abstract class AbstractAlgorithm {

    private static int STATUS_INFORM_MOD = 10;

    private int mutableStatusCounter = 0;
    private String algoritmName;

    AbstractAlgorithm() {
        algoritmName = this.getClass().getSimpleName();
    }

    protected void statusStartBuild() {
        StatusBar.setActiveMessage(algoritmName + " building ...");
        mutableStatusCounter = 0;
    }

    protected void statusFinishBuild() {
        StatusBar.setActiveMessage(algoritmName + " made in : " + mutableStatusCounter + " loops");
    }

    protected void statusInteruptBuild() {
        StatusBar.setActiveMessage(algoritmName + " interupted after : " + mutableStatusCounter + " loops");
    }

    protected void statusBuildIs(boolean state) {
        if (state)
            statusFinishBuild();
        else
            statusInteruptBuild();
    }

    protected void statusAddCounter() {
        mutableStatusCounter++;
        if ((mutableStatusCounter % STATUS_INFORM_MOD) == 0)
            StatusBar.setActiveMessage(algoritmName + " building " + mutableStatusCounter + "...");

    }

    public abstract void accept(Vector<AbstractLayer> v, InterfaceGraphicVisitor visitor);

    public abstract AbstractShape getCompositeShape();

}
