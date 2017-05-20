package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.CloudOfPoints;
import at.u4a.geometric_algorithms.geometric.CloudOfSegments;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Segment;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.AlgorithmLayer;
import at.u4a.geometric_algorithms.utils.Calc;

/**
 * Triangulation
 * 
 * @author Hip
 * @see Computational Geometry - Algorithms and Applications,
 *      SegmentIntersection
 *
 */
public class SegmentIntersection extends AbstractAlgorithm {

    public static class Builder implements InterfaceAlgorithmBuilder {

        @Override
        public String getName() {
            return SegmentIntersection.class.getSimpleName();
        }

        @Override
        public boolean canApply(AbstractLayer l) {
            return (l.getShape() instanceof CloudOfSegments);
        }

        static int SegmentIntersectionCount = 1;

        @Override
        public AbstractLayer builder(AbstractLayer l) {

            //
            AbstractShape as = l.getShape();
            if (!(as instanceof CloudOfSegments))
                throw new RuntimeException("SegmentIntersection need a CloudOfSegments Shape !");

            //
            CloudOfSegments cos = (CloudOfSegments) as;

            //
            AbstractLayer al = new AlgorithmLayer<SegmentIntersection>(new SegmentIntersection(cos.cloud, cos), Algorithm.SegmentIntersection, l);
            al.setLayerName("t" + String.valueOf(SegmentIntersectionCount));
            SegmentIntersectionCount++;
            return al;
        }

    };

    private final AbstractList<Segment> cloud;
    private final AbstractShape as;

    private final CloudOfPoints cop;

    public SegmentIntersection(AbstractList<Segment> cloud, AbstractShape as) {
        this.cloud = cloud;
        this.as = as;

        this.cop = new CloudOfPoints(as.origin);
        // this.cop = new MonotonePolygon(as.origin, points);
    }

    /* ************** */

    @Override
    public void accept(Vector<AbstractLayer> v, InterfaceGraphicVisitor visitor) {
        makeSegmentInteraction();
        visitor.visit(cop);
    }

    /* ************** */

    public CloudOfPoints getCloudOfPoint() {
        makeSegmentInteraction();
        return cop;
    }

    /* ************** */

    @Override
    public AbstractShape getCompositeShape() {
        return getCloudOfPoint();
    }

    /* ************** */

    private int mutablePreviousLinesHash = 0;

    protected void makeSegmentInteraction() {

        int currentLinesHash = as.hashCode();
        if (currentLinesHash != mutablePreviousLinesHash) {

            //
            //buildSegmentInteraction();
            buildSegmentInteractionQuadratique();

            //
            mutablePreviousLinesHash = currentLinesHash;
        }

    }

    /* ************** */

    private class TComparator implements Comparator<Segment> {

        @Override
        public int compare(Segment arg0, Segment arg1) {
            // TODO Auto-generated method stub
            return 0;
        }

    };

    /* ************** */

    private final HashMap<Point, Vector<Segment>> intersections = new HashMap<Point, Vector<Segment>>();

    private final SortedMap<Segment, Segment> T = new TreeMap<Segment, Segment>(new TComparator());

    /* ************** */

    /**
     * Permet d'associer un Segment avec un Point et de trouver le point e plus
     * en haut ou a gauche
     */
    class SegmentAssoc {
        public Segment s;
        public Point upper, downer;

        public SegmentAssoc(Segment s) {
            this.s = s;
            if ((s.a.y > s.b.y) ? true : ((s.a.y < s.b.y) ? false : ((s.a.x >= s.b.x) ? true : false))) {
                upper = s.a;
                downer = s.b;
            } else {
                upper = s.b;
                downer = s.a;
            }
        }
    }

    private void findIntersections(Segment s) {
        ArrayDeque<SegmentAssoc> Q = new ArrayDeque<SegmentAssoc>();
        Q.push(new SegmentAssoc(s));
        while (!Q.isEmpty())
            handleEventPoint(Q.pop());
    }

    private void handleEventPoint(SegmentAssoc sa) {

    }

    /* ************** */

    /**
     */
    private boolean buildSegmentInteraction() {
        //
        cop.clear();

        return true;
    }

    /* ************** */

    /**
     * Used for test and compare
     */
    protected boolean buildSegmentInteractionQuadratique() {
        //
        cop.clear();
        Set<Point> intersections = new HashSet<Point>();

        
        for (Segment s1 : cloud) {
            boolean firstS2 = true;
            
            for (Segment s2 : cloud) {
                
                if(firstS2) {
                    firstS2 = false;
                    continue;
                }
                
                Point pInter = Calc.intersection(s1,s2);
                
                if (pInter == null)
                    continue;
                
                if (!intersections.contains(pInter)) {
                    intersections.add(pInter);
                    cop.addPoint(pInter);
                }
                
            }
        }

        return true;
    }

};
