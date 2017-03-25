package at.u4a.geometric_algorithms.gui.tools.state;

import java.io.File;

import javax.swing.ImageIcon;

import at.u4a.geometric_algorithms.geometric.InterfaceGeometric;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.element.Drawer;
import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import at.u4a.geometric_algorithms.gui.layer.Geometric;
import at.u4a.geometric_algorithms.gui.tools.Tool;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

public class DirectSelectionToolState extends ToolState {

    /* TOOLBAR */

    public void valid(Drawer drawer) {
    }

    public void cancel(Drawer drawer) {
    }

    public Boolean needValidOperation() {
        return false;
    }

    /* MOUSE */

    protected InterfaceGeometric ig = null;
    protected boolean inMove = false;
    protected Point startPoint = null;
    protected Point translatePoint = null;

    @Override
    public void mousePressed(DrawerContext context, MouseEvent event) {

        if (ig != null) {
            inMove = true;
            startPoint = new Point(event.getX(), event.getY());
            translatePoint = new Point(0, 0);
        }

        /*
         * if (!isLeftClick(event)) return; if (currentState != State.Started)
         * return; // addPlace(context, event.getX(), event.getY());
         */
    }

    @Override
    public void mouseReleased(DrawerContext context, MouseEvent event) {

        inMove = false;

        /*
         * if (!isLeftClick(event)) return; // if (currentState ==
         * State.Waiting) { currentState = State.Started;
         * poly.origin.set(event.getX(), event.getY()); addPlace(context,
         * event.getX(), event.getY()); } inPlace = false;
         */
    }

    @Override
    public void mouseMoved(DrawerContext context, MouseEvent event) {

        if (inMove == true && ig != null) {
            translatePoint.set(event.getX() - startPoint.x, event.getY() - startPoint.y);
            ig.translate(translatePoint);
        }

        /*
         * Point overPoint = poly.containPoint(new Point(event.getX(),
         * event.getY()), Point.POINT_RAYON); if (overPoint != null) {
         * overPoint.x += 10; }
         * 
         * if (inPlace) { currentPlacedPoint.set(event.getX(), event.getY());
         * poly.convertToOrigin(currentPlacedPoint); } else { if
         * (currentPointToPlace == null) currentPointToPlace = new Point();
         * currentPointToPlace.set(event.getX(), event.getY()); }
         * context.repaint();
         */
    }

    @Override
    public void paint(InterfaceGraphicVisitor painter) {

    }

}
