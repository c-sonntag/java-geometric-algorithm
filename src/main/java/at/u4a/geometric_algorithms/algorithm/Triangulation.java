package at.u4a.geometric_algorithms.algorithm;

import java.util.List;

import at.u4a.geometric_algorithms.algorithm.AlgorithmBuilderInterface;
import at.u4a.geometric_algorithms.geometric.*;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer;

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

        @Override
        public AbstractLayer builder(AbstractLayer l) {
            return new Triangulation();
        }

    };

    public Triangulation() {
        System.out.println("New Triangulation !");
    }

    @Override
    public void accept(List<AbstractLayer> l) {
        // TODO Auto-generated method stub
        
    }

};
