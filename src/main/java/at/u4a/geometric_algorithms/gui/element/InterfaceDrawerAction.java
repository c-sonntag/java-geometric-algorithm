package at.u4a.geometric_algorithms.gui.element;

import javax.swing.JToolBar;

public interface InterfaceDrawerAction {

    public void addDrawerActionListenerOfValid(Runnable func);

    public void addDrawerActionListenerOfCancel(Runnable func);

    public void activeDrawerAction(boolean b);
    
    public void haveValid(boolean b);
    
    public void haveCancel(boolean b);

    public JToolBar getToolBar();

}
