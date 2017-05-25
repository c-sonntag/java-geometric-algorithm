package at.u4a.geometric_algorithms.gui.layer;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import at.u4a.geometric_algorithms.geometric.Point;
import at.u4a.geometric_algorithms.gui.element.DrawerScene;
import at.u4a.geometric_algorithms.gui.element.InterfaceLayerAction;
import at.u4a.geometric_algorithms.gui.element.LayerTree;
import at.u4a.geometric_algorithms.gui.layer.AbstractLayer.AuthorizedAction;
import at.u4a.geometric_algorithms.utils.GraphToTests;

public class LayerManager implements Iterable<AbstractLayer> {

    private final Vector<AbstractLayer> layers = new Vector<AbstractLayer>();
    LayerTree controllerTree = null;

    private final InterfaceLayerAction la;

    // private AbstractLayer selectedLayer = null;

    private AbstractList<AbstractLayer> selectedLayers = new Vector<AbstractLayer>();

    /* */

    private final DrawerScene ds;

    public LayerManager(DrawerScene ds) {
        this.ds = ds;
        this.la = ds.getLayerAction();

        //
        la.addLayerActionListenerOfDelete(() -> {
            if (!selectedLayers.isEmpty())
                deleteLayer(selectedLayers);
        });

        //
        la.addLayerActionListenerOfDeleteAlgorithm(() -> {
            if (!selectedLayers.isEmpty())
                deleteAlgorithmLayer(selectedLayers);
        });

    }

    /* */

    public AbstractLayer[] getSelectedLayersArray() {
        return (AbstractLayer[]) selectedLayers.toArray();
    }

    /* */

    public void addLayer(AbstractLayer... layer) {
        for (AbstractLayer al : layer) {
            layers.add(al);
        }
        refresh();
    }

    public void addLayerAndSelectIt(AbstractLayer... layer) {

        //
        selectedLayers.clear();

        //
        for (AbstractLayer al : layer) {
            layers.add(al);
            selectedLayers.add(al);
        }

        refresh();
        ds.refresh();
    }

    public void refreshLayerAction() {
        la.setActiveLayers(selectedLayers);
    }

    public void refreshLayerTree() {
        if (controllerTree != null)
            controllerTree.selectNodes(selectedLayers);
    }

    public void reloadLayerTree() {
        layerTree_blockIt();
        if (controllerTree != null)
            controllerTree.reload();
        layerTree_unblockIt();

        refreshLayerTree();
    }

    public void refresh() {

        /*
         * if (controllerTree != null) { AbstractLayer oldSelectedLayer =
         * selectedLayer; controllerTree.reload(); if (oldSelectedLayer != null)
         * setSelectedLayer(oldSelectedLayer); }
         */

        //
        reloadLayerTree();
        refreshLayerAction();
    }

    public void clear() {
        layers.clear();

        GraphToTests.defaultGraph(ds.getLayerManager()); /** @todo */

        refresh();
        ds.refresh();
        setSelectedLayer();
    }

    /* */

    public Iterator<AbstractLayer> iterator() {
        return layers.iterator();
    }

    // public AbstractLayer getAddLayer() {
    // return selectedLayer;
    // }

    /* */

    public AbstractList<AbstractLayer> getSelectedLayers() {
        return selectedLayers;
    }

    public void setSelectedLayer(AbstractLayer... layers) {
        //
        selectedLayers.clear();
        for (AbstractLayer al : layers)
            selectedLayers.add(al);

        //
        refreshLayerAction();
        refreshLayerTree();
    }

    ///////

    private class AutoLayerSelector {

        int findMinLayerIndex = -1;

        public void check(AbstractLayer al) {
            int findLayerIndex = layers.indexOf(al);
            if ((findMinLayerIndex == -1) || (findLayerIndex < findMinLayerIndex))
                findMinLayerIndex = findLayerIndex;
        }

        public AbstractLayer getUpperLayer() {
            if ((findMinLayerIndex >= 0) && !layers.isEmpty()) {
                if ((findMinLayerIndex >= layers.size()))
                    return layers.lastElement();
                else
                    return layers.get(findMinLayerIndex);
            }
            return null;
        }

        public void autoAddUpperLayer() {
            AbstractLayer al = getUpperLayer();
            if (al != null)
                selectedLayers.add(al);
        }

    }

    /////

    public void deleteLayer(AbstractList<AbstractLayer> toDeletes) {

        AutoLayerSelector als = new AutoLayerSelector();

        for (AbstractLayer l : toDeletes) {

            if (!l.getAuthorized().contains(AuthorizedAction.Delete))
                continue;

            als.check(l);
            layers.remove(l);
        }

        //
        selectedLayers.removeAll(toDeletes);

        // Keep emplacement
        als.autoAddUpperLayer();

        //
        refresh();
        ds.refresh();
    }

    /*
     * private void deleteLayer(AbstractLayer l) {
     * 
     * // if (selectedLayers.contains(l)) { selectedLayers.remove(l); }
     * 
     * // int findLayerIndex = layers.indexOf(l); layers.remove(l);
     * 
     * // Keep emplacement AbstractLayer newSelected = null; if ((findLayerIndex
     * < layers.size()) && (findLayerIndex >= 0)) newSelected =
     * layers.get(findLayerIndex); else if (!layers.isEmpty()) newSelected =
     * layers.lastElement(); selectedLayers.add(newSelected); }
     */

    public void deleteAlgorithmLayer(AbstractList<AbstractLayer> toDeletes) {

        AutoLayerSelector als = new AutoLayerSelector();

        AbstractList<AbstractLayer> addSelectedLayers = new Vector<AbstractLayer>();

        for (AbstractLayer l : toDeletes) {

            if (!l.getAuthorized().contains(AuthorizedAction.DeleteAlgorithm))
                continue;

            //
            AbstractList<AbstractLayer> subLayers = l.getSubLayer();

            //
            final boolean noChilds = (subLayers == null) ? true : subLayers.isEmpty();

            if (noChilds) {
                als.check(l);
                layers.remove(l);
                continue;
            }

            //
            int findLayerIndex = layers.indexOf(l);
            layers.remove(l);

            //
            int decCount = 0;
            for (AbstractLayer al : subLayers) {
                al.restoreAuthorization();
                layers.insertElementAt(al, findLayerIndex + decCount);
                decCount++;
            }

            //
            for (AbstractLayer al : subLayers)
                addSelectedLayers.add(al);

        }

        //
        selectedLayers.removeAll(toDeletes);

        // Keep emplacement
        if (addSelectedLayers.isEmpty())
            als.autoAddUpperLayer();
        else
            selectedLayers.addAll(addSelectedLayers);

        //
        refresh();
        ds.refresh();
    }

    public void remplaceLayer(AbstractList<AbstractLayer> alRem_l, AbstractLayer newLayer) {

        //
        Iterator<AbstractLayer> alRem_it = alRem_l.iterator();
        if (alRem_it.hasNext()) {
            int minId = layers.indexOf(alRem_it.next());
            while (alRem_it.hasNext()) {
                int otherMinId = layers.indexOf(alRem_it.next());
                if (otherMinId < minId)
                    minId = otherMinId;
            }
            layers.insertElementAt(newLayer, minId);
        } else
            layers.add(newLayer);

        //
        layers.removeAll(alRem_l);
        selectedLayers.removeAll(alRem_l);

        //
        refresh();
        ds.refresh();

    }

    public void remplaceLayer(AbstractLayer findLayer, AbstractLayer newLayer) {

        //
        int findLayerIndex = layers.indexOf(findLayer);
        if (findLayerIndex >= 0)
            layers.set(findLayerIndex, newLayer);

        //
        selectedLayers.clear();

        //
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
     * Attention de bien gerer les conteneurs Note cela renvo 2 fois les
     * éléments selectionné
     */
    public List<AbstractLayer> getContain(Point p) {
        List<AbstractLayer> contains = new ArrayList<AbstractLayer>();

        // Always Selected Layer(s if container) are at the top
        for (AbstractLayer al : selectedLayers) {
            if (al.isActive())
                if (al.contains(p))
                    contains.add(al);
        }

        //
        for (AbstractLayer layer : layers) {
            if (!contains.contains(layer))
                if (layer.isActive())
                    if (layer.contains(p))
                        contains.add(layer);
        }

        //
        return contains;
    }

    public AbstractLayer getTopContainedShape(Point p) {

        // Always Selected Layer(s if container) are get at the top
        for (AbstractLayer al : selectedLayers) {
            if (al.isActive())
                if (al.contains(p))
                    return al;
        }

        //
        for (AbstractLayer layer : layers)
            if (layer.isActive())
                if (layer.contains(p))
                    return layer;

        //
        return null;
    }

    /* */

    /**
     * @todo Touver un moyen plus concret pour determiner la fin du selection
     */

    /*
     * private static long MAX_TIME_TO_SELECT_BY_LAYER_TREE_ALL_LAYER = 100;
     * 
     * private long mutableLastSelection = 0;
     * 
     * public void addSelectedLayer(AbstractLayer node) {
     * 
     * long currentTime = System.currentTimeMillis(); long dec =
     * mutableLastSelection - currentTime;
     * 
     * // if (dec >= MAX_TIME_TO_SELECT_BY_LAYER_TREE_ALL_LAYER) {
     * selectedLayers.clear(); }
     * 
     * // mutableLastSelection = currentTime; selectedLayers.add(node); }
     */

    boolean mutableBlockedActionOfLayerTree = false;

    public void layerTree_blockIt() {
        mutableBlockedActionOfLayerTree = true;
    }

    public void layerTree_unblockIt() {
        mutableBlockedActionOfLayerTree = false;
    }

    /* */

    public void layerTree_addSelectedLayer(AbstractLayer node) {
        if (!mutableBlockedActionOfLayerTree)
            selectedLayers.add(node);
    }

    public void layerTree_clearSelectedLayers() {
        if (!mutableBlockedActionOfLayerTree)
            selectedLayers.clear();
    }

    public void layerTree_refresh() {
        if (!mutableBlockedActionOfLayerTree)
            refreshLayerAction();
    }

}
