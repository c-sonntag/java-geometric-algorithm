package at.u4a.geometric_algorithms.algorithm;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.AlgorithmBuilderInterface;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.geometric.Rectangle;
import at.u4a.geometric_algorithms.geometric.Segment;
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
            AbstractLayer al = new AlgorithmLayer<Triangulation>(new Triangulation(), Algorithm.Triangulation, l);
            al.setLayerName("r" + String.valueOf(TriangulationCount));
            TriangulationCount++;
            return al;
        }

    };

    private final Polygon poly;
    
    public Triangulation(Polygon poly) {
        //points = new Vector<Point>();
        this.poly = poly;
        points = poly.perimeter;
        pointsTip = new Vector<Tip>(points.size());
        //
        fusionPoints = new Vector<Point>();
        triangulationFusion = new Vector<Segment>();
        //
        type = Polygon.Type.Simple;
    }

    /* ************** */

    // private final Polygon triangulation = new

    @Override
    public void accept(Vector<AbstractLayer> v, InterfaceShapePainterVisitor visitor) {
        Polygon p = (Polygon) v.firstElement().getShape();

        // Rectangle r =

        visitor.visit(new Rectangle(new Point(20, 20), new Point(40, 50)));

    }
    
    
    /* ************** */

    /**
     * Index du point le plus haut et le plus bas dans la listes des points
     */
    int topPointIndex, bottomPointIndex;
    
    /**
     * La liste des points ajouté.
     */
    private Vector<Point> points;
    
    /**
     * Position des points ajouté.
     */
    private Vector<Tip> pointsTip;

    /**
     * La liste des points affiché.
     */
    private Vector<Point> fusionPoints;

    /**
     * Le tableau des triangulation affiché en fonction de fusion.
     */
    public Vector<Segment> triangulationFusion;

    /**
     * indique si le poligone est monotone
     */
    public Polygon.Type type;

    
    /* ************** */

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
    
    
    /**
     * Algorithme qui recherche le coté des points
     *
     * @todo l'ordre des points doit être horaire pour le moments, integrer un
     *       système de reconnaissance pour plus tard
     */
    void searchTipSimple() {
        type = Polygon.Type.Simple;
        if (points.size() < 3)
            return;
        //
        Tip actualTip = Tip.RIGHT;
        //
        Point firstPoint = points.firstElement();
        Point prevPoint = null;
        
        //
        Iterator<Point> currentPoint_it = points.iterator();
        Iterator<Tip> currentPointTip_it = pointsTip.iterator();
        
        for (Point currentPoint : points) {
            if (prevPoint != null) {
                if (prevPoint.y >= currentPoint.y) {
                    if (actualTip == Tip.RIGHT)
                        actualTip = Tip.LEFT;
                } else {
                    if (actualTip == Tip.LEFT)
                        return;
                }
            }
            currentPoint.tip = actualTip;
            prevPoint = currentPoint;
        }
        type = Type.MONOTONE;
    }

    /**
     * Algorithme qui fusionne les cotés des points
     */
    Vector<Point> makeSide(Polygon poly, Tip choice, boolean Unordered) {
        Vector<Point> side = new Vector<Point>();
        //
        int i = Unordered ? points.size() - 1 : 0;
        int end = Unordered ? -1 : points.size();
        //
        while (i != end) {
            Point p = points.get(i);
            if (p.tip == choice)
                side.add(p);
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
    void buildFusion(Polygon poly) {
        searchTipSimple();
        if (type != Polygon.Type.Monotone)
            return;
        //
        fusionPoints.clear();
        Vector<Point> sideRight = makeSide(poly, Tip.RIGHT, false);
        Vector<Point> sideLeft = makeSide(poly, Tip.LEFT, true);
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
     * Produit vectoriel
     * x = a.y*0 - 0*b.y* = 0
     * y = 0*b.x - a.x*0 = 0
     * z = a.x*b.y - a.y*b.x
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
    static void buildTriangulation(Polygon poly) {
        triangulationFusion.clear();
        if (type == Type.UNKNOWN)
            return;
        //
        Vector<Point> pile = new Vector<Point>();
        boolean havePop;
        //
        for (int idCurrent = 0; idCurrent < fusionPoints.size() - 1; idCurrent++) {
            Point current = fusionPoints.get(idCurrent);
            do {
                havePop = false;
                if (pile.size() >= 2) {
                    Point pLast = pile.get(pile.size() - 1), pLastLast = pile.get(pile.size() - 2);
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

    /**
     * Algorithme qui recherche le coté des points
     */
    static void buildSide(Polygon poly) {
        topPointIndex = -1;
        bottomPointIndex = -1;
        type = Type.UNKNOWN;
        type = Type.MONOTONE;
        //
        if (points.size() < 3)
            return;
        //
        Point currentPoint = null, prevPoint = null;
        Point.Tip actualTip = Point.Tip.RIGHT;
        boolean topChanged = false;
        //
        int pointSize = points.size();
        //
        // sens horaire
        boolean clockwise =
                (points.elementAt(0).y < points.elementAt(1).y) && (points.elementAt(0).x < points.elementAt(1).x) ||
                        (points.elementAt(0).y > points.elementAt(1).y) && (points.elementAt(0).x > points.elementAt(1).x);
        //
        for (int i = 0; ; i = (i + 1) % pointSize) {
            if ((i >= pointSize) && !topChanged) // || ( !topChanged) )
                break;
            //
            currentPoint = points.elementAt(clockwise ? i : (pointSize - (1 + i)));
            //
            if (prevPoint != null) {
                if (prevPoint.y >= currentPoint.y) {
                    if (actualTip == Point.Tip.RIGHT)
                        actualTip = Point.Tip.LEFT;
                } else {
                    if (actualTip == Point.Tip.LEFT)
                        return;
                }
            }
            currentPoint.tip = actualTip;
            prevPoint = currentPoint;
        }

            /*if (topChanged && !currentPointIt.hasNext())
                currentPointIt = points.iterator(); // looping if top point changed
            if (!currentPointIt.hasNext())
                break;
            //
            Point currentPoint = currentPointIt.next();
            //
            if (topPoint == currentPoint) {
                break;
            } else if ((topPoint == null)) {
                topPoint = currentPoint;
                topPoint.tip = Point.Tip.TOP;
                topChanged = true;
            }


            //
            prevPoint = currentPoint;
        }

/*
        if (topPoint != null) {
            if ()
        }
        if (prevPoint != null) {
            if (prevPoint.y >= currentPoint.y) {
                if (actualTip == Point.Tip.RIGHT)
                    actualTip = Point.Tip.LEFT;
            } else {
                if (actualTip == Point.Tip.LEFT)
                    return;
            }
        }
        currentPoint.tip = actualTip;
        prevPoint = currentPoint;
    }*/

        type = Type.MONOTONE;
    }



};
