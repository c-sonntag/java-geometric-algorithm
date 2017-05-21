package at.u4a.geometric_algorithms.algorithm;

import java.util.Vector;

import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.element.StatusBar;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;

public abstract class AbstractAlgorithm {

    private static int STATUS_INFORM_MOD = 10;

    private int mutableStatusCounter = 0;
    private final String actionName, algoritmName;

    private long mutableStartChrono = 0, mutableStopChrono = 0, mutableTotalTimeChrono = 0;

    AbstractAlgorithm(String actionName) {
        this.actionName = actionName;
        algoritmName = this.getClass().getSimpleName();
    }

    AbstractAlgorithm() {
        this.actionName = "loop";
        algoritmName = this.getClass().getSimpleName();
    }

    private void startChrono() {
        mutableStartChrono = System.currentTimeMillis();
    }

    private void stopChrono() {
        mutableStopChrono = System.currentTimeMillis();
        mutableTotalTimeChrono = (mutableStopChrono - mutableStartChrono);
    }

    private String getDuarationChrono() {
        return "(duration:"+mutableTotalTimeChrono+" ms)";
    }

    protected void statusStartBuild() {
        StatusBar.setActiveMessage(algoritmName + " building ...");
        mutableStatusCounter = 0;
        startChrono();
    }

    protected void statusFinishBuild() {
        stopChrono();
        StatusBar.setActiveMessage(algoritmName + " made in : " + mutableStatusCounter + " " + actionName + " " + getDuarationChrono());
    }

    protected void statusInteruptBuild() {
        stopChrono();
        StatusBar.setActiveMessage(algoritmName + " interupted after : " + mutableStatusCounter + " " + actionName + " " + getDuarationChrono());
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
