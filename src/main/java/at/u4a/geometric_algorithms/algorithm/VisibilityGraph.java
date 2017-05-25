package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.CloudOfPoints;
import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.geometric.Polygon.MonotonePolygon;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.AlgorithmLayer;
import at.u4a.geometric_algorithms.utils.NonOrientedGraph;
import at.u4a.geometric_algorithms.utils.NonOrientedGraph.Vertice;
import at.u4a.geometric_algorithms.utils.Mutable;
import javafx.scene.paint.Color;

public class VisibilityGraph extends AbstractAlgorithm {

    public static class Builder implements InterfaceAlgorithmBuilder {

        @Override
        public String getName() {
            return VisibilityGraph.class.getSimpleName();
        }

        @Override
        public boolean canApply(AbstractList<AbstractLayer> layers) {
            if (layers.isEmpty())
                return false;

            //
            int nbOfPolygon = 0;
            int nbOfCloudOfPoint = 0;

            //
            for (AbstractLayer l : layers) {
                AbstractShape as = l.getShape();
                if (as instanceof Polygon)
                    nbOfPolygon++;
                else if (as instanceof CloudOfPoints)
                    nbOfCloudOfPoint++;
                else
                    return false;
            }

            //
            return (nbOfPolygon > 1) && (nbOfCloudOfPoint == 1);
        }

        static int VisibilityGraphCount = 1;

        @Override
        public AbstractLayer builder(AbstractList<AbstractLayer> layers) {

            //
            if (!canApply(layers))
                throw new RuntimeException("Need more that one layer and 2 points !");

            //
            Vector<Polygon> polygons = new Vector<Polygon>();
            CloudOfPoints cof = null;

            //
            for (AbstractLayer l : layers) {
                AbstractShape as = l.getShape();
                if (as instanceof Polygon)
                    polygons.add((Polygon) as);
                else if (as instanceof CloudOfPoints)
                    cof = (CloudOfPoints) as;
            }

            //
            AbstractLayer al = new AlgorithmLayer<VisibilityGraph>(new VisibilityGraph(polygons, cof), Algorithm.VisibilityGraph, layers);
            al.setLayerName("vg" + String.valueOf(VisibilityGraphCount));
            VisibilityGraphCount++;
            return al;
        }

    };

    private final AbstractList<Polygon> polygons;
    private final CloudOfPoints cof;

    private final NonOrientedGraph<Point> g;

    public VisibilityGraph(AbstractList<Polygon> polygons, CloudOfPoints cof) {
        this.polygons = polygons;
        this.cof = cof;
        this.g = new NonOrientedGraph<Point>();
    }

    /* ************** */

    InterfaceGraphicVisitor mutableVisitorForDebugging = null;

    @Override
    public void accept(AbstractList<AbstractLayer> subLayers, InterfaceGraphicVisitor visitor) {

        mutableVisitorForDebugging = visitor;

        makeVisibilityGraph();

    }

    @Override
    public int hashCode() {
        int polyHash = Mutable.getHashCode(polygons);
        return (polyHash * 31) + cof.hashCode();
    }

    /* ************** */

    public boolean isCompute() {
        return haveCompute;
    }

    /* ************** */

    @Override
    public AbstractShape getCompositeShape() {
        return null;
    }

    /* ************** */

    private ArrayList<Triangulation> triangulations = new ArrayList<Triangulation>();

    /* ************** */

    private int mutablePreviousVisibilityGraphHash = 0;

    boolean haveCompute = false;

    protected void makeVisibilityGraph() {

        int currenVisibilityGraphHash = hashCode();
        if (currenVisibilityGraphHash != mutablePreviousVisibilityGraphHash) {

            //
            haveCompute = false;

            //
            buildVisibilityGraph();

            //
            mutablePreviousVisibilityGraphHash = currenVisibilityGraphHash;
        }

    }

    /* ************** */

    protected Vector<NonOrientedGraph<Point>.Vertice> visibleVertices(NonOrientedGraph<Point>.Vertice v) {
        return null;

    }

    protected boolean buildVisibilityGraph() {

        //
        g.clear();

        //
        for (Polygon poly : polygons)
            for (Point p : poly.perimeter)
                g.addVerticle(p);

        //
        for (NonOrientedGraph<Point>.Vertice v : g.vertices.values()) {
            Vector<NonOrientedGraph<Point>.Vertice> visibles = visibleVertices(v);
            if (visibles != null)
                for (NonOrientedGraph<Point>.Vertice w : visibles)
                    g.addArc(v, w);
        }

        return true;
    }

};
