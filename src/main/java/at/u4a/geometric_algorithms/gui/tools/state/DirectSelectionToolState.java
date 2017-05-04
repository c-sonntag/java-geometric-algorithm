package at.u4a.geometric_algorithms.gui.tools.state;

import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import at.u4a.geometric_algorithms.geometric.InterfaceGeometric;

public class DirectSelectionToolState extends SelectionToolState {

    protected InterfaceMapper overlayComposant = null;
    
    /* MOUSE */


    protected void setMoveComponent() {
        if (overlayComposant != null)
            ig = (InterfaceGeometric) overlayComposant;
    }
    
    protected boolean isHave() {
        return overlayComposant != null;
    }
    
    protected void findOverlay(Point p) {
        super.findOverlay(p);
        if(overlay != null) {
            for(InterfaceMapper c : overlay.getShape().getMappedComposition() ) {
                if(c.contains(p)) {
                    overlayComposant = c;
                    return;
                }   
            }
        }
        overlayComposant = null;
    }
    
    
    
    

}
