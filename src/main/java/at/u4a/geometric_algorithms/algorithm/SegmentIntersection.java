package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.CloudOfPoints;
import at.u4a.geometric_algorithms.geometric.CloudOfSegments;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.geometric.Polygon.MonotonePolygon;
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
            buildSegmentInteraction();

            //
            mutablePreviousLinesHash = currentLinesHash;
        }

    }

    /* ************** */

    /**
     * 
     */
    private void findIntersections(Segment s) {

        ArrayDeque<Point> Q = new ArrayDeque<Point>();
        Q.push(s.b);
        
        
        
        
    }

    /* ************** */

    /**
     */
    private boolean buildSegmentInteraction() {

        //
        cop.clear();

        return true;
    }
};
