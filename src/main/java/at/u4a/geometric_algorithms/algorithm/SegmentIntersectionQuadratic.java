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
import at.u4a.geometric_algorithms.geometric.Polygon;
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
        public boolean canApply(AbstractList<AbstractLayer> layers) {
            if(layers.size() != 1)
                return false;
            //
            AbstractShape as = layers.get(0).getShape();
            return ((as instanceof CloudOfSegments) || (as instanceof Polygon));
        }

        static int SegmentIntersectionQuadraticCount = 1;

        @Override
        public AbstractLayer builder(AbstractList<AbstractLayer> layers) {

            if(layers.isEmpty())
                throw new RuntimeException("Need one layer !");
            
            //
            AbstractShape as = layers.get(0).getShape();
            Iterable<Segment> cloud = null;
            

            //
            if (as instanceof CloudOfSegments)
                cloud = ((CloudOfSegments) as).cloud;
            else if (as instanceof Polygon)
                cloud = ((Polygon) as);
            else
                throw new RuntimeException("SegmentIntersection need a CloudOfSegments Shape, or Polygon Shape !");

            //
            AbstractLayer al = new AlgorithmLayer<SegmentIntersectionQuadratic>(new SegmentIntersectionQuadratic(cloud, as), Algorithm.SegmentIntersectionQuadratic, layers);
            al.setLayerName("siq" + String.valueOf(SegmentIntersectionQuadraticCount));
            SegmentIntersectionQuadraticCount++;
            return al;
        }

    };

    private final Iterable<Segment> cloud;
    private final AbstractShape as;

    private final CloudOfPoints cop;

    public SegmentIntersectionQuadratic(Iterable<Segment> cloud, AbstractShape as) {
        super("intersections tests");
        this.cloud = cloud;
        this.as = as;
        this.cop = new CloudOfPoints(as.origin);
    }

    /* ************** */

    @Override
    public void accept(AbstractList<AbstractLayer> subLayers, InterfaceGraphicVisitor visitor) {
        makeSegmentInteraction();
        visitor.visit(cop);
    }

    @Override
    public int hashCode() {
        return as.hashCode();
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
                Point pInter = Calc.intersectionOnLine(s1, s2);
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
