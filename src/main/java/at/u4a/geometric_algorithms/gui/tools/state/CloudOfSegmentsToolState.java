package at.u4a.geometric_algorithms.gui.tools.state;

import java.io.File;

import javax.swing.ImageIcon;

import at.u4a.geometric_algorithms.geometric.CloudOfSegments;
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
import at.u4a.geometric_algorithms.gui.tools.ToolState.State;
import javafx.scene.input.MouseEvent;

public class CloudOfSegmentsToolState extends ToolState {

    private static final Point MIN_SEGMENT_SIZE = new Point(5, 5);

    /* */

    class DirectSelectionPolygonTool extends DirectSelectionToolState {

        protected void findOverlay(Point p) {
            for (InterfaceMapper c : cof.getMappedComposition()) {
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

    private CloudOfSegments cof = new CloudOfSegments();
    private Segment s = new Segment();

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
            da.haveValid(cof.cloud.size() > 0);
            da.haveCancel((cof.cloud.size() > 0) || (currentState != State.Waiting));
        }
    }

    public void init(Drawer drawer) {
        da = drawer.getDA();
        refreshDA();

        //
        directSelectionTool.init(drawer);
    }

    public void valid(Drawer drawer) {
        GeometricLayer<CloudOfSegments> cofLayer = new GeometricLayer<CloudOfSegments>(cof);
        cofLayer.setLayerName("cof" + String.valueOf(ColoudOfSegmentsCount));
        ColoudOfSegmentsCount++;

        drawer.getDS().getLayerMannager().addLayer(cofLayer);

        //
        cof = new CloudOfSegments();
        init(drawer);
    }

    public void cancel(Drawer drawer) {
        cof.clear();
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
        s.a.set(currentPointToPlace);
        cof.convertToOrigin(s.a);
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
            s.b.set(event.getX(), event.getY());
            cof.convertToOrigin(s.b);

            if (((Math.abs(s.a.x - s.b.x) >= MIN_SEGMENT_SIZE.x) && (Math.abs(s.a.y - s.b.y) >= MIN_SEGMENT_SIZE.y))) {
                //
                cof.cloud.add(s);
                s = new Segment();
                //
                refreshDA();
            }
        }

        currentState = State.Waiting;
    }

    @Override
    public void mouseMoved(DrawerContext context, MouseEvent event) {

        if (currentState == State.Started) {

            s.b.set(event.getX(), event.getY());
            cof.convertToOrigin(s.b);

        } else {

            directSelectionTool.mouseMoved(context, event);
            if (!directSelectionTool.isHave()) {

                if (currentPointToPlace == null)
                    currentPointToPlace = new Point();

                currentPointToPlace.set(event.getX(), event.getY());

            }
        }

        context.repaint();
    }

    @Override
    public void paint(InterfaceGraphicVisitor painter) {
        // painter.setSelected(selected);

        painter.visit(cof);

        if (!directSelectionTool.isHave())
            if (currentState == State.Started) {
                if (s != null) {
                    painter.setDotted(true);
                    painter.visit(s);
                    painter.setDotted(false);
                }
            } else {
                if (currentPointToPlace != null)
                    painter.visit(currentPointToPlace);
            }
    }

}
