package at.u4a.geometric_algorithms.algorithm;

import java.util.List;
import java.util.Vector;

import at.u4a.geometric_algorithms.algorithm.AlgorithmBuilderInterface;
import at.u4a.geometric_algorithms.geometric.*;
import at.u4a.geometric_algorithms.graphic_visitor.InterfaceShapePainterVisitor;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;
import at.u4a.geometric_algorithms.gui.layer.AlgorithmLayer;

/**
 * TriangulationOfDelaunay
 * 
 * @author Hip
 *
 */
class Triangulation extends AbstractAlgorithm {

    public static class Builder implements AlgorithmBuilderInterface {

        @Override
        public String getName() {
            return Triangulation.class.getSimpleName();
        }

        @Override
        public boolean canApply(AbstractLayer l) {
            return (l.getShape() instanceof Polygon);
        }

        static int TriangulationCount = 1;

        @Override
        public AbstractLayer builder(AbstractLayer l) {
            AbstractLayer al = new AlgorithmLayer<Triangulation>(new Triangulation(), Algorithm.Triangulation, l);
            al.setLayerName("r" + String.valueOf(TriangulationCount));
            TriangulationCount++;
            return al;
        }

    };

    public Triangulation() {
        System.out.println("New Triangulation !");
    }

    @Override
    public void accept(Vector<AbstractLayer> v, InterfaceShapePainterVisitor visitor) {
        Polygon p = (Polygon) v.firstElement().getShape();

        // Rectangle r =

        visitor.visit(new Rectangle(new Point(20, 20), new Point(40, 50)));

    }

};
