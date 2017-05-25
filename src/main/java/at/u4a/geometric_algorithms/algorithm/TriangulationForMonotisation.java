package at.u4a.geometric_algorithms.algorithm;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.InterfaceAlgorithmBuilder;
import at.u4a.geometric_algorithms.geometric.AbstractShape;
import at.u4a.geometric_algorithms.geometric.Polygon.MonotonePolygon;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGraphicVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.AlgorithmLayer;
import javafx.scene.paint.Color;

public class TriangulationForMonotisation extends AbstractAlgorithm {

    public static class Builder implements InterfaceAlgorithmBuilder {

        @Override
        public String getName() {
            return TriangulationForMonotisation.class.getSimpleName();
        }

        @Override
        public boolean canApply(AbstractList<AbstractLayer> layers) {
            if(layers.size() != 1)
                return false;
            //
            AbstractAlgorithm aa = layers.get(0).getAlgorithm();
            return (aa instanceof Monotisation);
        }

        static int TriangulationForMonotisationCount = 1;

        @Override
        public AbstractLayer builder(AbstractList<AbstractLayer> layers) {

            if(layers.isEmpty())
                throw new RuntimeException("Need one layer !");
            
            //
            AbstractAlgorithm aa = layers.get(0).getAlgorithm();
            if (!(aa instanceof Monotisation))
                throw new RuntimeException("TriangulationForMonotisation need a Monotisation Algorithm !");

            //
            Monotisation monotisationAlgorithm = (Monotisation) aa;

            //
            AbstractLayer al = new AlgorithmLayer<TriangulationForMonotisation>(new TriangulationForMonotisation(monotisationAlgorithm), Algorithm.TriangulationForMonotisation, layers);
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

    InterfaceGraphicVisitor mutableVisitorForDebugging = null;
    
    @Override
    public void accept(AbstractList<AbstractLayer> subLayers, InterfaceGraphicVisitor visitor) {

        mutableVisitorForDebugging = visitor;
        
        makeTriangulationForMonotisation();

        if (haveMonotized) {
            for (Triangulation triangulation : triangulations) {
                triangulation.accept(subLayers, visitor);
                //visitor.visit(triangulation.getPolygon());
            }
        }

    }

    public void acceptDebug(AbstractList<AbstractLayer> subLayers, InterfaceGraphicVisitor visitor) {

        int countStroboscope = 0;
        Color colorStrop[] = { Color.CORAL, Color.BLUEVIOLET, Color.MEDIUMSPRINGGREEN, Color.HOTPINK, Color.YELLOWGREEN, Color.FIREBRICK, Color.GREEN };

        makeTriangulationForMonotisation();

        System.out.println();

        if (haveMonotized) {

            for (Triangulation triangulation : triangulations) {

                // MonotonePolygon mp = triangulation.getPolygon();

                System.out.print(countStroboscope + " : ");

                Color sC = colorStrop[countStroboscope];

                visitor.getGraphicsContext().save();

                visitor.getGraphicsContext().setLineWidth(2);
                visitor.getGraphicsContext().setStroke(sC);

                // visitor.getGraphicsContext().setStroke(Color.color(sC.getRed(),
                // sC.getGreen(), sC.getBlue()));

                triangulation.accept(subLayers, visitor);
                visitor.visit(triangulation.getPolygon());

                visitor.getGraphicsContext().restore();

                countStroboscope = (countStroboscope + 1) % colorStrop.length;
            }

        }

        //

        /*
         * if (actualType == Polygon.Type.Monotone) {
         * 
         * // final Segment sToOrigin = new Segment(); for (Segment tf :
         * triangulationFusion) { sToOrigin.set(tf);
         * as.convertToStandard(sToOrigin.a); as.convertToStandard(sToOrigin.b);
         * visitor.visit_unit(sToOrigin); }
         * 
         * }
         * 
         * // // visitor.drawEdgeTipFromList(poly, poly.perimeter);
         */

    }

    /*
     * int countStroboscope = 0; Color colorStrop[] = { Color.CORAL,
     * Color.BLUEVIOLET, Color.MEDIUMSPRINGGREEN, Color.HOTPINK,
     * Color.YELLOWGREEN, Color.FIREBRICK, Color.GREEN };
     * 
     * mutableVisitorForDebugging = visitor;
     * 
     * makeMonotisation(); drawVertexInformType();
     * 
     * if (haveMake) {
     * 
     * final Segment sToOrigin = new Segment(); for (Segment s :
     * bordersBySegment) { sToOrigin.set(s); as.convertToStandard(sToOrigin.a);
     * as.convertToStandard(sToOrigin.b); visitor.visit_unit(sToOrigin); }
     * 
     * visitor.getGraphicsContext().save();
     * visitor.getGraphicsContext().setLineWidth(10); for (MonotonePolygon mp :
     * mp_v) { Color sC = colorStrop[countStroboscope];
     * mutableVisitorForDebugging.getGraphicsContext().setStroke(Color.color(sC.
     * getRed(), sC.getGreen(), sC.getBlue(), 0.2)); visitor.visit(mp);
     * countStroboscope = (countStroboscope + 1) % colorStrop.length; }
     * System.out.println("NbPolygon(" + mp_v.size() + ")");
     * visitor.getGraphicsContext().restore();
     * 
     */

    @Override
    public int hashCode() {
        return monotisationAlgorithm.hashCode();
    }

    /* ************** */

    public boolean isMonotonized() {
        makeTriangulationForMonotisation();
        return haveMonotized;
    }

    public Vector<MonotonePolygon> getMonotonesPolygon() {
        makeTriangulationForMonotisation();
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

    protected void makeTriangulationForMonotisation() {

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
