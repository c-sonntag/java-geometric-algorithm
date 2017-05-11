package at.u4a.geometric_algorithms.algorithm;

import java.util.List;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.AlgorithmBuilderInterface;
import at.u4a.geometric_algorithms.algorithm.Triangulation.PointTipped.Tip;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.geometric.Rectangle;
import at.u4a.geometric_algorithms.geometric.Segment;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.AlgorithmLayer;

/**
 * TriangulationOfDelaunay
 * 
 * @author Hip
 *
 */
class Triangulation extends AbstractAlgorithm {

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
            AbstractLayer al = new AlgorithmLayer<Triangulation>(new Triangulation((Polygon) l.getShape()), Algorithm.Triangulation, l);
            al.setLayerName("r" + String.valueOf(TriangulationCount));
            TriangulationCount++;
            return al;
        }

    };

    private final Polygon poly;

    public Triangulation(Polygon poly) {
        this.poly = poly;
        pointsTip = new Vector<PointTipped>(poly.perimeter.size());
        fusionPoints = new Vector<PointTipped>();
        triangulationFusion = new Vector<Segment>();
        //
        type = Polygon.Type.Simple;
    }

    /* ************** */

    // private final Polygon triangulation = new

    @Override
    public void accept(Vector<AbstractLayer> v, InterfaceGraphicVisitor visitor) {

        //
        buildFusion();
        buildTriangulation();

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

    /**
     * Index du point le plus haut et le plus bas dans la listes des points
     */
    int topPointIndex, bottomPointIndex;

    /**
     * La liste des points ajouté.
     */
    private Vector<PointTipped> pointsTip;

    /**
     * La liste des points affiché.
     */
    private Vector<PointTipped> fusionPoints;

    /**
     * Le tableau des triangulation affiché en fonction de fusion.
     */
    public Vector<Segment> triangulationFusion;

    /**
     * indique si le poligone est monotone
     */
    public Polygon.Type type;

    /* ************** */

    static class PointTipped extends Point {

        static enum Tip {
            NONE("?"), LEFT("L"), RIGHT("R"), TOP("T"), BOTTOM("B");

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
            tip = Tip.NONE;
        }

        /**
         * Position dans la le polygone monotone
         */
        public Tip tip;

    };

    /**
     * Algorithme qui recherche le coté des points
     *
     * @todo l'ordre des points doit être horaire pour le moments, integrer un
     *       système de reconnaissance pour plus tard
     */
    void searchTipSimple() {
        type = Polygon.Type.Simple;
        if (poly.perimeter.size() < 3)
            return;
        //
        pointsTip.clear();
        Tip actualTip = Tip.RIGHT;
        //
        // Point firstPoint = poly.perimeter.firstElement();
        PointTipped prevPoint = null;

        //
        for (Point currentPoint : poly.perimeter) {

            PointTipped currentPointTip = new PointTipped(currentPoint);
            pointsTip.add(currentPointTip);

            if (prevPoint != null) {
                if (prevPoint.y >= currentPoint.y) {
                    if (actualTip == Tip.RIGHT)
                        actualTip = Tip.LEFT;
                } else {
                    if (actualTip == Tip.LEFT)
                        return;
                }
            }

            currentPointTip.tip = actualTip;
            prevPoint = currentPointTip;
        }
        type = Polygon.Type.Monotone;
    }

    /**
     * Algorithme qui fusionne les cotés des points
     */
    Vector<PointTipped> makeSide(Tip choice, boolean Unordered) {
        Vector<PointTipped> side = new Vector<PointTipped>();
        //
        int i = Unordered ? pointsTip.size() - 1 : 0;
        int end = Unordered ? -1 : pointsTip.size();
        //
        while (i != end) {
            PointTipped pt = pointsTip.get(i);
            if (pt.tip == choice)
                side.add(pt);
            //
            if (Unordered)
                i--;
            else
                i++;
        }
        return side;
    }

    /**
     * Algorithme qui fusionne les cotés des points
     */
    void buildFusion() {
        searchTipSimple();
        if (type != Polygon.Type.Monotone)
            return;
        //
        fusionPoints.clear();
        Vector<PointTipped> sideRight = makeSide(Tip.RIGHT, false);
        Vector<PointTipped> sideLeft = makeSide(Tip.LEFT, true);
        //
        int idRight = 0, idLeft = 0;
        int sideRightSize = sideRight.size(), sideLeftSize = sideLeft.size();
        //
        PointTipped leftPoint, rightPoint;
        //
        while (true) {
            if ((idRight < sideRightSize) && (idLeft < sideLeftSize)) {
                leftPoint = sideLeft.get(idLeft);
                rightPoint = sideRight.get(idRight);
                if (leftPoint.y <= rightPoint.y) {
                    fusionPoints.add(leftPoint);
                    idLeft++;
                } else {
                    fusionPoints.add(rightPoint);
                    idRight++;
                }
            } else if (idRight < sideRightSize) {
                fusionPoints.add(sideRight.get(idRight));
                idRight++;
            } else if (idLeft < sideLeftSize) {
                fusionPoints.add(sideLeft.get(idLeft));
                idLeft++;
            } else
                break;
        }
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
    void buildTriangulation() {
        triangulationFusion.clear();
        if (type != Polygon.Type.Monotone)
            return;
        //
        Vector<PointTipped> pile = new Vector<PointTipped>();
        boolean havePop;
        //
        for (int idCurrent = 0; idCurrent < fusionPoints.size() - 1; idCurrent++) {
            PointTipped current = fusionPoints.get(idCurrent);
            do {
                havePop = false;
                if (pile.size() >= 2) {
                    PointTipped pLast = pile.get(pile.size() - 1), pLastLast = pile.get(pile.size() - 2);
                    double produit = resumeProduitVectorielZ(current, pLast, pLastLast);
                    //
                    if (((produit > 0) && (current.tip == Tip.RIGHT)) || ((produit < 0) && (current.tip == Tip.LEFT))) {
                        triangulationFusion.add(new Segment(current, pLastLast));
                        pile.remove(pLast);
                        havePop = true;
                    } else if (current.tip != pLast.tip) {
                        triangulationFusion.add(new Segment(current, pLast));
                        pile.remove(pLastLast);;
                        havePop = true;
                    }
                }
            } while (havePop);
            pile.add(current);
        }
    }

};
