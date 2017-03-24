package at.u4a.geometric_algorithms.gui.tools.state;

import java.io.File;

import javax.swing.ImageIcon;

import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.element.Drawer;
import at.u4a.geometric_algorithms.gui.element.DrawerContext;
import at.u4a.geometric_algorithms.gui.tools.Tool;
import at.u4a.geometric_algorithms.gui.tools.ToolState;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

public class SimplePolygonToolState extends ToolState {

    private Boolean inPlace = false;
    private final Polygon poly = new Polygon();

    private Point currentPlacedPoint = new Point();
    private Point currentPointToPlace = null;

    // private double x, y;

    /* */

    public void valid(Drawer drawer) {
        // Nothing
    }

    public void cancel(Drawer drawer) {
        // Nothing
    }

    public Boolean needValidOperation() {
        return true;
    }

    /* */

    public void mouseEntered(Drawer drawer) {
        drawer.setCursor(Cursor.
        
        // drawer.setCursor(Cursor.NONE);
        // drawer.setCursor(Cursor.E_RESIZE);
        // setIcon(new ImageIcon(Tool.iconRessource + tool.icon));
        // Image image = new Image("mycursor.png");
        // ImageIcon(Tool.iconRessource + tool.icon);
        // java.net.URL imgURL = getClass().getResource(path);
        //String path = Tool.iconRessource + Tool.ShapeSimplePoligon.icon;
        //System.out.println("Path : " + path);

        // Image imgIcn = new Image(new File(path).toURI().toString());
        
        //Image imgIcon = new Image("file:"+path);

        //Image imgIcon = new Image("file:" + path, 8, 8, false, true);

        //drawer.setCursor(new ImageCursor(imgIcon));

        // drawer.setCursor(new ImageCursor(imgIcon, imgIcon.getWidth() / 2,
        // imgIcon.getHeight() / 2));

        /*
         * , image.getWidth() / 2, image.getHeight() /2));
         */
    }

    /* */

    private void addPlace(DrawerContext context, double x, double y) {
        currentPlacedPoint = new Point(x, y);
        poly.convertToOrigin(currentPlacedPoint);
        // currentPlacedPoint = poly.getByOrigin(x, y);
        poly.addPoint(currentPlacedPoint); // event.getX(), event.getY());
        inPlace = true;
        context.repaint();
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

        Point overPoint = poly.containPoint(new Point(event.getX(), event.getY()));
        if (overPoint != null) {
            overPoint.x += 10;
        }

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

        painter.visit(poly);
        if (!inPlace)
            if (currentPointToPlace != null)
                painter.visit(currentPointToPlace);

        /*
         * context.setStroke(Color.BLACK); context.strokeLine(x - 5, y, x + 5,
         * y); context.strokeLine(x, y - 5, x, y + 5);
         */
    }

}
