package at.u4a.geometric_algorithms.utils;

import at.u4a.geometric_algorithms.geometric.Line;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Segment;

public class Calc {

    /**
     * Fait le calcul necessaire au decalage de A et B en fonction de Origine et
     * effectue le produit vectoriel : x = a.y*0 - 0*b.y* = 0 y = 0*b.x - a.x*0
     * = 0 z = a.x*b.y - a.y*b.x
     */
    public static double resumeProduitVectorielZ(Point pA, Point pOrigine, Point pB) {

        final double pAx = pA.x - pOrigine.x;
        final double pAy = pA.y - pOrigine.y;
        final double pBx = pB.x - pOrigine.x;
        final double pBy = pB.y - pOrigine.y;

        return pAx * pBy - pAy * pBx;
    }

    /*
     * public static double resumeProduitVectorielZWithoutXTorque(Point pA,
     * Point pOrigine, Point pB) {
     * 
     * double pAx = pA.x - pOrigine.x; double pAy = pA.y - pOrigine.y; double
     * pBx = pB.x - pOrigine.x; double pBy = pB.y - pOrigine.y;
     * 
     * if(((pAx > 0) && (pBx > 0)) || ((pAx < 0) && (pBx < 0))) pAx = pBx;
     * System.out.print("pAx("+pAx+") pAy("+pAy+") pBx("+pBx+") pBy("+pBy+") ");
     * 
     * /*if((pAx > 0) && (pBx > 0)) pAx = pBx = Math.max(pAx, pBx); else if
     * ((pAx < 0) && (pBx < 0)) pAx = pBx = Math.min(pAx, pBx); if((pAy >
     * pOrigine.y) && (pBy > pOrigine.y)) pAy = pBy = Math.max(pAy, pBy); else
     * if ((pAy < pOrigine.y) && (pBy < pOrigine.y)) pAy = pBy = Math.min(pAy,
     * pBy);
     * 
     * return pAx * pBy - pAy * pBx; }
     */

    /**
     * Renvoi l'intersection entre 2 segments.
     * 
     * @return Si aucune intersection est trouvé renvoie null
     */
    public static Point intersection(Segment sa, Segment sb) {
        final Point intersectLine = intersection((Line) sa, (Line) sb);
        if (intersectLine == null)
            return null;
        else
            return /* */
            /* Sa : */ (
            /* minX */ ((intersectLine.x >= Math.min(sa.a.x, sa.b.x)) &&
            /* maxX */ (intersectLine.x <= Math.max(sa.a.x, sa.b.x)) &&
            /* minY */ (intersectLine.y >= Math.min(sa.a.y, sa.b.y)) &&
            /* maxY */ (intersectLine.y <= Math.max(sa.a.y, sa.b.y)))) &&
            /* Sb : */ (
            /* minX */ ((intersectLine.x >= Math.min(sb.a.x, sb.b.x)) &&
            /* maxX */ (intersectLine.x <= Math.max(sb.a.x, sb.b.x)) &&
            /* minY */ (intersectLine.y >= Math.min(sb.a.y, sb.b.y)) &&
            /* maxY */ (intersectLine.y <= Math.max(sb.a.y, sb.b.y)))
            /* */ ) ? intersectLine : null;
    }

    /**
     * Renvoi l'intersection entre 2 lignes.
     * 
     * @return Si aucune intersection est trouvé renvoie null
     */
    public static Point intersection(Line la, Line lb) {

        final Segment.Inclinaison aInc = la.getInclinaison();
        final Segment.Inclinaison bInc = lb.getInclinaison();

        if ((aInc.gradiant == bInc.gradiant) || (!aInc.compute || !bInc.compute) || (aInc.isInfinite && bInc.isInfinite))
            return null;

        // if have infinite
        if (aInc.isInfinite)
            return new Point(aInc.decal, bInc.gradiant * aInc.decal + bInc.decal);
        else if (bInc.isInfinite)
            return new Point(bInc.decal, aInc.gradiant * bInc.decal + aInc.decal);

        // its only for the same orientation
        final double x = (bInc.decal - aInc.decal) / (aInc.gradiant - bInc.gradiant);
        final double y = aInc.gradiant * x + aInc.decal;
        return new Point(x, y);
    }

    /**
     * 
     * 
     * 
     * Renvoi l'intersegtion entre 2 segments Si aucune intersection trouvé
     * renvoie null
     * 
     * @todo a terminer
     */
    public static Point intersectionWithHV(Line la, Line lb) {

        final Segment.InclinaisonHV aInc = la.getInclinaisonHV();
        final Segment.InclinaisonHV bInc = lb.getInclinaisonHV();

        if (//
        ((aInc.gradiant == bInc.gradiant) && (aInc.isHorizontal == bInc.isHorizontal)) || //
                (!aInc.compute || !bInc.compute) //
        )
            return null;

        // its only for the same orientation
        if (aInc.isHorizontal == bInc.isHorizontal) {
            final double w = (bInc.decal - aInc.decal) / (aInc.gradiant - bInc.gradiant);
            final double z = aInc.gradiant * w + aInc.decal;
            return aInc.isHorizontal ? new Point(w, z) : new Point(z, w);
        }

        final Segment.InclinaisonHV horizontalInc = aInc.isHorizontal ? aInc : bInc;
        final Segment.InclinaisonHV verticalInc = aInc.isHorizontal ? bInc : aInc;

        // It's a cross
        if (horizontalInc.gradiant == 0) {
            return new Point(verticalInc.decal + (verticalInc.gradiant * horizontalInc.decal), horizontalInc.decal);
        } else if (verticalInc.gradiant == 0) {
            return new Point(verticalInc.decal, horizontalInc.decal + (horizontalInc.gradiant * verticalInc.decal));
        }

        // NEED TO FIND DIFFERENT BETWEEN HorizontalLine and VerticalLine
        return null; // normaly is not null , but it's in waiting to finish this
                     // work
    }

}
