package at.u4a.geometric_algorithms.graphic_visitor;

import java.util.Iterator;

import at.u4a.geometric_algorithms.geometric.CloudOfSegments;
import at.u4a.geometric_algorithms.geometric.Line;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.geometric.Rectangle;
import at.u4a.geometric_algorithms.geometric.Segment;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GraphicPainter implements InterfaceGraphicVisitor {

    /** La couleur d'un point a l'ecran. */
    private final static Color pointColor = Color.GRAY;

    /** La couleur d'un segment a l'ecran. */
    private final static Color segmentColor = Color.BLUE;

    /// ** * La couleur d'une triangulation a l'ecran. */
    // private final static Color triangulationColor = Color.CYAN;

    /** La couleur d'un point selectionne a l'ecran. */
    private final static Color selectedColor = Color.RED;

    /* ACESS */

    protected boolean isSelected = false;
    protected boolean isOverlay = false;

    protected Color color = null; // strockeColor, fillColor, selected Color ...

    protected GraphicsContext gc = null;

    public void setOverlay(boolean overlay) {
        isOverlay = overlay;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setGraphicsContext(GraphicsContext gc) {
        this.gc = gc;
    }

    @Override
    public GraphicsContext getGraphicsContext() {
        return this.gc;
    }

    /* GEOMETRIC */

    public void visit(Point p) {
        gc.setFill(isSelected ? selectedColor : pointColor);
        gc.setStroke(Color.BLACK);
        gc.fillOval((int) (p.x - 2 * Point.POINT_RAYON), (int) (p.y - 2 * Point.POINT_RAYON), 2 * 2 * Point.POINT_RAYON, 2 * 2 * Point.POINT_RAYON);
        gc.strokeOval((int) (p.x - 2 * Point.POINT_RAYON), (int) (p.y - 2 * Point.POINT_RAYON), 2 * 2 * Point.POINT_RAYON, 2 * 2 * Point.POINT_RAYON);
    }

    /** TODO doit dessiner une ligne et non un segment ! */
    public void visit(Line l) {
        gc.setStroke(isSelected ? selectedColor : segmentColor);
        gc.strokeLine((int) l.a.x, (int) l.a.y, (int) l.b.x, (int) l.b.y);
    }

    public void visit(Segment s) {
        gc.setStroke(isSelected ? selectedColor : segmentColor);
        gc.strokeLine((int) s.a.x, (int) s.a.y, (int) s.b.x, (int) s.b.y);
    }

    /* SHAPES */

    public void visit(Polygon poly) {
        final Segment sToOrigin = new Segment();
        for (Segment s : poly) {
            sToOrigin.set(s);
            poly.convertToStandard(sToOrigin.a);
            poly.convertToStandard(sToOrigin.b);
            visit(sToOrigin);
        }
        final Point pToOrigin = new Point();
        for (Point p : poly.perimeter) {
            pToOrigin.set(p);
            poly.convertToStandard(pToOrigin);
            visit(pToOrigin);
        }
    }

    public void visit_onetracing(Polygon poly) { // visit_onetracing
        Segment segment = new Segment();
        Point point = new Point(), lastPoint = null;
        for (Point p : poly.perimeter) {
            point.set(poly.origin.x + p.x, poly.origin.y + p.y);
            visit(point);
            if (lastPoint != null) {
                segment.a.set(poly.origin.x + lastPoint.x, poly.origin.y + lastPoint.y);
                segment.b.set(point);
                visit(segment);
            }
            lastPoint = p;
        }
    }

    public void visit(CloudOfSegments clouds) {

    }

    public void visit(Rectangle rectangle) {
        gc.setStroke(Color.RED);

        final Point oppositeSide = rectangle.getOppositeSide();
        gc.strokeRect(/* MinHorizontal */ Math.min(rectangle.origin.x, oppositeSide.x), /* MinVertical */ Math.min(rectangle.origin.y, oppositeSide.y), /* SizeHorizontal */ Math.abs(oppositeSide.x - rectangle.origin.x), /* SizeVertical */ Math.abs(oppositeSide.y - rectangle.origin.y));
    }

}