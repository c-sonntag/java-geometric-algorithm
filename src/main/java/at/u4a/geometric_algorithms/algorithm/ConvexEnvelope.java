package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;
import at.u4a.geometric_algorithms.geometric.*;
import at.u4a.geometric_algorithms.geometric.Polygon.*;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.AlgorithmLayer;
import javafx.scene.paint.Color;

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
        visitor.visit(convexPoly);
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
        System.out.println(); /** @todo */
        System.out.println(); /** @todo */
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
    private double crossCompareSide(Point current, Point linePointLeft, Point linePointRight) {

        double v = (current.x - linePointLeft.x) * (linePointRight.y - linePointLeft.y) - (current.y - linePointLeft.y) * (linePointRight.x - linePointLeft.x);
        
        drawLine(new Line(linePointLeft,linePointRight));
        
        System.out.println("current(" + current + ") * linePointLeft(" + current + ") * linePointRight(" + current + ") = " + v);
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
            final boolean uTop_IsOnTop_OfThePolyRight_TopLine = crossCompareSide(uTopPoint, vTopPointPrec, vTopPoint) > 0;
            final boolean vTop_IsOnTop_OfThePolyLeft_TopLine = crossCompareSide(vTopPoint, uTopPoint, uTopPointSuiv) > 0;

            //
            if (uTop_IsOnTop_OfThePolyRight_TopLine) {
                vTop = Math.floorMod(vTop - 1, polyRightSize);
                vTopChange = true;
            } else if (vTop_IsOnTop_OfThePolyLeft_TopLine) {
                uTop = Math.floorMod(uTop + 1, polyLeftSize);
                uTopChange = true;
            } else
                break;

        }

        //
        Point uBottomPoint = null, vBottomPoint = null;
        Point uBottomPointPrec = null, vBottomPointSuiv = null;
        boolean uBottomChange = true, vBottomChange = true;

        //
        while (true) {

            //
            if (uBottomChange) {
                uBottomPoint = polyLeft.get(uBottom);
                uBottomPointPrec = polyLeft.get(Math.floorMod(uBottom - 1, polyLeftSize));
                uBottomChange = false;
            }
            if (vBottomChange) {
                vBottomPoint = polyRight.get(vBottom);
                vBottomPointSuiv = polyRight.get(Math.floorMod(vBottom + 1, polyRightSize));
                vBottomChange = false;
            }

            //
            final boolean uBottom_IsOnBottom_OfThePolyRight_BottomLine = crossCompareSide(uBottomPoint, vBottomPoint, vBottomPointSuiv) > 0;
            final boolean vBottom_IsOnBottom_OfThePolyLeft_BottomLine = crossCompareSide(vBottomPoint, uBottomPointPrec, uBottomPoint) > 0;

            //
            if (uBottom_IsOnBottom_OfThePolyRight_BottomLine) {
                vBottom = Math.floorMod(vBottom + 1, polyRightSize);
                vBottomChange = true;
            } else if (vBottom_IsOnBottom_OfThePolyLeft_BottomLine) {
                uBottom = Math.floorMod(uBottom - 1, polyLeftSize);
                uBottomChange = true;
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

        convexPoints.add(polyRight.get(vTop));

        final int vStart = Math.floorMod(vTop + 1, polyRightSize);
        final int vEnd = Math.floorMod(vBottom + 1, polyRightSize);

        for (int i = vStart; i != vEnd; i = Math.floorMod(i + 1, polyRightSize))
            convexPoints.add(polyRight.get(i));

        //
        return convexPoints;
    }

    private AbstractList<Point> devideToRing(AbstractList<Point> poly) {

        //
        if (poly.size() <= 3)
            return poly;

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

    
    public void drawLine(Line s) {
        if (mutableVisitor == null)
            return;
        mutableVisitor.getGraphicsContext().save();
        mutableVisitor.getGraphicsContext().setStroke(Color.CHOCOLATE);
        mutableVisitor.getGraphicsContext().setLineWidth(2);
        final Line lToOrigin = new Line();
        lToOrigin.set(s);
        as.convertToStandard(lToOrigin.a);
        as.convertToStandard(lToOrigin.b);
        mutableVisitor.visit_unit(lToOrigin);
        mutableVisitor.getGraphicsContext().restore();
    }
};
