package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;
import at.u4a.geometric_algorithms.algorithm.ConvexEnvelope.PointTipped.Tip;
import at.u4a.geometric_algorithms.geometric.*;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.AlgorithmLayer;

/**
 * Triangulation
 * 
 * @author Hip
 *
 */
public class ConvexEnvelope extends AbstractAlgorithm {

    public static class Builder implements InterfaceAlgorithmBuilder {

        @Override
        public String getName() {
            return ConvexEnvelope.class.getSimpleName();
        }

        @Override
        public boolean canApply(AbstractLayer l) {
            AbstractShape as = l.getShape();
            return (as instanceof CloudOfPoints) || (as instanceof Polygon) || (as instanceof CloudOfSegments);
        }

        static int ConvexEnvelopeCount = 1;

        @Override
        public AbstractLayer builder(AbstractLayer l) {

            //
            AbstractShape as = l.getShape();
            AbstractList<Point> points = null;

            //
            if (as instanceof CloudOfPoints) {
                points = ((CloudOfPoints) as).cloud;
            } else if (as instanceof Polygon) {
                points = ((Polygon) as).perimeter;
            } else if (as instanceof CloudOfSegments) {
                points = ((CloudOfSegments) as).getPoints();
            } else {
                throw new RuntimeException("ConvexEnvelope need some points (CloudOfPoints, Polygon, CloudOfSegments ...) !");
            }

            //
            AbstractLayer al = new AlgorithmLayer<ConvexEnvelope>(new ConvexEnvelope(points, as), Algorithm.ConvexEnvelope, l);
            al.setLayerName("ce" + String.valueOf(ConvexEnvelopeCount));
            ConvexEnvelopeCount++;
            return al;
        }

    };

    private final AbstractList<Point> points;
    private final AbstractShape as;

    public ConvexEnvelope(AbstractList<Point> points, AbstractShape as) {
        this.points = points;
        this.as = as;
    }

    /* ************** */

    @Override
    public void accept(Vector<AbstractLayer> v, InterfaceGraphicVisitor visitor) {
        makeConvexEnveloppe();
        visitor.visit(convexPoly);
    }

    /* ************** */

    private int mutablePreviousShapeHash = 0;

    protected void makeConvexEnveloppe() {

        int currentShapeHash = as.hashCode();
        if (currentShapeHash != mutablePreviousShapeHash) {

            //
            buildConvexPolygon();
            //
            mutablePreviousShapeHash = currentShapeHash;
        }

    }

    /* ************** */
    
    private final Polygon convexPoly = new Polygon();

    public Polygon getPolygon() {
        return convexPoly;
    }

    /* ************** */
    
    
    private boolean buildConvexPolygon() {
        
        convexPoly.clear();
        
        
        return true;
    }
    

};
