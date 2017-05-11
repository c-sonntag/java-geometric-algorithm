package at.u4a.geometric_algorithms.gui.element;

import javax.swing.JToolBar;

import at.u4a.geometric_algorithms.algorithm.AlgorithmBuilderInterface;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;

public interface InterfaceLayerAction {

    public void addLayerActionListenerOfDelete(Runnable func);
    
    public void setActiveLayer(AbstractLayer l);
    
    public void applyAlgorithm(AlgorithmBuilderInterface abi);
    
    public JToolBar getToolBar();


}
