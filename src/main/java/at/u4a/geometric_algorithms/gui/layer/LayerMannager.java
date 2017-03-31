package at.u4a.geometric_algorithms.gui.layer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import at.u4a.geometric_algorithms.gui.element.LayerTree;

public class LayerMannager implements Iterable<AbstractLayer> {

    private final Vector<AbstractLayer> layers = new Vector<AbstractLayer>();
    LayerTree controllerTree = null;

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
        //if (controllerTree != null)
        //    controllerTree.reload();
        refresh();
    }

    public void refresh() {
        if (controllerTree != null)
            controllerTree.reload();
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

    public void setControllerTree(LayerTree controllerTree) {
        this.controllerTree = controllerTree;
    }

    public Vector<AbstractLayer> getLayers() {
        return layers;
    }

}
