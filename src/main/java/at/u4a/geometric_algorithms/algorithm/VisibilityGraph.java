package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.Polygon;
import at.u4a.geometric_algorithms.geometric.Polygon.MonotonePolygon;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.AlgorithmLayer;
import javafx.scene.paint.Color;

public class VisibilityGraph extends AbstractAlgorithm {

    public static class Builder implements InterfaceAlgorithmBuilder {

        @Override
        public String getName() {
            return VisibilityGraph.class.getSimpleName();
        }

        @Override
        public boolean canApply(AbstractList<AbstractLayer> layers) {
            if(layers.isEmpty())
                return false;
            //
            AbstractShape as = layers.get(0).getShape();
            return (as instanceof Polygon);
        }

        static int VisibilityGraphCount = 1;

        @Override
        public AbstractLayer builder(AbstractList<AbstractLayer> layers) {

            if(layers.isEmpty())
                throw new RuntimeException("Need one layer !");
            
            //
            AbstractShape as = layers.get(0).getShape();
            
            /** @todo */
            if(true)
                throw new RuntimeException("VisibilityGraph need a List of Polygon");
            
            //
            AbstractShape as = l.getShape();
            if (!(as instanceof Polygon))
                throw new RuntimeException("VisibilityGraph need a Monotisation Algorithm !");

            //
            Polygon poly = (Polygon) as;

            //
            AbstractLayer al = new AlgorithmLayer<VisibilityGraph>(new VisibilityGraph(poly), Algorithm.VisibilityGraph, l);
            al.setLayerName("vg" + String.valueOf(VisibilityGraphCount));
            VisibilityGraphCount++;
            return al;
        }

    };

    private final List<Polygon> polygons;

    public VisibilityGraph(List<Polygon> polygons) {
        this.polygons = polygons;
    }

    /* ************** */

    InterfaceGraphicVisitor mutableVisitorForDebugging = null;
    
    @Override
    public void accept(AbstractList<AbstractLayer> subLayers, InterfaceGraphicVisitor visitor) {

        mutableVisitorForDebugging = visitor;
        
        makeVisibilityGraph();

        if (haveMonotized) {
            for (Triangulation triangulation : triangulations) {
                triangulation.accept(v, visitor);
                //visitor.visit(triangulation.getPolygon());
            }
        }
    }


    @Override
    public int hashCode() {
        return monotisationAlgorithm.hashCode();
    }

    /* ************** */

    public boolean isMonotonized() {
        makeVisibilityGraph();
        return haveMonotized;
    }

    public Vector<MonotonePolygon> getMonotonesPolygon() {
        makeVisibilityGraph();
        return monotisationAlgorithm.getMonotonesPolygon();
    }

    /* ************** */

    @Override
    public AbstractShape getCompositeShape() {
        return null;
    }

    /* ************** */

    private ArrayList<Triangulation> triangulations = new ArrayList<Triangulation>();

    /* ************** */

    private int mutablePreviousMonotisationHash = 0;

    boolean haveMonotized = false;

    protected void makeVisibilityGraph() {

        int currentMonotisationHash = monotisationAlgorithm.hashCode();
        if (currentMonotisationHash != mutablePreviousMonotisationHash) {

            //
            triangulations.clear();
            haveMonotized = true;

            //
            final Vector<MonotonePolygon> mp_v = monotisationAlgorithm.getMonotonesPolygon();

            //
            if (mp_v == null) {
                haveMonotized = false;

            } else {
                for (MonotonePolygon mp : mp_v) {

                    //
                    if(mp.perimeter.size() <= 3)
                        continue;
                    
                    //
                    final Triangulation triangulationsAlgorithm = new Triangulation(mp.perimeter, mp, false);
                    triangulationsAlgorithm.mutableVisitorForDebugging = mutableVisitorForDebugging;
                    
                    //
                    if (!triangulationsAlgorithm.isMonotone()) {
                        haveMonotized = false;
                        
                        break;
                    }

                    //
                    triangulations.add(triangulationsAlgorithm);
                }
            }

            //
            mutablePreviousMonotisationHash = currentMonotisationHash;
        }

    }

};
