package at.u4a.geometric_algorithms.gui.layer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.gui.element.DrawerScene;
import at.u4a.geometric_algorithms.gui.element.InterfaceLayerAction;
import at.u4a.geometric_algorithms.gui.element.LayerTree;

public class LayerManager implements Iterable<AbstractLayer> {

    private final Vector<AbstractLayer> layers = new Vector<AbstractLayer>();
    LayerTree controllerTree = null;

    private final InterfaceLayerAction la;

    private AbstractLayer selectedLayer = null;

    /* */

    private final DrawerScene ds;

    public LayerManager(DrawerScene ds) {
        this.ds = ds;
        this.la = ds.getLayerAction();

        //
        la.addLayerActionListenerOfDelete(() -> {
            if (selectedLayer != null)
                deleteLayer(selectedLayer);
        });

    }

    /* */

    public void addLayer(AbstractLayer layer) {
        layers.add(layer);
        refresh();
    }

    public void refresh() {
        if (controllerTree != null) {
            AbstractLayer oldSelectedLayer = selectedLayer;
            controllerTree.reload();
            if (oldSelectedLayer != null)
                setSelectedLayer(oldSelectedLayer);
        }
    }

    public void clear() {
        layers.clear();
        refresh();
        ds.refresh();
        setSelectedLayer(null);
    }

    /* */

    public Iterator<AbstractLayer> iterator() {
        return layers.iterator();
    }

    // public AbstractLayer getAddLayer() {
    // return selectedLayer;
    // }

    /* */

    public AbstractLayer getSelectedLayer() {
        return selectedLayer;
    }

    public void setSelectedLayer(AbstractLayer l) {
        selectedLayer = l;
        //
        if (controllerTree != null)
            controllerTree.selectNode(l);
        //
        la.setDelete(selectedLayer != null);

    }

    public void deleteLayer(AbstractLayer l) {
        if (selectedLayer == l)
            setSelectedLayer(null);
        layers.remove(l);
        refresh();
        ds.refresh();
    }

    /* */

    public void setControllerTree(LayerTree controllerTree) {
        this.controllerTree = controllerTree;
    }

    /* */

    public Vector<AbstractLayer> getLayers() {
        return layers;
    }

    /* */

    /**
     * Crée une liste des AbstractLayer qui contiennent un element en point p
     * Attention de bien gerer les conteneurs
     */
    public List<AbstractLayer> getContain(Point p) {
        List<AbstractLayer> contains = new ArrayList<AbstractLayer>();

        // Always Selected Layer(s if container) are at the top
        if (selectedLayer != null)
            if (selectedLayer.contains(p))
                contains.add(selectedLayer);

        //
        for (AbstractLayer layer : layers) {
            if (layer.contains(p))
                contains.add(layer);
        }

        //
        return contains;
    }

    public AbstractLayer getTopContainedShape(Point p) {

        // Always Selected Layer(s if container) are get at the top
        if (selectedLayer != null)
            if (selectedLayer.isActive())
                if (selectedLayer.contains(p))
                    return selectedLayer;

        //
        for (AbstractLayer layer : layers)
            if (layer.isActive())
                if (layer.contains(p))
                    return layer;

        //
        return null;
    }

}
