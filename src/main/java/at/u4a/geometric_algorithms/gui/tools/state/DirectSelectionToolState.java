package at.u4a.geometric_algorithms.gui.tools.state;

import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import at.u4a.geometric_algorithms.geometric.InterfaceGeometric;

public class DirectSelectionToolState extends SelectionToolState {

    protected InterfaceMapper overlayComposant = null;

    /* MOUSE */

    protected boolean isHave() {
        return overlayComposant != null;
    }

    protected void translate(Point p) {
        overlayComposant.translate(p);
    }

    protected void findOverlay(Point p) {
        super.findOverlay(p);
        if (overlay != null) {
            overlayComposant = overlay.getTopContainMappedComposition(p);
        } else
            overlayComposant = null;
    }
}
