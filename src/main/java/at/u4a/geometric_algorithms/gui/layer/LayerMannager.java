package at.u4a.geometric_algorithms.gui.layer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LayerMannager implements Iterable<AbstractLayer> {

    private final List<AbstractLayer> layers = new ArrayList<AbstractLayer>();

    private AbstractLayer selectedLayer = null;

    /**
     * Crée une liste des AbstractLayer qui contiennent un element en point p
     * Attention de bien gerer les conteneurs
     * 
     * @return
     */
    public List<AbstractLayer> getContain() {
        // List<AbstractLayer> contain = new ArrayList<AbstractLayer>(); /**
        // TODO gerer le cast des LayerContener */
        // for (int i = shapes.size(); i >= 0; i--) {
        // Shape shape = shapes.get(i);
        // if (shape.contains(x, y))
        // return shape;
        // }
        return null;
    }

    public void addLayer(AbstractLayer layer) {
        layers.add(layer);
    }

    public Iterator<AbstractLayer> iterator() {
        return layers.iterator();
    }

    public AbstractLayer getAddLayer() {
        return selectedLayer;
    }

    public AbstractLayer getSelectedLayer() {
        return selectedLayer;
    }

}
