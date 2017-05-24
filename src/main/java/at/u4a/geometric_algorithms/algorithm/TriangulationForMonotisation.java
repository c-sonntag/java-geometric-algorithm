package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.geometric.Polygon.MonotonePolygon;
import at.u4a.geometric_algorithms.geometric.Segment;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.AlgorithmLayer;
import at.u4a.geometric_algorithms.utils.Calc;


public class TriangulationForMonotisation extends AbstractAlgorithm {

    public static class Builder implements InterfaceAlgorithmBuilder {

        @Override
        public String getName() {
            return TriangulationForMonotisation.class.getSimpleName();
        }

        @Override
        public boolean canApply(AbstractLayer l) {
            return (l.getAlgorithm() instanceof Monotisation);
        }

        static int TriangulationForMonotisationCount = 1;

        @Override
        public AbstractLayer builder(AbstractLayer l) {

            //
            AbstractAlgorithm aa = l.getAlgorithm();
            if (!(aa instanceof Monotisation))
                throw new RuntimeException("TriangulationForMonotisation need a Monotisation Algorithm !");

            //
            Monotisation monotisationAlgorithm = (Monotisation) aa;

            //
            AbstractLayer al = new AlgorithmLayer<TriangulationForMonotisation>(new TriangulationForMonotisation(monotisationAlgorithm), Algorithm.TriangulationForMonotisation, l);
            al.setLayerName("tfm" + String.valueOf(TriangulationForMonotisationCount));
            TriangulationForMonotisationCount++;
            return al;
        }

    };

    private final Monotisation monotisationAlgorithm;

    public TriangulationForMonotisation(Monotisation monotisationAlgorithm) {
        this.monotisationAlgorithm = monotisationAlgorithm;
    }

    /* ************** */

    @Override
    public void accept(Vector<AbstractLayer> v, InterfaceGraphicVisitor visitor) {

        makeTriangulationForMonotisation();

        //
        if (actualType == Polygon.Type.Monotone) {

            //
            final Segment sToOrigin = new Segment();
            for (Segment tf : triangulationFusion) {
                sToOrigin.set(tf);
                as.convertToStandard(sToOrigin.a);
                as.convertToStandard(sToOrigin.b);
                visitor.visit_unit(sToOrigin);
            }

        }

        //
        // visitor.drawEdgeTipFromList(poly, poly.perimeter);

    }

    /* ************** */

    public boolean isMonotonized() {
        makeTriangulationForMonotisation();
        return actualType == Polygon.Type.Monotone;
    }

    public List<MonotonePolygon> getPolygon() {
        makeTriangulationForMonotisation();
        if (actualType == Polygon.Type.Monotone)
            return mp;
        else
            return null;
    }

    /* ************** */

    @Override
    public AbstractShape getCompositeShape() {
        return null;
    }

    /* ************** */

    private int mutablePreviousPolyHash = 0;

    protected void makeTriangulationForMonotisation() {

        int currentPolyHash = as.hashCode();
        if (currentPolyHash != mutablePreviousPolyHash) {

            //
            statusStartBuild();

            //
            actualType = Polygon.Type.Simple;
            if (buildTriangulationForMonotisation()) {
                actualType = Polygon.Type.Monotone;
                statusFinishBuild();
            } else
                statusInteruptBuild();
                

            //
            mutablePreviousPolyHash = currentPolyHash;
        }

    }

};
