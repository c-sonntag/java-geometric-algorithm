package at.u4a.geometric_algorithms.gui.element;

import java.util.AbstractList;

import javax.swing.JToolBar;

import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;

public interface InterfaceLayerAction {

    public void addLayerActionListenerOfDelete(Runnable func);
    public void addLayerActionListenerOfDeleteAlgorithm(Runnable func);
    
    public void setActiveLayers(AbstractList<AbstractLayer> layers);
    
    public void applyAlgorithm(InterfaceAlgorithmBuilder abi);
    
    public JToolBar getToolBar();


}
