package at.u4a.geometric_algorithms.algorithm;

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
    }

    /* ************** */

    // private final Polygon triangulation = new

    private int mutablePreviousPolyHash = 0;

    @Override
    public void accept(Vector<AbstractLayer> v, InterfaceGraphicVisitor visitor) {

        int currentPolyHash = poly.hashCode();
        if (currentPolyHash != mutablePreviousPolyHash) {

            //
            System.out.println("gen triangulation...");
            buildFusion();
            buildTriangulation();

            //
            mutablePreviousPolyHash = currentPolyHash;
        }

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
    private void searchUpDonwSide() {

        topPointIndex = -1;
        bottomPointIndex = -1;
        type = Polygon.Type.Simple;

        final int size = poly.perimeter.size();

        if (size < 3)
            return;

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
                            return;
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

        type = Polygon.Type.Monotone;
    }

    /**
     * Algorithme qui fusionne les cotés des points
     * 
     * @param clockwise
     *            : sens horaire, donc coté droit
     */
    Vector<Point> makeSide(boolean clockwise) {

        final Vector<Point> points = poly.perimeter;
        final int size = poly.perimeter.size();

        Vector<Point> side = new Vector<Point>();

        int start = Math.floorMod(topPointIndex + (clockwise ? 0 : -1), size);
        int end = Math.floorMod(bottomPointIndex + (clockwise ? 0 : -1), size);
        int add = clockwise ? 1 : -1;

        for (int i = start; i != end; i = Math.floorMod(i + add, size)) {
            side.addElement(points.get(i));
        }

        return side;
    }

    /**
     * Algorithme qui fusionne les cotés des points
     */
    void buildFusion() {
        searchUpDonwSide();
        if (type != Polygon.Type.Monotone)
            return;
        //
        fusionPoints.clear();
        
        Vector<Point> sideRight = makeSide(true);
        Vector<Point> sideLeft = makeSide(false);
        
        System.out.println( "" );
        System.out.println( "topPointIndex("+topPointIndex+") bottomPointIndex("+bottomPointIndex+")");
        System.out.println( "sideRight("+sideRight.size()+") : " + sideRight );
        System.out.println( "sideLeft("+sideLeft.size()+") : " + sideLeft );
        
        //
        int idRight = 0, idLeft = 0;
        int sideRightSize = sideRight.size(), sideLeftSize = sideLeft.size();
        //
        Point leftPoint, rightPoint;
        //
        while (true) {
            if ((idRight < sideRightSize) && (idLeft < sideLeftSize)) {
                leftPoint = sideLeft.get(idLeft);
                rightPoint = sideRight.get(idRight);
                if (leftPoint.y <= rightPoint.y) {
                    fusionPoints.add(new PointTipped(leftPoint, Tip.LEFT));
                    idLeft++;
                } else {
                    fusionPoints.add(new PointTipped(rightPoint, Tip.RIGHT));
                    idRight++;
                }
            } else if (idRight < sideRightSize) {
                fusionPoints.add(new PointTipped(sideRight.get(idRight), Tip.RIGHT));
                idRight++;
            } else if (idLeft < sideLeftSize) {
                fusionPoints.add(new PointTipped(sideLeft.get(idLeft), Tip.LEFT));
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
                        pile.remove(pLastLast);
                        havePop = true;
                    }
                }
            } while (havePop);
            pile.add(current);
        }
    }

};
