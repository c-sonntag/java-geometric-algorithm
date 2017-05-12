package at.u4a.geometric_algorithms.algorithm;

import java.util.ArrayDeque;
import java.util.ArrayList;
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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

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

        GraphicsContext gc = visitor.getGraphicsContext();
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Verdana", 12));

        //
        Vector<Point> aleradyWrite = new Vector<Point>();

        final Segment sToOrigin = new Segment();
        for (Segment tf : triangulationFusion) {
            sToOrigin.set(tf);
            poly.convertToStandard(sToOrigin.a);
            poly.convertToStandard(sToOrigin.b);
            visitor.visit_unit(sToOrigin);

            if (!aleradyWrite.contains(tf.a)) {
                gc.strokeText(tf.a.toString(), sToOrigin.a.x, sToOrigin.a.y);
                aleradyWrite.add(tf.a);
            }

            if (!aleradyWrite.contains(tf.b)) {
                gc.strokeText(tf.b.toString(), sToOrigin.b.x, sToOrigin.b.y);
                aleradyWrite.add(tf.b);
            }

        }

    }

    /* ************** */

    private int mutablePreviousPolyHash = 0;

    protected void triangulation() {

        int currentPolyHash = poly.hashCode();
        if (currentPolyHash != mutablePreviousPolyHash) {

            //
            type = Polygon.Type.Simple;
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
            None("?", 0b000), Left("L", 0b001), Right("R", 0b010), Up("U", 0b111), Down("D", 0b011);

            public final String str;
            public final int code;

            private Tip(String value, int code) {
                this.str = value;
                this.code = code;
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
            return tip.toString() + "(" + super.toString() + ")";
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
     */
    SideTuple makeSide(boolean clockwise, boolean withUpDown) {

        final Vector<Point> points = poly.perimeter;
        final int size = poly.perimeter.size();

        SideTuple st = new SideTuple();

        int start = Math.floorMod(topPointIndex + (withUpDown ? (clockwise ? 0 : -1) : (clockwise ? 1 : -1)), size);
        int end = Math.floorMod(bottomPointIndex + (withUpDown ? (clockwise ? 0 : -1) : (clockwise ? 0 : 0)), size);
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

        SideTuple sideClockwise = makeSide(true, false);
        SideTuple sideUnclockwise = makeSide(false, false);

        boolean normalSens = sideUnclockwise.minX < sideClockwise.maxX;

        Vector<Point> sideRight = normalSens ? sideClockwise.side : sideUnclockwise.side;
        Vector<Point> sideLeft = normalSens ? sideUnclockwise.side : sideClockwise.side;

        System.out.println("");
        System.out.println(normalSens ? "Clockwise" : "Unclockwise");
        System.out.println("topPointIndex(" + topPointIndex + ") bottomPointIndex(" + bottomPointIndex + ")");
        System.out.println("sideRight(" + sideRight.size() + ") : " + sideRight);
        System.out.println("sideLeft(" + sideLeft.size() + ") : " + sideLeft);

        // Add Top
        fusionPoints.add(new PointTipped(topPoint, Tip.Up));

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

        // Add Bottom
        fusionPoints.add(new PointTipped(bottomPoint, Tip.Down));

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

    static boolean inP(Point pA, PointTipped pOrigine, Point pB) {
        double produit = resumeProduitVectorielZ(pA, pOrigine, pB);
        System.out.print("inP(" + pA + " * " + pOrigine + " * " + pB + ")=" + produit + " \t");
        return (((produit > 0) && (pOrigine.tip == Tip.Right)) || ((produit < 0) && (pOrigine.tip == Tip.Left)) || (pOrigine.tip == Tip.Up) || (pOrigine.tip == Tip.Down));
    }

    /**
     * Algorithme qui Créer les segments de la triangulation
     * 
     * @see Computational Geometry - Algorithms and Applications -
     *      TRIANGULATEMONOTONEPOLYGON
     */
    private boolean buildTriangulation() {
        if (!buildFusion())
            return false;

        //
        triangulationFusion.clear();

        //
        ArrayDeque<PointTipped> stack = new ArrayDeque<PointTipped>();
        int fusionPointsSize = fusionPoints.size();

        //
        stack.push(fusionPoints.get(0));
        stack.push(fusionPoints.get(1));

        //
        for (int i = 2; i < fusionPointsSize - 1; i++) {

            PointTipped current = fusionPoints.get(i);
            PointTipped stackFirst = stack.getFirst();

            if ((current.tip.code & stackFirst.tip.code) != 0) { // same chain

                //
                PointTipped firstStackPop = stack.pop();

                //
                
                PointTipped stackPoppedBack = null;
                while (!stack.isEmpty()) {
                    stackPoppedBack = stack.pop();
                    //if (inP(stackPoppedBack, current, firstStackPop) || inP(firstStackPop, current, stackPoppedBack) ) {
                    if (inP(firstStackPop, current, stackPoppedBack) ) {
                        System.out.print("s(" + current + " * " + stackPoppedBack + ") \n");
                        triangulationFusion.add(new Segment(current, stackPoppedBack));
                    } else
                        break;
                }
                

                //
                if (stackPoppedBack != null)
                    stack.push(stackPoppedBack);
                // stack.push(firstStackPop);
                stack.push(current);

            } else {

                //
                System.out.print("ADD:");
                Iterator<PointTipped> stack_it = stack.iterator();
                while (stack_it.hasNext()) {
                    PointTipped stackPoint = stack_it.next();
                    if (stack_it.hasNext()) {
                        System.out.print("s(" + current + " * " + stackPoint + ") \t");
                        triangulationFusion.add(new Segment(current, stackPoint));
                    }
                }
                stack.clear();
                System.out.print(" |\n  ");

                //
                stack.push(fusionPoints.get(i - 1));
                stack.push(current);

            }
        }

        return true;
    }

    /**
     * Algorithme qui Créer les segments de la triangulation
     */
    private boolean buildTriangulation_old() {
        triangulationFusion.clear();
        //
        Vector<PointTipped> pile = new Vector<PointTipped>();
        boolean havePop;
        int fusionPointsSize = fusionPoints.size();
        //
        for (int idCurrent = 0; idCurrent < fusionPointsSize; idCurrent++) {
            PointTipped current = fusionPoints.get(idCurrent);
            do {
                havePop = false;
                if (pile.size() >= 2) {

                    PointTipped pLast = pile.get(pile.size() - 1), pLastLast = pile.get(pile.size() - 2);

                    double produit = resumeProduitVectorielZ(current, pLast, pLastLast);

                    System.out.print("PV(" + current + " * " + pLast + " * " + pLastLast + ")=" + produit + "\t ");

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
                        pile.remove(pLast);
                        havePop = true;
                    } else if ((current.tip.code & pLast.tip.code) == 0) {
                        if ((current.tip.code & pLastLast.tip.code) == 0) {
                            triangulationFusion.add(new Segment(current, pLastLast));
                            pile.remove(pLast);
                        } else {
                            triangulationFusion.add(new Segment(current, pLast));
                            pile.remove(pLastLast);
                        }
                        havePop = true;
                    } else if (current.tip == Tip.UpDown) {
                        triangulationFusion.add(new Segment(current, pLastLast));
                        pile.remove(pLast);
                        havePop = true;
                    }

                    if (havePop)
                        System.out.print(" POP(" + triangulationFusion.lastElement() + ")\t ");

                    if (((idCurrent % 2) == 0) && (idCurrent != 0)) {
                        System.out.println();
                    }

                }
            } while (havePop);

            pile.add(current);

        }
        System.out.println();
        return true;
    }

};
