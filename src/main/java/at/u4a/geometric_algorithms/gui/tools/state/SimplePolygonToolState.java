package at.u4a.geometric_algorithms.gui.tools.state;

import java.io.File;

import javax.swing.ImageIcon;

import at.u4a.geometric_algorithms.geometric.InterfaceGeometric;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.element.Drawer;
import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import at.u4a.geometric_algorithms.gui.element.InterfaceDrawerAction;
import at.u4a.geometric_algorithms.gui.layer.GeometricLayer;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.scene.input.MouseEvent;

public class SimplePolygonToolState extends ToolState {

    static int PolygonCount = 1;

    private Boolean inPlace = false;
    private Polygon poly = new Polygon();

    private Point currentPlacedPoint = new Point();
    private Point currentPointToPlace = null;

    /* */

    private InterfaceDrawerAction da;

    private void refreshDA() {
        if (da != null) {
            da.haveValid(poly.perimeter.size() > 2);
            da.haveCancel(poly.perimeter.size() > 0);
        }
    }

    public void init(Drawer drawer) {
        da = drawer.getDA();
        refreshDA();
    }

    public void valid(Drawer drawer) {
        GeometricLayer<Polygon> polygonLayer = new GeometricLayer<Polygon>(poly);
        polygonLayer.setLayerName("p" + String.valueOf(PolygonCount));
        PolygonCount++;
        
        drawer.getDS().getLayerMannager().addLayer(polygonLayer);

        //
        poly = new Polygon();
        init(drawer);
    }

    public void cancel(Drawer drawer) {
        poly.clear();
        init(drawer);
    }

    public Boolean needValidOperation() {
        return true;
    }

    /* */

    private void addPlace(DrawerContext context, double x, double y) {
        currentPlacedPoint = new Point(x, y);
        poly.convertToOrigin(currentPlacedPoint);
        // currentPlacedPoint = poly.getByOrigin(x, y);
        poly.addPoint(currentPlacedPoint); // event.getX(), event.getY());
        inPlace = true;
        context.repaint();

        //
        refreshDA();
    }

    @Override
    public void mousePressed(DrawerContext context, MouseEvent event) {
        if (!isLeftClick(event))
            return;
        if (currentState != State.Started)
            return;
        //
        addPlace(context, event.getX(), event.getY());
    }

    @Override
    public void mouseReleased(DrawerContext context, MouseEvent event) {
        if (!isLeftClick(event))
            return;
        //
        if (currentState == State.Waiting) {
            currentState = State.Started;
            poly.origin.set(event.getX(), event.getY());
            addPlace(context, event.getX(), event.getY());
        }
        inPlace = false;
    }

    @Override
    public void mouseMoved(DrawerContext context, MouseEvent event) {

        //InterfaceGeometric overIG = poly.getContains(new Point(event.getX(), event.getY()));
        //if (overIG != null) {
        //    overIG.translate(new Point(1, 0));
        //}

        if (inPlace) {
            currentPlacedPoint.set(event.getX(), event.getY());
            poly.convertToOrigin(currentPlacedPoint);
        } else {
            if (currentPointToPlace == null)
                currentPointToPlace = new Point();
            currentPointToPlace.set(event.getX(), event.getY());
        }
        context.repaint();
    }

    @Override
    public void paint(InterfaceGraphicVisitor painter) {
        // painter.setSelected(selected);

        painter.setWorkedConstruct(true);
        painter.visit(poly);
        painter.setWorkedConstruct(false);

        if (!inPlace)
            if (currentPointToPlace != null)
                painter.visit(currentPointToPlace);

        /*
         * context.setStroke(Color.BLACK); context.strokeLine(x - 5, y, x + 5,
         * y); context.strokeLine(x, y - 5, x, y + 5);
         */
    }

}
