package graphique;

import java.util.*;

import graphique.Polygon.Type;
import graphique.Polygon.Segment;
import graphique.Polygon.Point;
import graphique.Polygon.Point.Tip;

/**
 * La classe algorithme.
 */
class Algorithmes {

    /**
     * Lance les algorithmes
     */
    public static void run(Polygon poly) {
        buildSegment(poly);
        buildFusion(poly);
        buildTriangulation(poly);
    }

    /**
     * Algorithme qui crée les segments a partir des points.
     */
    private static void buildSegment(Polygon poly) {
        // Ajout d'un segment entre chaque paire de points consecutifs
        for (int i = 0; i < poly.points.size() - 1; i++) {
            Point p1 = poly.points.elementAt(i);
            Point p2 = poly.points.elementAt(i + 1);
            if (poly.segments.size() > i) {
                Segment s = poly.segments.get(i);
                if ((s.a != p1) || (s.b != p2)) {
                    s.b = p1;
                    s.a = p2;
                }
            } else
                poly.segments.add(new Segment(p1, p2));
        }
        // Ajout du segment qui referme la boucle
        if (poly.segments.size() > 1) {
            Segment lastSegment = poly.segments.lastElement();
            if ((lastSegment.a != poly.points.firstElement()) || (lastSegment.b != poly.points.lastElement()))
                poly.segments.add(new Segment(poly.points.firstElement(), poly.points.lastElement()));
        }
    }

    /**
     * Algorithme qui recherche le coté des points
     *
     * @todo l'ordre des points doit être horaire pour le moments, integrer un système de reconnaissance pour plus tard
     */
    static void searchTipSimple(Polygon poly) {
        poly.type = Type.UNKNOWN;
        if (poly.points.size() < 3) return;
        //
        Point.Tip actualTip = Tip.RIGHT;
        //
        Point firstPoint = poly.points.firstElement();
        Point prevPoint = null;
        //
        for (Point currentPoint : poly.points) {
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
        poly.type = Type.MONOTONE;
    }

    /**
     * Algorithme qui recherche le coté des points
     *
     * @todo l'ordre des points doit être horaire pour le moments, integrer un système de reconnaissance pour plus tard
     */
    static void searchTip(Polygon poly) {
        poly.type = Type.UNKNOWN;
        if (poly.points.size() < 3) return;
        //
        Point.Tip actualTip = Tip.RIGHT;
        //
        Point firstPoint = poly.points.firstElement();
        Point prevPoint = null;
        //
        for (Point currentPoint : poly.points) {
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
        poly.type = Type.MONOTONE;
    }

    /**
     * Algorithme qui fusionne les cotés des points
     */
    static Vector<Point> makeSide(Polygon poly, Tip choice, boolean Unordered) {
        Vector<Point> side = new Vector<Point>();
        //
        int i = Unordered ? poly.points.size() - 1 : 0;
        int end = Unordered ? -1 : poly.points.size();
        //
        while (i != end) {
            Point p = poly.points.get(i);
            if (p.tip == choice)
                side.add(p);
            //
            if (Unordered) i--;
            else i++;
        }
        return side;
    }

    /**
     * Algorithme qui fusionne les cotés des points
     */
    static void buildFusion(Polygon poly) {
        searchTipSimple(poly);
        if (poly.type == Type.UNKNOWN)
            return;
        //
        poly.fusionPoints.clear();
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
                    poly.fusionPoints.add(leftPoint);
                    idLeft++;
                } else {
                    poly.fusionPoints.add(rightPoint);
                    idRight++;
                }
            } else if (idRight < sideRightSize) {
                poly.fusionPoints.add(sideRight.get(idRight));
                idRight++;
            } else if (idLeft < sideLeftSize) {
                poly.fusionPoints.add(sideLeft.get(idLeft));
                idLeft++;
            } else
                break;
        }
    }

    static double getDistance(Point pA, Point pB) {
        double x = Math.abs(pA.x - pB.x);
        double y = Math.abs(pA.y - pB.y);
        return Math.sqrt(x * x + y * y);
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
        poly.triangulationFusion.clear();
        if (poly.type == Type.UNKNOWN)
            return;
        //
        Vector<Point> pile = new Vector<Point>();
        boolean havePop;
        //
        for (int idCurrent = 0; idCurrent < poly.fusionPoints.size() - 1; idCurrent++) {
            Point current = poly.fusionPoints.get(idCurrent);
            do {
                havePop = false;
                if (pile.size() >= 2) {
                    Point pLast = pile.get(pile.size() - 1), pLastLast = pile.get(pile.size() - 2);
                    double produit = resumeProduitVectorielZ(current, pLast, pLastLast);
                    //
                    if (((produit > 0) && (current.tip == Tip.RIGHT)) || ((produit < 0) && (current.tip == Tip.LEFT))) {
                        poly.triangulationFusion.add(new Segment(current, pLastLast));
                        pile.remove(pLast);
                        havePop = true;
                    } else if (current.tip != pLast.tip) {
                        poly.triangulationFusion.add(new Segment(current, pLast));
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
        poly.topPointIndex = -1;
        poly.bottomPointIndex = -1;
        poly.type = Type.UNKNOWN;
        poly.type = Type.MONOTONE;
        //
        if (poly.points.size() < 3)
            return;
        //
        Point currentPoint = null, prevPoint = null;
        Point.Tip actualTip = Point.Tip.RIGHT;
        boolean topChanged = false;
        //
        int pointSize = poly.points.size();
        //
        // sens horaire
        boolean clockwise =
                (poly.points.elementAt(0).y < poly.points.elementAt(1).y) && (poly.points.elementAt(0).x < poly.points.elementAt(1).x) ||
                        (poly.points.elementAt(0).y > poly.points.elementAt(1).y) && (poly.points.elementAt(0).x > poly.points.elementAt(1).x);
        //
        for (int i = 0; ; i = (i + 1) % pointSize) {
            if ((i >= pointSize) && !topChanged) // || ( !topChanged) )
                break;
            //
            currentPoint = poly.points.elementAt(clockwise ? i : (pointSize - (1 + i)));
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
                currentPointIt = poly.points.iterator(); // looping if top point changed
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

        poly.type = Type.MONOTONE;
    }

    /**
     * Retourne un nombre aleatoire entre 0 et n-1.
     */
    static int rand(int n) {
        int r = new Random().nextInt();
        if (r < 0)
            r = -r;
        return r % n;
    }
}
