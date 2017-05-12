package at.u4a.geometric_algorithms.algorithm;

import java.util.Iterator;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.AlgorithmBuilderInterface;
import at.u4a.geometric_algorithms.algorithm.Triangulation.PointTipped.Tip;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.geometric.Segment;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.AlgorithmLayer;

/**
 * TriangulationOfDelaunay
 * 
 * @author Hip
 *
 */
public class Triangulation extends AbstractAlgorithm {

    public static class Builder implements AlgorithmBuilderInterface {

        @Override
        public String getName() {
            return Triangulation.class.getSimpleName();
        }

        @Override
        public boolean canApply(AbstractLayer l) {
            return (l.getShape() instanceof Polygon);
        }

        static int TriangulationCount = 1;

        @Override
        public AbstractLayer builder(AbstractLayer l) {
            if (!canApply(l))
                throw new RuntimeException("Triangulation need a Polygon !");
            //
            AbstractLayer al = new AlgorithmLayer<Triangulation>(new Triangulation((Polygon) l.getShape()), Algorithm.Triangulation, l);
            al.setLayerName("r" + String.valueOf(TriangulationCount));
            TriangulationCount++;
            return al;
        }

    };

    private final Polygon poly;

    public Triangulation(Polygon poly) {
        this.poly = poly;
    }

    /* ************** */

    @Override
    public void accept(Vector<AbstractLayer> v, InterfaceGraphicVisitor visitor) {

        triangulation();

        //
        if (type != Polygon.Type.Monotone)
            return;

        //
        final Segment sToOrigin = new Segment();
        for (Segment tf : triangulationFusion) {
            sToOrigin.set(tf);
            poly.convertToStandard(sToOrigin.a);
            poly.convertToStandard(sToOrigin.b);
            visitor.visit_unit(sToOrigin);
        }

        // Rectangle r =

        // visitor.visit(new Rectangle(new Point(20, 20), new Point(40, 50)));
    }

    /* ************** */

    private int mutablePreviousPolyHash = 0;

    protected void triangulation() {

        int currentPolyHash = poly.hashCode();
        if (currentPolyHash != mutablePreviousPolyHash) {

            //
            type = Polygon.Type.Simple;
            if (buildFusion())
                if (buildTriangulation())
                    type = Polygon.Type.Monotone;
            //
            mutablePreviousPolyHash = currentPolyHash;
        }

    }

    /* ************** */

    /**
     * Index du point le plus haut et le plus bas dans la listes des points
     */
    int topPointIndex, bottomPointIndex;
    Point topPoint, bottomPoint;

    /**
     * La liste des points affiché.
     */
    private Vector<PointTipped> fusionPoints = new Vector<PointTipped>();

    /**
     * Le tableau des triangulation affiché en fonction de fusion.
     */
    public Vector<Segment> triangulationFusion = new Vector<Segment>();

    /**
     * indique si le poligone est monotone
     */
    public Polygon.Type type = Polygon.Type.Simple;

    /* ************** */

    static class PointTipped extends Point {

        static enum Tip {
            None("?"), Left("U"), Right("C");

            private final String str;

            private Tip(String value) {
                this.str = value;
            }

            public String toString() {
                return this.str;
            }
        }

        public PointTipped(Point p) {
            set(p);
            tip = Tip.None;
        }

        public PointTipped(Point p, Tip t) {
            set(p);
            tip = t;
        }

        public String toString() {
            return tip.toString() + super.toString();
        }

        /**
         * Position dans la le polygone monotone
         */
        public Tip tip;

    };

    /**
     * Algorithme qui recherche l'index du point en haut et en bas
     */
    private boolean searchUpDonwSide() {

        topPointIndex = -1;
        bottomPointIndex = -1;
        type = Polygon.Type.Simple;

        final int size = poly.perimeter.size();

        if (size < 3)
            return false;

        double topY = 0, bottomY = 0;
        final Vector<Point> points = poly.perimeter;

        Point precPoint = null, precPrecPoint = null, currentPoint;
        int countAngle = 0;

        for (int i = 0; i < size + 2; i++) {
            currentPoint = points.get(i % size);

            if (precPoint != null) {

                //
                if (currentPoint.y < topY) {
                    topY = currentPoint.y;
                    topPointIndex = i;
                } else if (currentPoint.y > bottomY) {
                    bottomY = currentPoint.y;
                    bottomPointIndex = i;
                }

                //
                if (precPrecPoint != null) {

                    if (((precPrecPoint.y > precPoint.y) && (currentPoint.y > precPoint.y)) || ((precPrecPoint.y < precPoint.y) && (currentPoint.y < precPoint.y))) {
                        countAngle++;

                        if (countAngle > 2)
                            return false;
                    }
                }

            } else {
                topY = currentPoint.y;
                bottomY = currentPoint.y;
                topPointIndex = i;
                bottomPointIndex = i;
            }

            precPrecPoint = precPoint;
            precPoint = currentPoint;
        }

        topPoint = points.get(topPointIndex);
        bottomPoint = points.get(bottomPointIndex);

        return true;
    }

    class SideTuple {
        Vector<Point> side = new Vector<Point>();
        double minX, maxX;
    }

    /**
     * Algorithme qui fusionne les cotés des points
     * 
     * @param clockwise
     *            sens horaire
     */
    SideTuple makeSide(boolean clockwise) {

        final Vector<Point> points = poly.perimeter;
        final int size = poly.perimeter.size();

        SideTuple st = new SideTuple();

        int start = Math.floorMod(topPointIndex + (clockwise ? 0 : -1), size);
        int end = Math.floorMod(bottomPointIndex + (clockwise ? 0 : -1), size);
        int add = clockwise ? 1 : -1;

        // Add first
        Point p = points.get(start);
        st.side.addElement(p);
        st.maxX = st.minX = p.x;
        start = Math.floorMod(start + add, size);

        //
        for (int i = start; i != end; i = Math.floorMod(i + add, size)) {
            p = points.get(i);
            st.side.addElement(p);
            if (p.x > st.maxX)
                st.maxX = p.x;
            else if (p.x < st.minX)
                st.minX = p.x;
        }

        return st;
    }

    /**
     * Algorithme qui fusionne les cotés des points
     */
    private boolean buildFusion() {
        if (!searchUpDonwSide())
            return false;
        //
        fusionPoints.clear();

        SideTuple sideClockwise = makeSide(true);
        SideTuple sideUnclockwise = makeSide(false);

        boolean normalSens = sideUnclockwise.minX < sideUnclockwise.maxX;

        if (normalSens) {
            if ((sideClockwise.maxX <= sideUnclockwise.maxX) || (sideClockwise.minX <= sideUnclockwise.minX))
                return false;
        } else {
            if ((sideUnclockwise.maxX <= sideClockwise.maxX) || (sideUnclockwise.minX <= sideClockwise.minX))
                return false;
        }

        Vector<Point> sideRight = normalSens ? sideClockwise.side : sideUnclockwise.side;
        Vector<Point> sideLeft = normalSens ? sideUnclockwise.side : sideClockwise.side;

        System.out.println("");
        System.out.println("topPointIndex(" + topPointIndex + ") bottomPointIndex(" + bottomPointIndex + ")");
        System.out.println("sideRight(" + sideRight.size() + ") : " + sideRight);
        System.out.println("sideLeft(" + sideLeft.size() + ") : " + sideLeft);

        //
        Point leftPoint = null, rightPoint = null;
        Iterator<Point> leftPoint_it = sideLeft.iterator();
        Iterator<Point> rightPoint_it = sideRight.iterator();
        //
        while (true) {
            if (leftPoint_it.hasNext() && rightPoint_it.hasNext()) {

                if (leftPoint == null)
                    leftPoint = leftPoint_it.next();
                if (rightPoint == null)
                    rightPoint = rightPoint_it.next();

                if (leftPoint.y <= rightPoint.y) {
                    fusionPoints.add(new PointTipped(leftPoint, Tip.Left));
                    leftPoint = null;
                } else {
                    fusionPoints.add(new PointTipped(rightPoint, Tip.Right));
                    rightPoint = null;
                }
            } else if (rightPoint_it.hasNext() || (rightPoint != null)) {
                if (rightPoint == null)
                    rightPoint = rightPoint_it.next();
                fusionPoints.add(new PointTipped(rightPoint, Tip.Right));
                rightPoint = null;
            } else if (leftPoint_it.hasNext() || (leftPoint != null)) {
                if (leftPoint == null)
                    leftPoint = leftPoint_it.next();
                fusionPoints.add(new PointTipped(leftPoint, Tip.Left));
                leftPoint = null;

            } else
                break;
        }

        System.out.println("fusionPoints(" + fusionPoints.size() + ") : " + fusionPoints);
        return true;
    }

    /**
     * Produit vectoriel x = a.y*0 - 0*b.y* = 0 y = 0*b.x - a.x*0 = 0 z =
     * a.x*b.y - a.y*b.x
     */
    static double getProduitVectorielZ(Point pA, Point pB) {
        return pA.x * pB.y - pA.y * pB.x;
    }

    /**
     * Fait le calcul necessaire au decalage de A et B en fonction de O
     */
    static double resumeProduitVectorielZ(Point pA, Point pOrigine, Point pB) {
        Point pAdecal = new Point(pA.x - pOrigine.x, pA.y - pOrigine.y);
        Point pBdecal = new Point(pB.x - pOrigine.x, pB.y - pOrigine.y);
        return getProduitVectorielZ(pAdecal, pBdecal);
    }

    /**
     * Algorithme qui Créer les segments de la triangulation
     */
    private boolean buildTriangulation() {
        triangulationFusion.clear();

        String add = "";

        //
        Vector<PointTipped> pile = new Vector<PointTipped>();
        boolean havePop;
        int fusionPointsSize = fusionPoints.size();
        //
        for (int idCurrent = 0; idCurrent < fusionPointsSize - 1; idCurrent++) {
            PointTipped current = fusionPoints.get(Math.floorMod(idCurrent, fusionPointsSize));
            do {
                havePop = false;
                if (pile.size() >= 2) {

                    PointTipped pLast = pile.get(pile.size() - 1), pLastLast = pile.get(pile.size() - 2);

                    double produit = resumeProduitVectorielZ(current, pLast, pLastLast);

                    System.out.print("PV(" + current + " * " + pLast + " * " + pLastLast + ")=" + produit + "\t ");
                    if (((idCurrent % 4) == 0) && (idCurrent != 0)) {
                        System.out.println();
                    }

                    //
                    // if ((current.tip == pLastLast.tip) && (pLastLast.tip !=
                    // pLast.tip)) {
                    // if (((pLast.tip == Tip.Right) && (produit < 0)) ||
                    // (pLast.tip == Tip.Left) && (produit > 0))
                    // return false;
                    // }

                    //
                    if (((produit > 0) && (current.tip == Tip.Right)) || ((produit < 0) && (current.tip == Tip.Left))) {
                        triangulationFusion.add(new Segment(current, pLastLast));
                        add += "SL" + triangulationFusion.lastElement() + "\t ";
                        pile.remove(pLast);
                        havePop = true;
                    } else if (current.tip != pLast.tip) {
                        triangulationFusion.add(new Segment(current, pLast));
                        add += "SR" + triangulationFusion.lastElement() + "\t ";
                        pile.remove(pLastLast);
                        havePop = true;
                    }
                }
            } while (havePop);
            pile.add(current);
        }
        System.out.println();
        System.out.println(add);
        return true;
    }

};
