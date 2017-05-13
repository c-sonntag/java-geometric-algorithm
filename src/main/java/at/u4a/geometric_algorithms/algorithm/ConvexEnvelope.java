package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;
import at.u4a.geometric_algorithms.geometric.*;
import at.u4a.geometric_algorithms.geometric.Polygon.*;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.AlgorithmLayer;
import at.u4a.geometric_algorithms.utils.Calc;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Triangulation
 * 
 * @author Hip
 *
 */
public class ConvexEnvelope extends AbstractAlgorithm {

    public static class Builder implements InterfaceAlgorithmBuilder {

        @Override
        public String getName() {
            return ConvexEnvelope.class.getSimpleName();
        }

        @Override
        public boolean canApply(AbstractLayer l) {
            AbstractShape as = l.getShape();
            return (as instanceof CloudOfPoints) || (as instanceof Polygon) || (as instanceof CloudOfSegments);
        }

        static int ConvexEnvelopeCount = 1;

        @Override
        public AbstractLayer builder(AbstractLayer l) {

            //
            AbstractShape as = l.getShape();
            AbstractList<Point> points = null;

            //
            if (as instanceof CloudOfPoints) {
                points = ((CloudOfPoints) as).cloud;
            } else if (as instanceof Polygon) {
                points = ((Polygon) as).perimeter;
            } else if (as instanceof CloudOfSegments) {
                points = ((CloudOfSegments) as).getPoints();
            } else {
                throw new RuntimeException("ConvexEnvelope need some points (CloudOfPoints, Polygon, CloudOfSegments ...) !");
            }

            //
            AbstractLayer al = new AlgorithmLayer<ConvexEnvelope>(new ConvexEnvelope(points, as), Algorithm.ConvexEnvelope, l);
            al.setLayerName("ce" + String.valueOf(ConvexEnvelopeCount));
            ConvexEnvelopeCount++;
            return al;
        }

    };

    private final AbstractList<Point> points;
    private final AbstractShape as;

    public ConvexEnvelope(AbstractList<Point> points, AbstractShape as) {
        this.points = points;
        this.as = as;
        this.convexPoly = new ConvexPolygon(as.origin);
    }

    /* ************** */

    InterfaceGraphicVisitor mutableVisitor = null;

    @Override
    public void accept(Vector<AbstractLayer> v, InterfaceGraphicVisitor visitor) {
        mutableVisitor = visitor;
        makeConvexEnveloppe();
        mutableVisitor.getGraphicsContext().save();
        mutableVisitor.getGraphicsContext().setLineWidth(2);
        visitor.visit(convexPoly);
        mutableVisitor.getGraphicsContext().restore();

    }

    /* ************** */

    private int mutablePreviousShapeHash = 0;

    protected void makeConvexEnveloppe() {

        int currentShapeHash = as.hashCode();
        if (currentShapeHash != mutablePreviousShapeHash || true) {

            //
            buildConvexPolygon();
            //
            mutablePreviousShapeHash = currentShapeHash;
        }

    }

    /* ************** */

    private final ConvexPolygon convexPoly;

    public Polygon getPolygon() {
        return convexPoly;
    }

    /* ************** */

    private boolean buildConvexPolygon() {

        //
        convexPoly.clear();

        //

        for (int i = 0; i < 50; ++i)
            System.out.println();

        AbstractList<Point> convexPoints = devideToRing(points);
        if (convexPoints == null) {
            System.out.println("ConvexEnvelope not compute"); /** @todo */
            return false;

        }

        //
        convexPoly.swapPerimeter(convexPoints);

        //
        return true;
    }

    /* ************** */

    private static class SideSearch {

        Point top = null, bottom = null;
        Point left = null, right = null;

        final Point center = new Point();

        void initFirst(Point first) {
            this.top = first;
            this.bottom = first;
            this.left = first;
            this.right = first;
        }

        SideSearch(AbstractList<Point> points) {
            Iterator<Point> p_it = points.iterator();
            if (p_it.hasNext()) {
                initFirst(p_it.next());
                while (p_it.hasNext())
                    updateSide(p_it.next());
            }
        }

        private void updateSide(Point p) {
            if (p.y < top.y)
                top = p;
            else if (p.y > bottom.y)
                bottom = p;
            if (p.x < left.x)
                left = p;
            else if (p.x > right.x)
                right = p;
        }

        void updateCenter() {
            center.x = ((right.x - left.x) / 2) + left.x;
            // center.y = ((bottom.y - top.y) / 2) + top.y;
        }

    };

    private static class SideSearchHorizontalId {

        Point top = null, bottom = null;

        public SideSearchHorizontalId(AbstractList<Point> points) {
            Iterator<Point> points_it = points.iterator();
            if (points_it.hasNext()) {
                initFirst(points_it.next());
                while (points_it.hasNext())
                    updateSide(points_it.next());
            }
        }

        private void initFirst(Point first) {
            this.top = first;
            this.bottom = first;
        }

        private void updateSide(Point p) {
            if (p.y < top.y)
                top = p;
            else if (p.y > bottom.y)
                bottom = p;
        }

    };

    private static class SideTuple {

        final SideSearch sideSearch;

        final AbstractList<Point> pointsLeft = new Vector<Point>();
        final AbstractList<Point> pointsRight = new Vector<Point>();

        SideTuple(AbstractList<Point> points) {
            sideSearch = new SideSearch(points);
            fillSide(points);
        }

        private void fillSide(AbstractList<Point> points) {
            sideSearch.updateCenter();
            Point center = sideSearch.center;
            for (Point p : points) {
                if (p.x <= center.x)
                    pointsLeft.add(p);
                else
                    pointsRight.add(p);
            }
        }

    };

    /** @see https://math.stackexchange.com/questions/274712/calculate-on-which-side-of-a-straight-line-is-a-given-point-located */
    private double crossCompareSide_old(Point current, Point linePointLeft, Point linePointRight) {
        double v = (current.x - linePointLeft.x) * (linePointRight.y - linePointLeft.y) - (current.y - linePointLeft.y) * (linePointRight.x - linePointLeft.x);
        drawLine(new Line(linePointLeft, linePointRight));
        System.out.println("current(" + current + ") * linePointLeft(" + linePointLeft + ") * linePointRight(" + linePointRight + ") = " + v);
        return v;
    }

    /** @see https://math.stackexchange.com/questions/274712/calculate-on-which-side-of-a-straight-line-is-a-given-point-located */
    private double crossCompareSide_old2(Point P, Point linePointLeft, Point linePointRight) {
        boolean sens = linePointLeft.x > linePointRight.x;
        Point A = sens ? linePointLeft : linePointRight;
        Point B = sens ? linePointRight : linePointLeft;
        double v = (B.x - A.x) * (P.y - A.y) - (P.x - A.x) * (B.y - A.y);
        drawLine(new Line(A, B));
        System.out.println("P(" + P + "), A(" + A + "), B(" + B + ") : AB x AP \t= " + v);
        return v;
    }

    // private double angleCheck(Point P, Point A, Point B) {
    private double angleCheck_old(Point P, Point linePointLeft, Point linePointRight) {

        double v = Calc.resumeProduitVectorielZ(linePointLeft, P, linePointRight);

        // double v = (B.x - A.x) * (B.y - A.y) - (P.x - A.x) * (P.y - A.y);
        drawLine(new Line(linePointLeft, linePointRight), Color.YELLOW);
        drawLine(new Line(P, linePointLeft), Color.BROWN);
        drawLine(new Line(P, linePointRight), Color.CHOCOLATE);

        System.out.println("P(" + P + ") * linePointLeft(" + linePointLeft + ") * linePointRight(" + linePointRight + ") = " + v);
        return v;
    }

    private double angleCheck(Point P, Point linePointLeft, Point linePointRight) {

        // boolean sens = linePointLeft.x <= linePointRight.x;
        boolean sens = true;
        Point A = sens ? linePointLeft : linePointRight;
        Point B = sens ? linePointRight : linePointLeft;

        double v = Calc.resumeProduitVectorielZ(A, P, B);

        // double v = (B.x - A.x) * (B.y - A.y) - (P.x - A.x) * (P.y - A.y);
        drawLine(new Line(A, B), Color.YELLOW);
        drawLine(new Line(P, A), Color.BROWN);
        drawLine(new Line(P, B), Color.CHOCOLATE);

        System.out.print("P(" + P + ") * A(" + A + ") * B(" + B + ") = " + v + "   ");
        return v;
    }

    private Vector<Point> envelopeUnion(AbstractList<Point> polyLeft, AbstractList<Point> polyRight) {

        //
        if ((polyLeft == null) || (polyRight == null))
            return null;

        //
        final SideSearchHorizontalId sideSearchHozIdLeft = new SideSearchHorizontalId(polyLeft);
        final SideSearchHorizontalId sideSearchHozIdRight = new SideSearchHorizontalId(polyRight);

        //
        System.out.println();
        drawTip(sideSearchHozIdLeft.top);
        drawTip(sideSearchHozIdLeft.bottom);
        drawTip(sideSearchHozIdRight.top);
        drawTip(sideSearchHozIdRight.bottom);

        //
        int uTop = polyLeft.indexOf(sideSearchHozIdLeft.top);
        int vTop = polyRight.indexOf(sideSearchHozIdRight.top);
        int uBottom = polyLeft.indexOf(sideSearchHozIdLeft.bottom);
        int vBottom = polyRight.indexOf(sideSearchHozIdRight.bottom);

        if ((uTop < 0) || (vTop < 0) || (uBottom < 0) || (vBottom < 0)) {
            throw new RuntimeException("Internal error from script");
            // return null;
        }

        //
        final int polyLeftSize = polyLeft.size(), polyRightSize = polyRight.size();

        //
        Point uTopPoint = null, vTopPoint = null;
        Point uTopPointSuiv = null, vTopPointPrec = null;
        boolean uTopChange = true, vTopChange = true;

        //
        while (true) {

            //
            if (uTopChange) {
                uTopPoint = polyLeft.get(uTop);
                uTopPointSuiv = polyLeft.get(Math.floorMod(uTop + 1, polyLeftSize));
                uTopChange = false;
            }
            if (vTopChange) {
                vTopPoint = polyRight.get(vTop);
                vTopPointPrec = polyRight.get(Math.floorMod(vTop - 1, polyRightSize));
                vTopChange = false;
            }

            //
            final boolean uTop_IsOnTop_OfThePolyRight_TopLine = angleCheck(uTopPoint, vTopPointPrec, vTopPoint) < 0;
            final boolean vTop_IsOnTop_OfThePolyLeft_TopLine = angleCheck(vTopPoint, uTopPoint, uTopPointSuiv) < 0;

            //
            if (uTop_IsOnTop_OfThePolyRight_TopLine) {
                vTop = Math.floorMod(vTop - 1, polyRightSize);
                vTopChange = true;
                System.out.println("uTop_IsOnTop_OfThePolyRight_TopLine");
            } else if (vTop_IsOnTop_OfThePolyLeft_TopLine) {
                uTop = Math.floorMod(uTop + 1, polyLeftSize);
                uTopChange = true;
                System.out.println("vTop_IsOnTop_OfThePolyLeft_TopLine");
            } else
                break;

        }

        //
        Point uBottomPoint = null, vBottomPoint = null;
        Point uBottomPointPrec = null, vBottomPointSuiv = null;
        boolean uBottomChange = true, vBottomChange = true;

        Point uBottomPointSuiv = null;

        //
        while (true) {

            //
            if (uBottomChange) {
                uBottomPoint = polyLeft.get(uBottom);
                uBottomPointPrec = polyLeft.get(Math.floorMod(uBottom - 1, polyLeftSize));
                // uBottomPointSuiv = polyLeft.get(Math.floorMod(uBottom + 1,
                // polyLeftSize));
                uBottomChange = false;
            }
            if (vBottomChange) {
                vBottomPoint = polyRight.get(vBottom);
                vBottomPointSuiv = polyRight.get(Math.floorMod(vBottom + 1, polyRightSize));
                vBottomChange = false;
            }

            //
            final boolean uBottom_IsOnBottom_OfThePolyRight_BottomLine = angleCheck(uBottomPoint, vBottomPoint, vBottomPointSuiv) > 0;
            final boolean vBottom_IsOnBottom_OfThePolyLeft_BottomLine = angleCheck(vBottomPoint, uBottomPoint, uBottomPointPrec) > 0;

            //
            if (uBottom_IsOnBottom_OfThePolyRight_BottomLine) {
                vBottom = Math.floorMod(vBottom + 1, polyRightSize);
                vBottomChange = true;
                System.out.println("uBottom_IsOnBottom_OfThePolyRight_BottomLine");
            } else if (vBottom_IsOnBottom_OfThePolyLeft_BottomLine) {
                // uBottom = Math.floorMod(uBottom - 1, polyLeftSize);
                uBottom = Math.floorMod(uBottom + 1, polyLeftSize);
                uBottomChange = true;
                System.out.println("vBottom_IsOnBottom_OfThePolyLeft_BottomLine");
            } else
                break;

        }

        Vector<Point> convexPoints = new Vector<Point>();

        /*
         * Vector<Point> convexPoints = new Vector<Point>(); for (Point p :
         * polyLeft) convexPoints.add(p); for (Point p : polyRight)
         * convexPoints.add(p); if (!convexPoints.isEmpty()) return
         * convexPoints;
         */

        //
        convexPoints.add(polyLeft.get(uBottom));

        final int uStart = Math.floorMod(uBottom + 1, polyLeftSize);
        final int uEnd = Math.floorMod(uTop + 1, polyLeftSize);

        for (int i = uStart; i != uEnd; i = Math.floorMod(i + 1, polyLeftSize))
            convexPoints.add(polyLeft.get(i));

        //
        convexPoints.add(polyRight.get(vTop));

        final int vStart = Math.floorMod(vTop + 1, polyRightSize);
        final int vEnd = Math.floorMod(vBottom + 1, polyRightSize);

        for (int i = vStart; i != vEnd; i = Math.floorMod(i + 1, polyRightSize))
            convexPoints.add(polyRight.get(i));

        //
        return convexPoints;
    }

    /** @see http://stackoverflow.com/a/4199571 */
    public class PointCompare implements Comparator<Point> {
        public int compare(Point a, Point b) {
            if (a.x < b.x) {
                return -1;
            } else if (a.x > b.x) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private PointCompare pointComparator = new PointCompare();

    private AbstractList<Point> devideToRing(AbstractList<Point> poly) {

        //
        int polySize = poly.size();
        if (polySize <= 3) {
            if (polySize >= 2) {
                Point p0 = poly.get(0);
                Point p1 = poly.get(1);
                if (polySize == 3) {
                    Point p2 = poly.get(2);
                    if(Calc.resumeProduitVectorielZ(p0,p1,p2) > 0) { // set Clock wise
                        poly.clear();
                        poly.add(p2);
                        poly.add(p1);
                        poly.add(p0);
                    }
                } else {
                    if (p0.y > p1.y) {
                        poly.clear();
                        poly.add(p0);
                        poly.add(p1);
                    }
                }
            }
            return poly;
        }

        //
        SideTuple sides = new SideTuple(poly);

        //
        AbstractList<Point> polyLeft = devideToRing(sides.pointsLeft);
        AbstractList<Point> polyRight = devideToRing(sides.pointsRight);

        //
        visitPolygon(polyLeft);
        visitPolygon(polyRight);

        //
        return envelopeUnion(polyLeft, polyRight);
    }

    public void visitPolygon(AbstractList<Point> p) {
        if (p == null || mutableVisitor == null)
            return;
        mutableVisitor.getGraphicsContext().save();
        mutableVisitor.getGraphicsContext().setStroke(Color.RED);
        mutableVisitor.visit(new MonotonePolygon(as.origin, p));
        mutableVisitor.getGraphicsContext().restore();
    }

    public void visitPolygon(Polygon poly) {
        if (poly == null || mutableVisitor == null)
            return;
        mutableVisitor.visit(poly);
    }

    public void drawTip(Point p) {
        if (mutableVisitor == null)
            return;
        final Point pToOrigin = new Point();
        pToOrigin.set(p);
        as.convertToStandard(pToOrigin);
        mutableVisitor.drawTip(p.toString(), pToOrigin);
    }

    public void drawLine(Line s, Color color) {
        if (mutableVisitor == null)
            return;
        mutableVisitor.getGraphicsContext().save();
        mutableVisitor.getGraphicsContext().setStroke(Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.3));
        mutableVisitor.getGraphicsContext().setLineWidth(2);
        final Line lToOrigin = new Line();
        lToOrigin.set(s);
        as.convertToStandard(lToOrigin.a);
        as.convertToStandard(lToOrigin.b);
        // mutableVisitor.visit_unit(lToOrigin);
        mutableVisitor.getGraphicsContext().restore();
    }
};
