package at.u4a.geometric_algorithms.graphic_visitor;

import java.util.Iterator;

import at.u4a.geometric_algorithms.geometric.CloudOfSegments;
import at.u4a.geometric_algorithms.geometric.Line;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.geometric.Rectangle;
import at.u4a.geometric_algorithms.geometric.Segment;
import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class GraphicPainter implements InterfaceGraphicVisitor {

    /// ** La couleur d'un point a l'ecran. */
    // private final static Color pointColor = Color.GRAY;

    /** La couleur d'un segment a l'ecran. */
    private final static Color segmentColor = Color.BLUE;

    /// ** * La couleur d'une triangulation a l'ecran. */
    // private final static Color triangulationColor = Color.CYAN;

    /** La couleur d'un point selectionne a l'ecran. */
    private final static Color selectedColor = Color.RED;

    private Paint PointPaint = Color.GRAY; // gc.getStroke();

    // gc.getStroke();

    // private final static BasicStroke stokeDotted = new BasicStroke(3.0f,
    // BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));

    /* ACESS */

    protected boolean isSelected = false;
    protected boolean isOverlay = false;
    protected boolean isWorkedConstruct = false;
    protected boolean isDotted = false;

    protected Color color = null; // strockeColor, fillColor, selected Color ...

    protected final GraphicsContext gc;

    /* INITIALIZER */

    public GraphicPainter(GraphicsContext gc) {
        this.gc = gc;
    }

    /* SETTING */

    public void setOverlay(boolean overlay) {
        isOverlay = overlay;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setWorkedConstruct(boolean b) {
        this.isWorkedConstruct = b;
    }

    public void setDotted(boolean b) {
        this.isDotted = b;

        //
        if (b) {
            gc.setLineDashes(5, 10);
        } else {
            gc.setLineDashes(1);
        }
    }

    public boolean getDotted() {
        return this.isDotted;
    }

    // public void setGraphicsContext(GraphicsContext gc) {
    // this.gc = gc;
    // }

    /* PROTECTED */

    private Paint currentPaintStroke = null, currentPaintFill = null;

    protected void saveCurrentPaint() {
        currentPaintStroke = gc.getStroke();
        currentPaintFill = gc.getFill();
    }

    protected void restoreLastPaint() {
        if (currentPaintStroke != null)
            gc.setStroke(currentPaintStroke);
        if (currentPaintFill != null)
            gc.setFill(currentPaintFill);
    }

    protected void setPointPaint() {
        // gc.setFill(isSelected ? selectedColor : pointColor);
        gc.setFill(Color.GRAY);
        gc.setStroke(Color.BLACK);
    }

    /* GEOMETRIC */

    public void visit(Point p) {
        saveCurrentPaint();
        setPointPaint();
        gc.fillOval((int) (p.x - 2 * Point.POINT_RAYON), (int) (p.y - 2 * Point.POINT_RAYON), 2 * 2 * Point.POINT_RAYON, 2 * 2 * Point.POINT_RAYON);
        gc.strokeOval((int) (p.x - 2 * Point.POINT_RAYON), (int) (p.y - 2 * Point.POINT_RAYON), 2 * 2 * Point.POINT_RAYON, 2 * 2 * Point.POINT_RAYON);
        restoreLastPaint();
    }

    /** TODO doit dessiner une ligne et non un segment ! */
    public void visit(Line l) {
        // gc.setStroke(isSelected ? selectedColor : segmentColor);
        gc.strokeLine((int) l.a.x, (int) l.a.y, (int) l.b.x, (int) l.b.y);
    }

    public void visit(Segment s) {
        // gc.setStroke(isSelected ? selectedColor : segmentColor);
        gc.strokeLine((int) s.a.x, (int) s.a.y, (int) s.b.x, (int) s.b.y);
    }

    /* SHAPES */

    public void visit(Polygon poly) {
        //
        final Segment sToOrigin = new Segment();

        //
        Iterator<Segment> s_it = poly.iterator();
        Segment s = null;

        //
        boolean defaultDotted = getDotted();
        while (s_it.hasNext()) {
            s = s_it.next();
            if (!s_it.hasNext() && isWorkedConstruct)
                setDotted(true);

            sToOrigin.set(s);
            poly.convertToStandard(sToOrigin.a);
            poly.convertToStandard(sToOrigin.b);
            visit(sToOrigin);
        }
        setDotted(defaultDotted);
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
        for (InterfaceMapper im : rectangle.getMappedComposition())
            im.accept(this);
        
        visit_0(rectangle);

    }

    public void visit_0(Rectangle rectangle) {
        //gc.setFill(Color.BLUE);
        gc.setStroke(Color.BLUE);
        //
        final Point oppositeSide = rectangle.getOppositeSide();
        gc.strokeRect( //
                Math.min(rectangle.origin.x, oppositeSide.x), // MinHorizontal
                Math.min(rectangle.origin.y, oppositeSide.y), // MinVertical
                Math.abs(oppositeSide.x - rectangle.origin.x), // SizeHorizontal
                Math.abs(oppositeSide.y - rectangle.origin.y) // SizeVertical
        );

        // Border point
        final Point border = new Point();
        border.set(rectangle.origin);
        visit(border);
        border.set(rectangle.origin.x + rectangle.size.x, rectangle.origin.y);
        visit(border);
        border.set(rectangle.origin.x + rectangle.size.x, rectangle.origin.y + rectangle.size.y);
        visit(border);
        border.set(rectangle.origin.x, rectangle.origin.y + rectangle.size.y);
        visit(border);

    }

}