package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
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
public class SegmentIntersectionQuadratic extends AbstractAlgorithm {

    public static class Builder implements InterfaceAlgorithmBuilder {

        @Override
        public String getName() {
            return SegmentIntersectionQuadratic.class.getSimpleName();
        }

        @Override
        public boolean canApply(AbstractLayer l) {
            return (l.getShape() instanceof CloudOfSegments);
        }

        static int SegmentIntersectionQuadraticCount = 1;

        @Override
        public AbstractLayer builder(AbstractLayer l) {

            //
            AbstractShape as = l.getShape();
            if (!(as instanceof CloudOfSegments))
                throw new RuntimeException("SegmentIntersection need a CloudOfSegments Shape !");

            //
            CloudOfSegments cos = (CloudOfSegments) as;

            //
            AbstractLayer al = new AlgorithmLayer<SegmentIntersectionQuadratic>(new SegmentIntersectionQuadratic(cos.cloud, cos), Algorithm.SegmentIntersection, l);
            al.setLayerName("siq" + String.valueOf(SegmentIntersectionQuadraticCount));
            SegmentIntersectionQuadraticCount++;
            return al;
        }

    };

    private final AbstractList<Segment> cloud;
    private final AbstractShape as;

    private final CloudOfPoints cop;

    public SegmentIntersectionQuadratic(AbstractList<Segment> cloud, AbstractShape as) {
        super("intersections tests");
        this.cloud = cloud;
        this.as = as;
        this.cop = new CloudOfPoints(as.origin);
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
            statusStartBuild();
            statusBuildIs(buildSegmentInteractionQuadratic()); 
            mutablePreviousLinesHash = currentLinesHash;
        }

    }

    /* ************** */

    protected boolean buildSegmentInteractionQuadratic() {

        //
        cop.clear();
        Set<Point> intersections = new HashSet<Point>();

        for (Segment s1 : cloud) {
            boolean firstS2 = true;

            for (Segment s2 : cloud) {

                if (firstS2) {
                    firstS2 = false;
                    continue;
                }

                //
                statusAddCounter();

                //
                Point pInter = Calc.intersection(s1, s2);
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
