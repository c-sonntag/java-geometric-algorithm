package at.u4a.geometric_algorithms.graphic_visitor;

import java.util.AbstractList;
import java.util.Iterator;

import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.CloudOfPoints;
import at.u4a.geometric_algorithms.geometric.CloudOfSegments;
import at.u4a.geometric_algorithms.geometric.Line;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.geometric.Rectangle;
import at.u4a.geometric_algorithms.geometric.Segment;
import at.u4a.geometric_algorithms.geometric.mapper.InterfaceMapper;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

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

    protected final Font tipFont = Font.font("Verdana", 12);

    public GraphicPainter(GraphicsContext gc) {
        this.gc = gc;

        //
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.BOTTOM);
        ;
    }

    /* SETTING */

    public void setOverlay(boolean overlay) {
        isOverlay = overlay;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setColor(Color color) {
        // this.color = color;
        gc.setStroke(color);
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

    public GraphicsContext getGraphicsContext() {
        return gc;
    }

    /* PROTECTED */

    protected void setPointPaint() {
        // gc.setFill(isSelected ? selectedColor : pointColor);
        gc.setFill(Color.GRAY);
        gc.setStroke(Color.BLACK);
    }

    protected void drawTip(String t, Point p) {
        
    }

    @Override
    public void drawEdgeTipFromList(AbstractShape as, AbstractList<Point> lp) {
        gc.save();

        gc.setFont(tipFont);
        gc.setStroke(Color.BLACK);
        gc.setFontSmoothingType(FontSmoothingType.GRAY);

        final Point pToOrigin = new Point();
        for (Point p : lp) {
            pToOrigin.set(p);
            as.convertToStandard(pToOrigin);
            drawTip(p.toString(), pToOrigin);
        }

        gc.restore();
    }

    /* GEOMETRIC */

    public void visit(Point p) {
        gc.save();
        setPointPaint();
        gc.fillOval((int) (p.x - 2 * Point.POINT_RAYON), (int) (p.y - 2 * Point.POINT_RAYON), 2 * 2 * Point.POINT_RAYON, 2 * 2 * Point.POINT_RAYON);
        gc.strokeOval((int) (p.x - 2 * Point.POINT_RAYON), (int) (p.y - 2 * Point.POINT_RAYON), 2 * 2 * Point.POINT_RAYON, 2 * 2 * Point.POINT_RAYON);
        gc.restore();
    }

    /** TODO doit dessiner une ligne et non un segment ! */
    public void visit_unit(Line l) {
        Canvas c = gc.getCanvas();

        double screenWidth = c.getWidth();
        double screenHeight = c.getHeight();

        final double sizeX = Math.abs(l.a.x - l.b.x);
        final double sizeY = Math.abs(l.a.y - l.b.y);

        // final double factorX = x / y ;
        // final double factorY = y / x ;

        final boolean isVertical = Math.abs(l.a.x - l.b.x) >= Math.abs(l.a.y - l.b.y);

        final double factor = (isVertical ? (sizeY / sizeX) : (sizeX / sizeY));
        final int add = (int) (isVertical ? Math.min(l.a.x, l.b.x) : Math.min(l.a.y, l.b.y));

        if (isVertical) {
            // gc.strokeLine((int) l.a.x, (int) l.a.y, (int) l.b.x, (int)
            // l.b.y);
        } else {
            // gc.strokeLine(0, (int) (factor *) , (int) l.b.x, (int) l.b.y);
        }

        // double b_x = Math.max(l.a.x , l.b.x ) + sizeX * factorX;
        // double b_x = Math.max(l.a.x , l.b.x ) + sizeX * factorX;

        // final boolean isVertical = Math.abs( l.a.x - l.b.x ) >= Math.abs(
        // l.a.y - l.b.y );
        // final double factorX = Math.min(l.a.x , l.b.x) / Math.max(l.a.x ,
        // l.b.x);
        // final double factorY = Math.min(l.a.y , l.b.y) / Math.max(l.a.y ,
        // l.b.y);

        gc.strokeLine((int) l.a.x, (int) l.a.y, (int) l.b.x, (int) l.b.y);
    }

    public void visit(Line l) {
        visit_unit(l);
        visit(l.a);
        visit(l.b);
    }

    public void visit_unit(Segment s) {
        gc.strokeLine((int) s.a.x, (int) s.a.y, (int) s.b.x, (int) s.b.y);

    }

    public void visit(Segment s) {
        visit_unit(s);
        visit(s.a);
        visit(s.b);
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
            visit_unit(sToOrigin);
        }
        setDotted(defaultDotted);
        final Point pToOrigin = new Point();
        for (Point p : poly.perimeter) {
            pToOrigin.set(p);
            poly.convertToStandard(pToOrigin);
            visit(pToOrigin);
        }
    }

    /** @todo other */
    public void visit_onetracing(Polygon poly) { // visit_onetracing
        Segment segment = new Segment();
        Point point = new Point(), lastPoint = null;
        for (Point p : poly.perimeter) {
            point.set(poly.origin.x + p.x, poly.origin.y + p.y);
            visit(point);
            if (lastPoint != null) {
                segment.a.set(poly.origin.x + lastPoint.x, poly.origin.y + lastPoint.y);
                segment.b.set(point);
                visit_unit(segment);
            }
            lastPoint = p;
        }
    }

    public void visit(CloudOfSegments clouds) {
        final Segment sToOrigin = new Segment();
        for (Segment s : clouds) {
            sToOrigin.set(s);
            clouds.convertToStandard(sToOrigin.a);
            clouds.convertToStandard(sToOrigin.b);
            visit(sToOrigin);
        }
    }

    public void visit(CloudOfPoints clouds) {
        final Point pToOrigin = new Point();
        for (Point p : clouds) {
            pToOrigin.set(p);
            clouds.convertToStandard(pToOrigin);
            visit(pToOrigin);
        }

    }

    /** @todo test */
    public void visit_fromcomposition(Rectangle rectangle) {

        gc.setStroke(Color.RED);
        for (InterfaceMapper im : rectangle.getMappedComposition())
            im.accept(this);
    }

    public void visit(Rectangle rectangle) {
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