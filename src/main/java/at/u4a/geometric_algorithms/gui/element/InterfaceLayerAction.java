package at.u4a.geometric_algorithms.gui.element;

import javax.swing.JToolBar;

public interface InterfaceLayerAction {

    public void addLayerActionListenerOfDelete(Runnable func);
    
    public void setDelete(boolean b);
    
    public JToolBar getToolBar();


}
