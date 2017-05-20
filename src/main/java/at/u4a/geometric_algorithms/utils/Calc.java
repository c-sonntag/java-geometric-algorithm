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

    /**
     * Renvoi l'intersegtion entre 2 segments Si aucune intersection trouvé
     * renvoie nulle
     */
    public static Point intersection(Segment sa, Segment sb) {

        Segment.Inclinaison aInc = sa.getInclinaison();
        Segment.Inclinaison bInc = sb.getInclinaison();

        if (//
        ((aInc.gradiant == bInc.gradiant) && (aInc.isHorizontal == bInc.isHorizontal)) || //
                (!aInc.compute || !bInc.compute) //
        )
            return null;

        /** @todo its only for the same orientation */
        final double x = (bInc.decal - aInc.decal) / (aInc.gradiant - bInc.gradiant);
        final double y = aInc.gradiant * x + aInc.decal;

        return new Point(x, y);
    }

}
