package at.u4a.geometric_algorithms.gui.tools.state;

import java.io.File;

import javax.swing.ImageIcon;

import at.u4a.geometric_algorithms.geometric.CloudOfPoints;
import at.u4a.geometric_algorithms.geometric.InterfaceGeometric;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.geometric.Segment;
import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.element.Drawer;
import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import at.u4a.geometric_algorithms.gui.element.InterfaceDrawerAction;
import at.u4a.geometric_algorithms.gui.layer.GeometricLayer;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.scene.input.MouseEvent;

public class CloudOfPointsToolState extends ToolState {

    /* */

    class DirectSelectionPolygonTool extends DirectSelectionToolState {

        protected void findOverlay(Point p) {
            for (InterfaceMapper c : cop.getMappedComposition()) {
                if (c.contains(p)) {
                    overlayComposant = c;
                    return;
                }
            }
            overlayComposant = null;
        }

        protected void setSelected() {
            // Nothing
        }

    }

    /* */

    static int ColoudOfSegmentsCount = 1;

    private CloudOfPoints cop = new CloudOfPoints();

    private Point currentPointToPlace = null;

    private DirectSelectionPolygonTool directSelectionTool = new DirectSelectionPolygonTool();

    /* */

    public void mouseExited(Drawer drawer) {
        currentPointToPlace = null;
        super.mouseExited(drawer);
    }

    /* */

    private InterfaceDrawerAction da;

    private void refreshDA() {
        if (da != null) {
            da.haveValid(cop.cloud.size() > 0);
            da.haveCancel((cop.cloud.size() > 0) || (currentState != State.Waiting));
        }
    }

    public void init(Drawer drawer) {
        da = drawer.getDA();
        refreshDA();

        //
        directSelectionTool.init(drawer);
    }

    public void valid(Drawer drawer) {
        GeometricLayer<CloudOfPoints> copLayer = new GeometricLayer<CloudOfPoints>(cop);
        copLayer.setLayerName("cop" + String.valueOf(ColoudOfSegmentsCount));
        ColoudOfSegmentsCount++;

        drawer.getDS().getLayerMannager().addLayer(copLayer);

        //
        cop = new CloudOfPoints();
        init(drawer);
    }

    public void cancel(Drawer drawer) {
        cop.clear();
        init(drawer);
    }

    public Boolean needValidOperation() {
        return true;
    }

    /* */

    @Override
    public void mousePressed(DrawerContext context, MouseEvent event) {

        //
        directSelectionTool.mousePressed(context, event);
        if (directSelectionTool.isHave())
            return;

        //
        if (!isLeftClick(event))
            return;
        if (currentState != State.Waiting)
            return;
        //
        currentState = State.Started;
        refreshDA();
    }

    @Override
    public void mouseReleased(DrawerContext context, MouseEvent event) {

        //
        directSelectionTool.mouseReleased(context, event);
        if (directSelectionTool.isHave())
            return;

        //
        if (!isLeftClick(event))
            return;
        //
        if (currentState == State.Started) {
            //
            Point p = new Point();
            p.set(currentPointToPlace);
            cop.convertToOrigin(p);
            cop.cloud.add(p);
            //
            refreshDA();
        }

        currentState = State.Waiting;
    }

    @Override
    public void mouseMoved(DrawerContext context, MouseEvent event) {

        directSelectionTool.mouseMoved(context, event);
        if (!directSelectionTool.isHave()) {

            if (currentPointToPlace == null)
                currentPointToPlace = new Point();

            currentPointToPlace.set(event.getX(), event.getY());
        }

        context.repaint();
    }

    @Override
    public void paint(InterfaceGraphicVisitor painter) {
        // painter.setSelected(selected);

        painter.visit(cop);

        if (!directSelectionTool.isHave())
            if (currentPointToPlace != null)
                painter.visit(currentPointToPlace);
    }

}
